package com.example.myapplication.model;

public class OrderDetail {
    private long id;
    private long orderId;
    private int productId;
    private int quantity;
    private double price;
    private String productName;
    private String productImage;

    public OrderDetail(long id, long orderId, int productId, int quantity, double price, String productName, String productImage) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
        this.productImage = productImage;
    }

    public long getId() {
        return id;
    }

    public long getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }
}
