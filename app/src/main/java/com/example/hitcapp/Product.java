package com.example.hitcapp;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private int imageResId; // Resource ID for the product image (e.g., R.drawable.comchien)
    private String description;
    private String categoryName; // THÊM TRƯỜNG NÀY VÀO

    public Product(String id, String name, double price, int imageResId, String description, String categoryName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
        this.categoryName = categoryName; // KHỞI TẠO TRƯỜNG MỚI
    }

    // Constructor cũ nếu bạn vẫn muốn dùng tạm thời
    public Product(String id, String name, double price, int imageResId, String description) {
        this(id, name, price, imageResId, description, "Khác"); // Mặc định là "Khác"
    }

    // Thêm getter và setter cho categoryName
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // Các getter và setter hiện có (giữ nguyên)
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setDescription(String description) { this.description = description; }
}