package com.pw.lan.server.auth;

/**
 * Created by aradej on 2016-05-16.
 */
public class AuthService {

    private UserRepository userRepository = UserRepository.getInstance();

    public boolean login(User user){
        if( userRepository.findByLoginAndPassword(user.getLogin(), user.getPassword()) != null){
            return true;
        }else{
            return false;
        }
    }

}
