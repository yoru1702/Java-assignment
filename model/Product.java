package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Product {
    private String id;
    private String name;
    private double price; // ราคาขาย
    private double costPrice; // ราคาทุน
    private int stock;
    private LocalDateTime importedAt; // เวลานำเข้า

    public Product(String id, String name, double price, double costPrice, int stock, LocalDateTime importedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
        this.importedAt = importedAt;
    }

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public int getStock() {
        return stock;
    }

    public LocalDateTime getImportedAt() {
        return importedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImportedAt(LocalDateTime importedAt) {
        this.importedAt = importedAt;
    }

    // ในไฟล์ Product.java
    public String toCSV() {
        String dateStr = (importedAt != null) ? importedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "null";
        return String.format("%s,%s,%.2f,%.2f,%d,%s",
                id, name, price, costPrice, stock, dateStr);
    }
}