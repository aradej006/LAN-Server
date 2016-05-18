package com.pw.lan.server.domain.entities;

/**
 * Created by aradej on 2016-05-18.
 */
public class Directory {

    private String path;
    private boolean canRead;
    private boolean canWrite;
    private Group owner;

    public Directory(String path, boolean canRead, boolean canWrite, Group owner) {
        this.path = path;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.owner = owner;
    }

    public Group getOwner() {
        return owner;
    }

    public void setOwner(Group owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
}
