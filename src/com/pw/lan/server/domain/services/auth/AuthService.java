package com.pw.lan.server.domain.services.auth;

import com.pw.lan.server.domain.entities.User;
import com.pw.lan.server.domain.repositories.UserRepository;

/**
 * Created by aradej on 2016-05-16.
 */
public class AuthService {

    private UserRepository userRepository = UserRepository.getInstance();

    public User login(User user) {
        User u = userRepository.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (u != null) {
            return u;
        } else {
            return null;
        }
    }

}
