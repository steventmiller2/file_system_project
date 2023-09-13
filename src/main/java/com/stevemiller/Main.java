package com.stevemiller;

import com.stevemiller.authentication.UserSessionManager;
import com.stevemiller.data_access.DatabaseManager;
import com.stevemiller.data_access.FileSystemDAO;
import com.stevemiller.data_access.SQLiteFileSystemDAO;
import com.stevemiller.data_access.UserDAO;
import com.stevemiller.data_access.SQLiteUserDAO;
import com.stevemiller.file_system.FileSystem;
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

            UserDAO userDAO = new SQLiteUserDAO(conn);
            // Request Login to get User
            UserSessionManager session = new UserSessionManager(userDAO);
            session.authenticateUser();

            FileSystemDAO fileSystemDAO = new SQLiteFileSystemDAO(conn, session);
            // get FileSystem instance
            FileSystem fileSystem = FileSystem.getInstance(fileSystemDAO);

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