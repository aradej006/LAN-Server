package com.pw.lan.server.domain.repositories;

import com.pw.lan.server.domain.entities.Directory;
import com.pw.lan.server.domain.entities.Group;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aradej on 2016-05-18.
 */
public class DirectoryRepository {

    private static final String FILE = "db/directories.db";
    private static DirectoryRepository instance;
    private Map<String, Directory> directories;

    private DirectoryRepository() {
        directories = new HashMap<>();
        readDirectories();
    }

    public static DirectoryRepository getInstance() {
        if (instance == null) instance = new DirectoryRepository();
        return instance;
    }

    private synchronized void readDirectories() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                directories.put(split[0], new Directory(split[0], Boolean.parseBoolean(split[1]), Boolean.parseBoolean(split[2]), new Group(split[3])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Group setDirectories(Group group) {
        directories.entrySet().stream()
                .filter(map -> map.getValue().getOwner().getGroupName().equals(group.getGroupName()))
                .forEach(d -> group.addDirectory(d.getValue()));
        return group;
    }
}
