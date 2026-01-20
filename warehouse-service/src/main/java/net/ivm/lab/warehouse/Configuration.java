package net.ivm.lab.warehouse;

public record Configuration(
        int portTemperature,
        int portHumidity,
        String messageBrokerUrl,
        String messageTopic
) {
    public static Configuration defaultLocalhost() {
        return new Configuration(3344, 3355,"nats://localhost:4222", "WarehouseSensorDataTopic");
    }
}
