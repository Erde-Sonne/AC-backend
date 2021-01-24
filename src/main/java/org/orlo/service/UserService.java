package org.orlo.service;

import org.orlo.entity.User;
import org.orlo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User getUserByNameAndPassword(String userName, String password) {
        return userRepository.getUserByNameAndPassword(userName, password);
    }
}
