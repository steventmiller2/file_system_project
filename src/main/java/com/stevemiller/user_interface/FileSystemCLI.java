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
            System.out.println("--Enter command (touch, mkdir, cat, update-file, rm, rmd, ls, pwd, cd, exit): --");
            String command = scanner.nextLine().trim();

            switch (command.toLowerCase()) {
                case "pwd":
                    printCurrentDirectory();
                    break;
                case "cd":
                    changeDirectory();
                    break;
                case "touch":
                    createFile();
                    break;
                case "mkdir":
                    createDirectory();
                    break;
                case "cat":
                    readFile();
                    break;
                case "update-file":
                    updateFile();
                    break;
                case "rm":
                    deleteFile();
                    break;
                case "rmd":
                    deleteDirectory();
                    break;
                case "ls":
                    listFilesInDirectory();
                    listSubDirectories();
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

    private void changeDirectory() {
        System.out.println("Enter directory name:"); //TODO: UPDATE
        String dirName = scanner.nextLine();
        fileSystem.changeDirectory(dirName);
    }

    private void createFile() {
        System.out.println("Enter file name:");
        String fileName = scanner.nextLine();

        System.out.println("Enter file content:");
        String content = scanner.nextLine();

        File file = new File(fileName, content);
        fileSystem.createFile(file);

        System.out.println("File created successfully."); //TODO: DELETE these clgs
    }

    private void createDirectory() {
        System.out.println("Enter directory name:");
        String dirName = scanner.nextLine();

        Directory directory = new Directory(dirName);
        fileSystem.createDirectory(directory);

        System.out.println("Directory created successfully.");
    }

    private void printCurrentDirectory() {
        Directory directory = fileSystem.printCurrentDirectory();

        if (directory != null) {
            System.out.println(directory.getName());
        } else {
            System.out.println("Directory not found or permission denied.");
        }
    }

    private void readFile() {
        System.out.println("Enter file name:");
        String fileName = scanner.nextLine();

        FileSystemEntry file = fileSystem.readFile(fileName);

        if (file != null) {
            System.out.println("File content:");
            System.out.println(((File) file).getContent());
        } else {
            System.out.println("File not found or permission denied.");
        }
    }

    private void updateFile() {
        System.out.println("Enter file name:");
        String fileName = scanner.nextLine();

        System.out.println("Enter new content:");
        String newContent = scanner.nextLine();

        fileSystem.updateFile(fileName, newContent);
        System.out.println("File updated successfully.");
    }

    private void deleteFile() {
        System.out.println("Enter file name:");
        String fileName = scanner.nextLine();

        fileSystem.deleteFile(fileName);
        System.out.println("File deleted successfully.");
    }

    private void deleteDirectory() {
        System.out.println("Enter directory name:");
        String dirName = scanner.nextLine();

        fileSystem.deleteDirectory(dirName);
        System.out.println("Directory deleted successfully.");
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
}
