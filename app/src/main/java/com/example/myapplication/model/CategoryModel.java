package com.example.myapplication.model;

public class CategoryModel {
    private final String idCategory;
    private final String name;
    private final String imageUrl;

    public CategoryModel(String idCategory, String name, String imageUrl) {
        this.idCategory = idCategory;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getIdCategory() {
        return idCategory;
    }
}
