package com.pw.lan.server.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aradej on 2016-05-16.
 */
public class UserRepository {

    private static UserRepository instance;
    private List<User> users;

    private UserRepository() {
        users = new ArrayList<>();
        users.add(new User("a", "a", "SHA-256", User.ACTIVE));
    }

    public static UserRepository getInstance() {
        if (instance == null)
            instance = new UserRepository();
        return instance;
    }

    public User findByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password) && user.getUserState().equals(User.ACTIVE)) {
                return user;
            }
        }
        return null;
    }

}
