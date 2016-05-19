package com.pw.lan.server.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by aradej on 2016-05-18.
 */
public class Group {

    private String groupName;
    private List<Directory> directories;

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Directory> getDirectories() {
        if (directories == null) directories = new ArrayList<>();
        return directories;
    }

    public void setDirectories(List<Directory> directories) {
        this.directories = directories;
    }

    public void addDirectory(Directory directory) {
        if (directories == null) directories = new ArrayList<>();
        if (!directories.stream().filter(dir -> dir.equals(directory)).findAny().isPresent())
            directories.add(directory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupName, group.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName);
    }
}
