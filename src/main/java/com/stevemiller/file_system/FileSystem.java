package com.stevemiller.file_system;

import com.stevemiller.data_access.FileSystemDAO;

import java.util.List;

/**
 * YOUTUBE VIDEOS:
 * https://www.youtube.com/watch?v=gswVHTrRX8I
 * https://www.youtube.com/watch?v=4tGPKofmurk
 * https://www.youtube.com/watch?v=MaHxZEBRcT4
 */

public class FileSystem {
    private static FileSystem instance = null;
    private Directory currentDirectory;
    private FileSystemDAO fileSystemDAO;

    // private constructor to enforce Singleton pattern
    private FileSystem(FileSystemDAO fileSystemDAO) {
        this.fileSystemDAO = fileSystemDAO;
        this.currentDirectory = new Directory("root", -1, 1);
        createDirectory(this.currentDirectory, -1);
    }

    public static FileSystem getInstance(FileSystemDAO fileSystemDAO) {
        if (instance == null) {
            instance = new FileSystem(fileSystemDAO);
        }
        return instance;
    }

    public void createFile(File file) {
        fileSystemDAO.createFile(file, getCurrentDirectory().getId());
    }

    public void createDirectory(Directory directory) {
        fileSystemDAO.createDirectory(directory, getCurrentDirectory().getId());
    }

    private void createDirectory(Directory directory, int rootParentDirectoryId) {
        fileSystemDAO.createDirectory(directory, rootParentDirectoryId);
    }

    public FileSystemEntry readFile(String fileName) {
        return fileSystemDAO.readFile(fileName);
    }

    public void updateFile(String fileName, String newContent) {
        fileSystemDAO.updateFile(fileName, newContent);
    }

    public void deleteFile(String fileName) {
        fileSystemDAO.deleteFile(fileName);
    }

    public void deleteDirectory(String directoryName) {
        fileSystemDAO.deleteDirectory(directoryName);
    }

    public List<FileSystemEntry> getFilesInDirectory() {
        return fileSystemDAO.getFilesInDirectory(getCurrentDirectory().getId());
    }

    public List<FileSystemEntry> getSubDirectories() {
        return fileSystemDAO.getSubDirectories(getCurrentDirectory().getId());
    }

    public Directory printCurrentDirectory() {
        return fileSystemDAO.printCurrentDirectory(getCurrentDirectory().getId());
    }

    public void changeDirectory(String dirName) {
        Directory dir;
        if (dirName.equals("..")) {
            dir = fileSystemDAO.getParentDirectory(getCurrentDirectory().getParentDirectoryId());
        } else {
            dir = fileSystemDAO.getChildDirectory(dirName, getCurrentDirectory().getId());
        }
        if (dir != null) {
            this.currentDirectory = dir;
        }
    }

    public Directory getCurrentDirectory() {
        return this.currentDirectory;
    }

}
