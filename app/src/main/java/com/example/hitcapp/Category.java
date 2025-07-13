package com.example.hitcapp;

import java.io.Serializable;

public class Category implements Serializable {
    private String name;
    private int iconResId; // Đã đổi tên từ imageResId thành iconResId

    public Category(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() { // Đã đổi tên getter
        return iconResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconResId(int iconResId) { // Đã đổi tên setter
        this.iconResId = iconResId;
    }
}