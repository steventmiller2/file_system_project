package com.stevemiller.data_access;

import java.util.List;

import com.stevemiller.file_system.Directory;
import com.stevemiller.file_system.FileSystemEntry;

public interface FileSystemDAO {
    int getPermissions(String entryName, int parentDirectoryId);
    void updatePermissions(String entryName, int permissions, int parentDirectoryId);
    void createFile(FileSystemEntry file, int parentDirectoryId);
    void createDirectory(FileSystemEntry directory, int parentDirectoryId);
    String printCurrentDirectoryPath(int currentDirectoryId);
    Directory getChildDirectory(String dirName, int currentDirectoryId);
    Directory getParentDirectory(int parentDirectoryId);
    FileSystemEntry readFile(String fileName, int parentDirectoryId);
    void updateFile(String fileName, String newContent, int parentDirectoryId);
    void deleteFile(String fileName, int parentDirectoryId);
    void deleteDirectory(String directoryName, int parentDirectoryId);
    List<FileSystemEntry> getFilesInDirectory(int currentDirectoryId);
    List<FileSystemEntry> getSubDirectories(int currentDirectoryId);
}
