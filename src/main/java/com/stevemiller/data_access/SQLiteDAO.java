package com.stevemiller.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stevemiller.file_system.Directory;
import com.stevemiller.file_system.File;
import com.stevemiller.file_system.FileSystemEntry;

public class SQLiteDAO implements FileSystemDAO {
    private Connection connection;

    public SQLiteDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }
    private void createTableIfNotExists() {
        String createFileTableSQL = "CREATE TABLE IF NOT EXISTS file (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "content TEXT, " +
                "parent_directory_id INTEGER NOT NULL" +
                ");";

        String createDirectoryTableSQL = "CREATE TABLE IF NOT EXISTS directory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "parent_directory_id INTEGER" +
                ");";

        try (PreparedStatement stmt1 = connection.prepareStatement(createFileTableSQL);
             PreparedStatement stmt2 = connection.prepareStatement(createDirectoryTableSQL)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void createFile(FileSystemEntry file, int parentDirectoryId) {
        String insertFileSQL = "INSERT INTO file (name, content, parent_directory_id) VALUES (?, ?, ?);";
    
        try (PreparedStatement stmt = connection.prepareStatement(insertFileSQL)) {
            stmt.setString(1, file.getName());
            stmt.setString(2, ((File) file).getContent()); // Cast to File and get content
            stmt.setInt(3, parentDirectoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }

    @Override
    public void createDirectory(FileSystemEntry directory, int parentDirectoryId) {
        String insertDirectorySQL = "INSERT INTO directory (name, parent_directory_id) VALUES (?, ?);";

        try (PreparedStatement stmt = connection.prepareStatement(insertDirectorySQL)) {
            stmt.setString(1, directory.getName());
            stmt.setInt(2, parentDirectoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating directory: " + e.getMessage());
        }
    }

    @Override
    public Directory printCurrentDirectory(int currentDirectoryId){
        String selectDirectorySQL = "SELECT name FROM directory WHERE id = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(selectDirectorySQL)) {
            stmt.setInt(1, currentDirectoryId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return new Directory(name);
            }
        } catch (SQLException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Directory getChildDirectory(String dirName, int currentDirectoryId) {
        String selectDirectorySQL = "SELECT id FROM directory WHERE name = ? AND parent_directory_id = ?;";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectDirectorySQL)) {
            stmt.setString(1, dirName);
            stmt.setInt(2, currentDirectoryId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                return new Directory(dirName, currentDirectoryId, id);
            } else {
                System.err.println("Directory not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error changing directory: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Directory getParentDirectory(int parentDirectoryId) {
        String selectDirectorySQL = "SELECT id, name, parent_directory_id FROM directory WHERE id = ?;";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectDirectorySQL)) {
            stmt.setInt(1, parentDirectoryId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int parentDirId = resultSet.getInt("parent_directory_id");
                return new Directory(name, parentDirId, id);
            } else {
                System.err.println("Directory not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error changing directory: " + e.getMessage());
        }
        return null;
    }

    @Override
    public FileSystemEntry readFile(String fileName) {

        String selectFileSQL = "SELECT name, content, parent_directory_id FROM file WHERE name = ?;"; //TODO: add parent_directory_id to WHERE clause
    
        try (PreparedStatement stmt = connection.prepareStatement(selectFileSQL)) {
            stmt.setString(1, fileName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String content = resultSet.getString("content");
                int parentDirectoryId = resultSet.getInt("parent_directory_id");
                return new File(name, content, parentDirectoryId);
            }
        } catch (SQLException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateFile(String fileName, String newContent) {
        String updateFileSQL = "UPDATE file SET content = ? WHERE name = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(updateFileSQL)) {
            stmt.setString(1, newContent);
            stmt.setString(2, fileName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName) {
        String deleteFileSQL = "DELETE FROM file WHERE name = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(deleteFileSQL)) {
            stmt.setString(1, fileName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }

    @Override
    public void deleteDirectory(String directoryName) {
        String deleteDirectorySQL = "DELETE FROM directory WHERE name = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(deleteDirectorySQL)) {
            stmt.setString(1, directoryName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting directory: " + e.getMessage());
        }
    }

    @Override
    public List<FileSystemEntry> getFilesInDirectory(int directoryId) {
        List<FileSystemEntry> files = new ArrayList<>();
    
        String selectFilesSQL = "SELECT name, content, parent_directory_id FROM file WHERE parent_directory_id = ?;";
    
        try (PreparedStatement stmt = connection.prepareStatement(selectFilesSQL)) {
            stmt.setInt(1, directoryId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String content = resultSet.getString("content");
                int parentDirectoryId = resultSet.getInt("parent_directory_id");
                files.add(new File(name, content, parentDirectoryId));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching files in directory: " + e.getMessage());
        }
        return files;
    }


    @Override
    public List<FileSystemEntry> getSubDirectories(int directoryId) {
        List<FileSystemEntry> subDirectories = new ArrayList<>();
    
        String selectSubDirectoriesSQL = "SELECT name, parent_directory_id FROM directory WHERE parent_directory_id = ?;";
    
        try (PreparedStatement stmt = connection.prepareStatement(selectSubDirectoriesSQL)) {
            stmt.setInt(1, directoryId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int parentDirectoryId = resultSet.getInt("parent_directory_id");
                subDirectories.add(new Directory(name, parentDirectoryId));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching subdirectories: " + e.getMessage());
        }
        return subDirectories;
    }
}