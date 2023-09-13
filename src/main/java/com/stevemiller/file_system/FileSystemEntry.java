package com.stevemiller.file_system;

import java.util.Date;

public abstract class FileSystemEntry {
    private String name;
    private int parentDirectoryId;
    private Date creationDate;
    private int ownerId;
    private int permissions;

    public FileSystemEntry(String name) {
        this.name = name;
        this.parentDirectoryId = -1;
        this.creationDate = new Date();
        this.ownerId = -1; // Default owner ID
        this.permissions = 0; // Default permissions
    }

    public FileSystemEntry(String name, int parentDirectoryId, int ownerId, int permissions) {
        this.name = name;
        this.parentDirectoryId = parentDirectoryId;
        this.creationDate = new Date();
        this.ownerId = ownerId;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public int getParentDirectoryId() {
        return parentDirectoryId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getPermissions() {
        return permissions;
    }
}
