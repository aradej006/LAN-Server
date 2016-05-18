package com.pw.lan.server.auth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aradej on 2016-05-16.
 */
public class UserRepository {

    private static UserRepository instance;
    private List<User> users;
    private static final String USERSFILE = "users.usr";

    private UserRepository() {
        users = new ArrayList<>();
        readUsers();
    }

    private void readUsers(){
        try (BufferedReader br = new BufferedReader(new FileReader(USERSFILE))) {
            String line;
            String[] split;
            while((line=br.readLine())!=null){
                split = line.split(" ");
                users.add(new User(split[0],split[1],split[2],split[3]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public List<User> getUsers(){
        return users;
    }
}
