package com.pw.lan.server.auth;

/**
 * Created by aradej on 2016-05-13.
 */
public class User {

    private String login;
    private String password;
    private String hashAlgorithm;
    private String userState;

    static final String ACTIVE = "active";
    static final String DEACTIVE = "deactive";
    static final String BLOCKED = "blockted";


    public User(String login, String password, String hashAlgorithm, String userState) {
        this.login = login;
        this.password = password;
        this.hashAlgorithm = hashAlgorithm;
        this.userState = userState;
    }

    public User(String login, String password, String hashAlgorithm) {
        this.login = login;
        this.password = password;
        this.hashAlgorithm = hashAlgorithm;
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
}
