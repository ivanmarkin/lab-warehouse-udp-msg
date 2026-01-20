package net.ivm.lab.warehouse.in;

import net.ivm.lab.warehouse.out.NatsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpListener {
    private static final Logger log = LoggerFactory.getLogger(UdpListener.class);

    private final int port;
    private final NatsPublisher natsPublisher;

    public UdpListener(int port, NatsPublisher natsPublisher) {
        this.port = port;
        this.natsPublisher = natsPublisher;
    }

    public void start() {
        Thread.startVirtualThread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                log.info("UDP listening: port={}", port);

                byte[] buffer = new byte[1024];

                while (!socket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    log.info("Received: {}", message);

                    natsPublisher.publish(message);
                }
            } catch (IOException e) {
                log.error("UDP listening: port={}", port);
            }
        });
    }
}