package com.stevemiller.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_PATH = "resource:databases/connection.db";
    private static Connection conn = null;

    // static {
    //     // Load the SQLite JDBC driver explicitly
    //     try {
    //         Class.forName("org.sqlite.JDBC");
    //     } catch (ClassNotFoundException e) {
    //         System.err.println("Error loading SQLite JDBC driver: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    // Private constructor to prevent instantiation from outside
    private DatabaseManager() {}

    // Method to get the instance of the connection
    public static Connection getConnection() {
        if (conn == null) {
            try {
                // Establish a connection to the database
                String url = "jdbc:sqlite::" + DB_PATH;
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return conn;
    }

    // Method to close the connection (call this when the application is closing)
    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
