package net.ivm.lab.warehouse.model;

public record ThresholdSettings(
        double temperature,
        double humidity
) {
    public double getThresholdFor(SensorType sensorType) {
        return switch (sensorType) {
            case TEMPERATURE -> temperature;
            case HUMIDITY -> humidity;
        };
    }
}
