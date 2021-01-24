package org.orlo.service;

import org.orlo.entity.UserVerify;
import org.orlo.repository.UserVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserVerifyService {
    @Autowired
    UserVerifyRepository userVerifyRepository;

    public UserVerify getUserByNameAndPassword(String userName, String password) {
        return userVerifyRepository.getUserByNameAndPassword(userName, password);
    }

    public void addUserVerify(UserVerify user) {
        userVerifyRepository.save(user);
    }

    public void removeUserVerify(UserVerify user) {
        userVerifyRepository.delete(user);
    }

    public void updateUserVerify(UserVerify user) {
        userVerifyRepository.save(user);
    }

}
