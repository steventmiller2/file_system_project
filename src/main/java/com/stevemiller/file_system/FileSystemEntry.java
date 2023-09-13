package com.stevemiller.file_system;

import java.util.Date;

public abstract class FileSystemEntry {
    private String name;
    private int parentDirectoryId;
    private Date creationDate;

    public FileSystemEntry(String name) {
        this.name = name;
        this.parentDirectoryId = -1;
    }

    public FileSystemEntry(String name, int parentDirectoryId) {
        this.name = name;
        this.parentDirectoryId = parentDirectoryId;
        this.creationDate = new Date();
    }

    public String getName() {
        return name;
    }

    public int getParentDirectoryId() {
        return parentDirectoryId;
    }

    public void setParentDirectoryId(int parentDirectoryId) {
        this.parentDirectoryId = parentDirectoryId;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
