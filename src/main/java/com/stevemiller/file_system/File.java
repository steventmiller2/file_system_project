package com.stevemiller.file_system;

public class File extends FileSystemEntry {
    private String content;
    private long size;

    public File(String name, String content) {
        super(name);
        this.content = content;
        this.size = content.length();
    }

    public File(String name, String content, int parentDirectoryId) {
        super(name, parentDirectoryId);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public long getSize() {
        return size;
    }
}