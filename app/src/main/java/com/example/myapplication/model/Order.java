package com.example.myapplication.model;

public class Order {
    private int orderId;
    private int userId;
    private int addressId;
    private double totalAmount;
    private int paymentMethod;
    private String orderDate;
    private String status;
    private String note;
    private String deliveryDate;

    public Order(int orderId, int userId, int addressId, double totalAmount, int paymentMethod, String orderDate, String status, String note, String deliveryDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.status = status;
        this.note = note;
        this.deliveryDate = deliveryDate;
    }

    // Getter methods
    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public int getAddressId() {
        return addressId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    // Setter methods
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
