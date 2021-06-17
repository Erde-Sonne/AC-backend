package org.orlo.service;

import org.orlo.entity.UserUnVerify;
import org.orlo.repository.UserUnVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUnVerifyService {
    @Autowired
    UserUnVerifyRepository userUnVerifyRepository;
    public UserUnVerify getUserByPhone(long phone) {
        return userUnVerifyRepository.getUserUnVerifyByPhone(phone);
    }

    public void addRow(UserUnVerify user) {
        userUnVerifyRepository.save(user);
    }

    public List<UserUnVerify> getAll() {
        return userUnVerifyRepository.findAll();
    }

    public void removeUnVerifyUser(UserUnVerify user) {
        userUnVerifyRepository.delete(user);
    }
}
