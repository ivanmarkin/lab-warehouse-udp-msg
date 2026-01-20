package net.ivm.lab.warehouse;

import net.ivm.lab.warehouse.in.UdpServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarehouseService {
    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);

    private final UdpServerContext udpServerContext;

    public WarehouseService(Configuration configuration) {
        this.udpServerContext = new UdpServerContext(configuration);
    }

    public void start() {
        udpServerContext.udpTemperatureListener().start();
        udpServerContext.udpHumidityListener().start();

        log.info("WarehouseService started");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }
}