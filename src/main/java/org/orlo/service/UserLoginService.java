package org.orlo.service;


import org.orlo.entity.UserLogin;
import org.orlo.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {
    @Autowired
    UserLoginRepository userLoginRepository;

    public UserLogin getUserByNameAndPassword(String userName, String password) {
        return userLoginRepository.getUserByNameAndPassword(userName, password);
    }

    public void addRow(UserLogin user) {
        userLoginRepository.save(user);
    }

    public void removeRow(UserLogin user) {
        userLoginRepository.delete(user);
    }


}
