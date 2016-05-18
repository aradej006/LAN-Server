package com.pw.lan.server.domain.repositories;

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
public class GroupRepository {

    private static final String FILE = "db/group.db";
    private static GroupRepository instance;
    private Map<String, Group> groups;

    private GroupRepository() {
        groups = new HashMap<>();
        readGroups();
    }

    public static GroupRepository getInstance() {
        if (instance == null) instance = new GroupRepository();
        return instance;
    }

    private synchronized void readGroups() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                groups.put(line, new Group(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
