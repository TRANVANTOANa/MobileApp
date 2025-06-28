package com.example.hitcapp;

public class Category {
    private String name;
    private int iconResId; // Resource ID for the icon

    public Category(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}