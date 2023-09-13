package com.stevemiller.file_system;

public class Directory extends FileSystemEntry {
    private int id;

    public Directory(String name) {
        super(name);
    }

    public Directory(String name, int parentDirectoryId, int id, int ownerId, int permissions) {
        super(name, parentDirectoryId, ownerId, permissions);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
