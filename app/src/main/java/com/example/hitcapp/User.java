package com.example.hitcapp;

public class User {
    private String email;
    private String password;
    // MockAPI.io sẽ tự động tạo ID, bạn không cần phải thêm nó vào constructor
    // nhưng có thể thêm getter/setter nếu muốn đọc lại từ phản hồi.
    // private String id;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}