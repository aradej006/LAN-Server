package com.pw.lan.server.domain.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aradej on 2016-05-13.
 */
public class User {

    private String login;
    private String password;
    private String hashAlgorithm;
    private String userState;
    private List<Group> groups;

    public static final String ACTIVE = "active";
    public static final String DEACTIVE = "inactive";
    public static final String BLOCKED = "blocked";


    public User(String login, String password, String hashAlgorithm, String userState) {
        this(login,password,hashAlgorithm);
        this.userState = userState;
    }

    public User(String login, String password, String hashAlgorithm) {
        this.login = login;
        this.password = password;
        this.hashAlgorithm = hashAlgorithm;
        groups = new ArrayList<>();
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public void addGroup(Group group){
        groups.add(group);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
