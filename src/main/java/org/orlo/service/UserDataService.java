package org.orlo.service;

import org.orlo.entity.UserData;
import org.orlo.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {
    @Autowired
    UserDataRepository userDataRepository;

    public void addUser(UserData user) {
        userDataRepository.save(user);
    }

    public UserData findUserByName(String name) {
        return userDataRepository.getUserByName(name);
    }


}
