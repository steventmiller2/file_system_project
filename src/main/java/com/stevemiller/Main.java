package com.stevemiller;

import com.stevemiller.data_access.DatabaseManager;
import com.stevemiller.data_access.FileSystemDAO;
import com.stevemiller.data_access.SQLiteDAO;
import com.stevemiller.file_system.FileSystem;
// import com.stevemiller.authentication.Authentication;
// import com.stevemiller.authentication.User;
import com.stevemiller.user_interface.FileSystemCLI;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseManager.getConnection();

            //Test the connection
            if (conn != null) {
                System.out.println("SQLite connection is successful!");
            } else {
                System.out.println("Failed to connect to the database.");
            }

            FileSystemDAO fileSystemDAO = new SQLiteDAO(conn);
            // get FileSystem instance
            FileSystem fileSystem = FileSystem.getInstance(fileSystemDAO);

            // Request Login to get User
            // Authentication auth = new Authentication();
            // User user = auth.login();

            // Initialize and run User Interface
            FileSystemCLI ui = new FileSystemCLI(fileSystem);
            ui.start();

            DatabaseManager.closeConnection();
        } catch (Exception e) {
            System.err.println("General error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}