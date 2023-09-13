package com.stevemiller.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stevemiller.authentication.UserSessionManager;
import com.stevemiller.file_system.Directory;
import com.stevemiller.file_system.File;
import com.stevemiller.file_system.FileSystemEntry;

public class SQLiteFileSystemDAO implements FileSystemDAO {
    private Connection connection;
    private UserSessionManager userSession;

    public SQLiteFileSystemDAO(Connection connection, UserSessionManager userSession) {
        this.connection = connection;
        this.userSession = userSession;
        initializeDatabase();
    }
    private void initializeDatabase() {
        String createFileTableSQL = "CREATE TABLE IF NOT EXISTS file (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "content TEXT, " +
                "parent_directory_id INTEGER NOT NULL, " +
                "owner_id INTEGER NOT NULL, " +
                "permissions BOOLEAN" +
                ");";
    
        String createDirectoryTableSQL = "CREATE TABLE IF NOT EXISTS directory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "parent_directory_id INTEGER," +
                "owner_id INTEGER NOT NULL, " +
                "permissions BOOLEAN" +
                ");";

        try (PreparedStatement stmt1 = connection.prepareStatement(createFileTableSQL);
             PreparedStatement stmt2 = connection.prepareStatement(createDirectoryTableSQL)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private boolean hasPermission(String tableName, String entryName, int parentDirectoryId) {
        if (userSession.getLoggedInUser().isAdmin()) {
            return true; // Admins have permission to do anything
        } else {
            String selectPermissionSQL = "SELECT permissions FROM " + tableName + " WHERE name = ? AND parent_directory_id = ?;";

            try (PreparedStatement stmt = connection.prepareStatement(selectPermissionSQL)) {
                stmt.setString(1, entryName);
                stmt.setInt(2, parentDirectoryId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int permissions = resultSet.getInt("permissions");
                int owner_id = resultSet.getInt("owner_id");
                return permissions == 1 || userSession.getLoggedInUser().getUserId() == owner_id;
            }
        } catch (SQLException e) {
            System.err.println("Error checking permissions: " + e.getMessage());
        }

        return false; // Entry not found or error occurred
        }
    }

    private boolean hasFilePermission(String fileName, int parentDirectoryId) {
        return hasPermission("file", fileName, parentDirectoryId);
    }
    
    private boolean hasDirectoryPermission(String dirName, int parentDirectoryId) {
        return hasPermission("directory", dirName, parentDirectoryId);
    }
    
    @Override
    public int getPermissions(String entryName, int parentDirectoryId) {
        String selectPermissionsFileSQL = "SELECT permissions FROM file WHERE name = ? AND parent_directory_id = ?;";
        String selectPermissionsDirSQL = "SELECT permissions FROM directory WHERE name = ? AND parent_directory_id = ?;";
    
        try (PreparedStatement stmtFile = connection.prepareStatement(selectPermissionsFileSQL);
             PreparedStatement stmtDir = connection.prepareStatement(selectPermissionsDirSQL)) {
            
            stmtFile.setString(1, entryName);
            stmtFile.setInt(2, parentDirectoryId);
    
            ResultSet resultSetFile = stmtFile.executeQuery();
            if (resultSetFile.next()) {
                return resultSetFile.getInt("permissions");
            } else {
                stmtDir.setString(1, entryName);
                stmtDir.setInt(2, parentDirectoryId);
    
                ResultSet resultSetDir = stmtDir.executeQuery();
                if (resultSetDir.next()) {
                    return resultSetDir.getInt("permissions");
                }
            }
    
        } catch (SQLException e) {
            System.err.println("Error getting permissions: " + e.getMessage());
        }
        System.out.println("File/directory doesn't exist.");
        return -1; // Entry not found or error occurred
    }

    @Override
    public void updatePermissions(String entryName, int parentDirectoryId, int permissions) {
        String updatePermissionsFileSQL = "UPDATE file SET permissions = ? WHERE name = ? AND parent_directory_id = ?;";
        String updatePermissionsDirSQL = "UPDATE directory SET permissions = ? WHERE name = ? AND parent_directory_id = ?;";
    
        try (PreparedStatement stmtFile = connection.prepareStatement(updatePermissionsFileSQL);
             PreparedStatement stmtDir = connection.prepareStatement(updatePermissionsDirSQL)) {
            
            stmtFile.setInt(1, permissions);
            stmtFile.setString(2, entryName);
            stmtFile.setInt(3, parentDirectoryId);
            int affectedRowsFile = stmtFile.executeUpdate();
    
            if (affectedRowsFile == 0) {
                stmtDir.setInt(1, permissions);
                stmtDir.setString(2, entryName);
                stmtDir.setInt(3, parentDirectoryId);
                int affectedRowsDir = stmtDir.executeUpdate();
    
                if (affectedRowsDir == 0) {
                    System.out.println("File/directory doesn't exist.");
                } else {
                    System.out.println("Permissions updated successfully.");
                }
            } else {
                System.out.println("Permissions updated successfully.");
            }
    
        } catch (SQLException e) {
            System.err.println("Error updating permissions: " + e.getMessage());
        }
    }
    
    @Override
    public void createFile(FileSystemEntry file, int parentDirectoryId) {
        String insertFileSQL = "INSERT INTO file (name, content, parent_directory_id, owner_id, permissions) VALUES (?, ?, ?, ?, 0);";
    
        try (PreparedStatement stmt = connection.prepareStatement(insertFileSQL)) {
            stmt.setString(1, file.getName());
            stmt.setString(2, ((File) file).getContent()); // Cast to File and get content
            stmt.setInt(3, parentDirectoryId);
            stmt.setInt(4, userSession.getLoggedInUser().getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }

    @Override
    public void createDirectory(FileSystemEntry directory, int parentDirectoryId) {
        String insertDirectorySQL = "INSERT INTO directory (name, parent_directory_id, owner_id, permissions) VALUES (?, ?, ?, 0);";

        try (PreparedStatement stmt = connection.prepareStatement(insertDirectorySQL)) {
            stmt.setString(1, directory.getName());
            stmt.setInt(2, parentDirectoryId);
            stmt.setInt(3, userSession.getLoggedInUser().getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating directory: " + e.getMessage());
        }
    }

    @Override
    public String printCurrentDirectoryPath(int currentDirectoryId) {
        String selectDirectorySQL = "SELECT name, parent_directory_id FROM directory WHERE id = ?;";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectDirectorySQL)) {
            stmt.setInt(1, currentDirectoryId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int parentDirId = resultSet.getInt("parent_directory_id");
                
                if (parentDirId == -1) {
                    // This is the root directory, so return its name only
                    return name;
                } else {
                    // Recursively get the parent directory path
                    String parentDirPath = printCurrentDirectoryPath(parentDirId);
                    return parentDirPath + ">" + name;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading directory: " + e.getMessage());
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
    public FileSystemEntry readFile(String fileName, int parentDirectoryId) {
        String selectFileSQL = "SELECT name, content, parent_directory_id FROM file WHERE name = ? AND parent_directory_id = ?;";
    
        try (PreparedStatement stmt = connection.prepareStatement(selectFileSQL)) {
            stmt.setString(1, fileName);
            stmt.setInt(2, parentDirectoryId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String content = resultSet.getString("content");
                return new File(fileName, content, parentDirectoryId);
            } else {
                System.err.println("File not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateFile(String fileName, String newContent, int parentDirectoryId) {
        if (hasFilePermission(fileName, parentDirectoryId)) {
            String updateFileSQL = "UPDATE file SET content = ? WHERE name = ? AND parent_directory_id = ?;";

            try (PreparedStatement stmt = connection.prepareStatement(updateFileSQL)) {
                stmt.setString(1, newContent);
                stmt.setString(2, fileName);
                stmt.setInt(3, parentDirectoryId);
                stmt.executeUpdate();
                System.out.println("File updated successfully.");
            } catch (SQLException e) {
                System.err.println("Error updating file: " + e.getMessage());
            }
        } else {
            System.out.println("Permission denied or file not found.");
        }
    }

    @Override
    public void deleteFile(String fileName, int parentDirectoryId) {
        if (hasFilePermission(fileName, parentDirectoryId)) {
            String deleteFileSQL = "DELETE FROM file WHERE name = ? AND parent_directory_id = ?;";

            try (PreparedStatement stmt = connection.prepareStatement(deleteFileSQL)) {
                stmt.setString(1, fileName);
                stmt.setInt(2, parentDirectoryId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        } else {
            System.out.println("Permission denied or file not found.");
        }
    }

    @Override
    public void deleteDirectory(String directoryName, int parentDirectoryId) {
        if (hasDirectoryPermission(directoryName, parentDirectoryId)) {
            String deleteDirectorySQL = "DELETE FROM directory WHERE name = ? AND parent_directory_id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(deleteDirectorySQL)) {
                stmt.setString(1, directoryName);
                stmt.setInt(2, parentDirectoryId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error deleting directory: " + e.getMessage());
            }
        } else {
            System.out.println("Permission denied or file not found.");
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