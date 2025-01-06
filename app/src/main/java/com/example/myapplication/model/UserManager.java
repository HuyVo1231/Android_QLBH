package com.example.myapplication.model;

public class UserManager {
    private static UserManager instance;
    private User currentUser;

    private UserManager() {}

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // Lưu thông tin người dùng sau khi đăng nhập
    public void setUser(User user) {
        this.currentUser = user;
    }

    // Lấy thông tin người dùng hiện tại
    public User getUser() {
        return currentUser;
    }

    // Xóa thông tin người dùng (khi logout)
    public void clearUser() {
        currentUser = null;
    }
}
