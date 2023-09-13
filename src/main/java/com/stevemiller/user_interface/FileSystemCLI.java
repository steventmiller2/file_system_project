package com.stevemiller.user_interface;

import java.util.Scanner;
import com.stevemiller.file_system.Directory;
import com.stevemiller.file_system.File;
import com.stevemiller.file_system.FileSystem;
import com.stevemiller.file_system.FileSystemEntry;

public class FileSystemCLI {
    private FileSystem fileSystem;
    private Scanner scanner;

    public FileSystemCLI(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("--Enter command (touch, mkdir, cat, update-file, rm, rmd, ls, pwd, cd, chmod, exit): --");
            String[] input = scanner.nextLine().trim().split(" ", 2);
            String command = input[0].toLowerCase();
            String argument = input.length > 1 ? input[1] : null;
    
            switch (command) {
                case "pwd":
                    printCurrentDirectoryPath();
                    break;
                case "cd":
                    changeDirectory(argument);
                    break;
                case "touch":
                    createFile(argument);
                    break;
                case "mkdir":
                    createDirectory(argument);
                    break;
                case "cat":
                    readFile(argument);
                    break;
                case "update-file":
                    updateFile(argument);
                    break;
                case "rm":
                    deleteFile(argument);
                    break;
                case "rmd":
                    deleteDirectory(argument);
                    break;
                case "ls":
                    listFilesInDirectory();
                    listSubDirectories();
                    break;
                case "chmod":
                    changePermissions(argument);
                    break;
                case "exit":
                    System.out.println("Exiting the File System CLI.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid command. Try again.");
            }
        }
    }

    private void changeDirectory(String dirName) {
        if (dirName != null && !dirName.isEmpty()) {
            fileSystem.changeDirectory(dirName);
        } else {
            System.out.println("Invalid directory name. Try again.");
        }
    }
    
    private void createFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            System.out.println("Enter file content:");
            String content = scanner.nextLine();
            File file = new File(fileName, content);
            fileSystem.createFile(file);
        } else {
            System.out.println("Invalid file name. Try again.");
        }
    }
    
    private void createDirectory(String dirName) {
        if (dirName != null && !dirName.isEmpty()) {
            Directory directory = new Directory(dirName);
            fileSystem.createDirectory(directory);
        } else {
            System.out.println("Invalid directory name. Try again.");
        }
    }

    private void printCurrentDirectoryPath() {
        String path = fileSystem.printCurrentDirectoryPath();

        if (path != null) {
            System.out.println(path);
        }
    }

    private void readFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            FileSystemEntry file = fileSystem.readFile(fileName);
    
            if (file != null) {
                System.out.println("File content:");
                System.out.println(((File) file).getContent());
            }
        } else {
            System.out.println("Invalid file name. Try again.");
        }
    }
    
    private void updateFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            System.out.println("Enter new content:");
            String newContent = scanner.nextLine();
            fileSystem.updateFile(fileName, newContent);
        } else {
            System.out.println("Invalid file name. Try again.");
        }
    }

    private void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            fileSystem.deleteFile(fileName);
        } else {
            System.out.println("Invalid file name. Try again.");
        }
    }
    
    private void deleteDirectory(String dirName) {
        if (dirName != null && !dirName.isEmpty()) {
            fileSystem.deleteDirectory(dirName);
        } else {
            System.out.println("Invalid directory name. Try again.");
        }
    }

    private void listFilesInDirectory() {
        for (FileSystemEntry entry : fileSystem.getFilesInDirectory()) {
            System.out.println(entry.getName());
        }
    }

    private void listSubDirectories() {
        for (FileSystemEntry entry : fileSystem.getSubDirectories()) {
            System.out.println(entry.getName());
        }
    }

    private void changePermissions(String entryName) {
        if (entryName != null && !entryName.isEmpty()) {
            int currentPermissions = fileSystem.getPermissions(entryName);
    
            if (currentPermissions == -1) {
                System.out.println("Entry not found. Try again.");
                return;
            }
    
            System.out.println("Current permissions: " + (currentPermissions == 1 ? "public" : "private"));
            System.out.println("Do you want to change permissions? (y/n)");
    
            String response = scanner.nextLine().trim().toLowerCase();
    
            if (response.equals("y")) {
                int newPermissions = (currentPermissions == 1) ? 0 : 1;
                fileSystem.changePermissions(entryName, newPermissions);
            } else if (response.equals("n")) {
            } else {
                System.out.println("Invalid response. Please enter 'y' or 'n'.");
            }
        } else {
            System.out.println("Invalid entry name. Try again.");
        }
    }
    
}
