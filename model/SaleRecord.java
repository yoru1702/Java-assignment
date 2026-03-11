package model;

public class SaleRecord {

    private String datetime;
    private String productId;
    private String name;
    private int qty;
    private double price;
    private double total;

    public SaleRecord(String datetime, String productId,
                      String name, int qty,
                      double price, double total) {
        this.datetime = datetime;
        this.productId = productId;
        this.name = name;
        this.qty = qty;
        this.price = price;
        this.total = total;
    }

    public String toCSV() {
        return datetime + "," + productId + "," + name + ","
                + qty + "," + price + "," + total;
    }
}
