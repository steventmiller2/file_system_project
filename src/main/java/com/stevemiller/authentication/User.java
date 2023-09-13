package com.stevemiller.authentication;

public class User {
    String username;
    int userId;
    Boolean admin;

    public User(String username, int userId, Boolean admin) {
        this.username = username;
        this.userId = userId;
        this.admin = admin;
    }

    public String getUsername() {
      return username;
    }

    public int getUserId() {
      return userId;
    }

    public Boolean isAdmin() {
        return admin;
    }
}