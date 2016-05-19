package com.pw.lan.server.domain.repositories;

import com.pw.lan.server.domain.entities.Group;
import com.pw.lan.server.domain.entities.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aradej on 2016-05-16.
 */
public class UserRepository {

    private static final String FILE = "db/users.db";
    private static final String BRIDGEFILE = "db/users_groups.db";
    private static UserRepository instance;
    private Map<String, User> users;

    private UserRepository() {
        users = new HashMap<>();
        readUsers();
    }

    public static UserRepository getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    private synchronized void readUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            String[] split;
            while ((line = br.readLine()) != null) {
                split = line.split(" ");
                users.put(split[0], new User(split[0], split[1], split[2], split[3]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findByLoginAndPassword(String login, String password) {
        User user = users.get(login);
        if (user.getLogin().equals(login) && user.getPassword().equals(password) && user.getUserState().equals(User.ACTIVE)) {
            return user;
        }
        return null;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User setGroups(User user){        String login = user.getLogin();
        try (BufferedReader br = new BufferedReader(new FileReader(BRIDGEFILE))) {
            String line;
            String[] split;
            while ((line = br.readLine()) != null) {
                split = line.split(" ");
                if(split[1].equals(login) && !user.getGroups().contains(new Group(split[0])))
                    user.addGroup(new Group(split[0]));
            }
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
