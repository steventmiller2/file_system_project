package com.stevemiller.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.stevemiller.authentication.User;

public class SQLiteUserDAO implements UserDAO {
    private Connection connection;

    public SQLiteUserDAO(Connection connection) {
        this.connection = connection;
        initializeUserTable();
        insertDefaultUsers();
    }

    private void initializeUserTable() {
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL, " +
            "password TEXT NOT NULL," +
            "admin BOOLEAN" + 
            ");";

        try (PreparedStatement stmt = connection.prepareStatement(createUserTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating user table: " + e.getMessage());
        }
    }

    private void insertDefaultUsers() {
        String insertAdminUserSQL = "INSERT OR IGNORE INTO user (username, password, admin) VALUES ('admin', 'admin', 1);";
        String insertUser1SQL = "INSERT OR IGNORE INTO user (username, password, admin) VALUES ('user1', 'password1', 0);";
        String insertUser2SQL = "INSERT OR IGNORE INTO user (username, password, admin) VALUES ('user2', 'password2', 0);";

        try (PreparedStatement stmt1 = connection.prepareStatement(insertAdminUserSQL);
            PreparedStatement stmt2 = connection.prepareStatement(insertUser1SQL);
            PreparedStatement stmt3 = connection.prepareStatement(insertUser2SQL)) {
            stmt1.execute();
            stmt2.execute();
            stmt3.execute();
        } catch (SQLException e) {
            System.err.println("Error inserting default users: " + e.getMessage());
        }
    }

    @Override
    public User authenticateUser(String username, String password) {
        String selectUserSQL = "SELECT * FROM user WHERE username = ? AND password = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(selectUserSQL)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                boolean admin = resultSet.getBoolean("admin");

                return new User(username, id, admin);
            } else {
                return null; // Authentication failed
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return null; // Error occurred during authentication
        }
    }
}
