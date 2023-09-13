package com.stevemiller.authentication;

//singleton Admin 
public class Admin extends User {
    private static Admin instance = null;

    public Admin() {
        super("admin", 1, true);
    }

    public static Admin getInstance(String username, String group) {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

}