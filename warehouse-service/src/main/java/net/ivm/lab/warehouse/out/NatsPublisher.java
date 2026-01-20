package net.ivm.lab.warehouse.out;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NatsPublisher {
    private static final Logger log = LoggerFactory.getLogger(NatsPublisher.class);

    private final Connection connection;
    private final JetStream jetStream;

    private final String messageTopic;

    public NatsPublisher(String messageBrokerUrl, String messageTopic) {
        try {
            this.connection = Nats.connect(messageBrokerUrl);
            this.jetStream = connection.jetStream();
            this.messageTopic = messageTopic;
            log.info("Connected to msg broker at {}, messageTopic={}", messageBrokerUrl, messageTopic);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to NATS: " + e.getMessage(), e);
        }
    }

    public void publish(String message) {
        try {
//            String json = objectMapper.writeValueAsString(data);
//            jetStream.publish(SensorData.MESSAGE_TOPIC, json.getBytes());
            jetStream.publish(messageTopic, message.getBytes());
        } catch (Exception e) {
            log.error("Failed to publish: {}", e.getMessage());
        }
    }
}