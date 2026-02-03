package net.ivm.lab.warehouse.model;

public record SensorData(
        String sensorId,
        SensorType sensorType,
        double value
) {
}