package net.ivm.lab.warehouse.central;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import net.ivm.lab.warehouse.model.SensorData;
import net.ivm.lab.warehouse.model.ThresholdSettings;
import net.ivm.lab.warehouse.util.MessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.ivm.lab.warehouse.central.CentralService.*;

public class MessageProcessor extends AbstractActor {
    static final String START_MESSAGE_PROCESSOR = "StartMessageProcessor";

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final AtomicBoolean started = new AtomicBoolean(false);
    private ThresholdSettings thresholdSettings;

    private Connection natsConnection;
    private Dispatcher natsDispatcher;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(START_MESSAGE_PROCESSOR, msg -> start())
                .build();
    }

    private void start() {
        if (started.get()) {
            log.warning("MessageProcessor {} already started", this);
            return;
        }

        try {
            natsConnection = Nats.connect(NATS_URL);
            natsDispatcher = natsConnection.createDispatcher(this::processNatsMessage);
            natsDispatcher.subscribe(TOPIC_NAME, QUEUE_GROUP);

            thresholdSettings = new ThresholdSettings(TH_TEMPERATURE, TH_HUMIDITY);

            started.set(true);
            log.info("MessageProcessor {} started", this);
        } catch (Exception e) {
            log.error("Failed to connect to NATS: {}", e);
            getContext().stop(getSelf());
        }
    }

    private void processNatsMessage(Message msg) {
        String content = new String(msg.getData(), StandardCharsets.UTF_8);
        try {
            SensorData sensorData = MessageConverter.parseSensorMessage(content);
            double threshold = thresholdSettings.getThresholdFor(sensorData.sensorType());
            if (sensorData.value() > threshold) {
                log.error(String.format("!!!Alarm: exceeded %s threshold of %s. Sensor ID=%s, value=%s",
                        sensorData.sensorType().name(), threshold, sensorData.sensorId(), sensorData.value()));
            }
        } catch (Exception e) {
            log.error("Failed to parse sensor message: {}\n {}", content, e);
        }
    }

    @Override
    public void postStop() {
        clearConnection();
    }

    private void clearConnection() {
        try {
            if (natsDispatcher != null) {
                natsDispatcher.unsubscribe(TOPIC_NAME);
            }
            if (natsConnection != null) {
                natsConnection.close();
            }
        } catch (Exception e) {
            log.error("Error clearing NATS connection: {}", e);
        }
    }
}