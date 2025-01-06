package com.example.myapplication.model;

public class ProductModel {
    private int id;
    private String productCode;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private String categoryId;
    private int soldQuantity;
    private int quantity;

    // Full constructor
    public ProductModel(int id, String productCode, String name, double price, String description, String imageUrl, String categoryId, int soldQuantity, int quantity) {
        this.id = id;
        this.productCode = productCode;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.soldQuantity = soldQuantity;
    }

    // Short constructor
    public ProductModel(String productCode, String name, double price, String description, String imageUrl, String categoryId) {
        this.productCode = productCode;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.soldQuantity = 0;
        this.quantity = 0;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public int getQuantity() {   // Getter cho quantity
        return quantity;
    }

    public void setQuantity(int quantity) {   // Setter cho quantity
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", soldQuantity=" + soldQuantity +
                ", quantity=" + quantity +  // Thêm quantity vào toString()
                '}';
    }
}
