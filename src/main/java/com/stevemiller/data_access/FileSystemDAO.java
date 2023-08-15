package com.stevemiller.data_access;

import java.util.List;

import com.stevemiller.file_system.Directory;
import com.stevemiller.file_system.FileSystemEntry;

public interface FileSystemDAO {
    void createFile(FileSystemEntry file, int parentDirectoryId);
    void createDirectory(FileSystemEntry directory, int parentDirectoryId);
    Directory printCurrentDirectory(int currentDirectoryId);
    Directory getChildDirectory(String dirName, int currentDirectoryId);
    Directory getParentDirectory(int parentDirectoryId);
    FileSystemEntry readFile(String fileName);
    void updateFile(String fileName, String newContent);
    void deleteFile(String fileName);
    void deleteDirectory(String directoryName);
    List<FileSystemEntry> getFilesInDirectory(int currentDirectoryId);
    List<FileSystemEntry> getSubDirectories(int currentDirectoryId);
}
