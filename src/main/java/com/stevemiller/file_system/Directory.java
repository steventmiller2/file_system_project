package com.stevemiller.file_system;

public class Directory extends FileSystemEntry {
    private int id;

    public Directory(String name) {
        super(name);
    }

    public Directory(String name, int parentDirectoryId) {
        super(name, parentDirectoryId);
    }

    public Directory(String name, int parentDirectoryId, int id) {
        super(name, parentDirectoryId);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
