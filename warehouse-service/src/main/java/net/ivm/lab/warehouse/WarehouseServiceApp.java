package net.ivm.lab.warehouse;

public class WarehouseServiceApp {
    public static void main(String[] args) {
        new WarehouseService(Configuration.defaultLocalhost()).start();
    }
}
