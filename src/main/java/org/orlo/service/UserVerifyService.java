package org.orlo.service;

import org.orlo.entity.UserVerify;
import org.orlo.repository.UserVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserVerifyService {
    @Autowired
    UserVerifyRepository userVerifyRepository;

    public UserVerify getUserByPhone(long phone) {
        return userVerifyRepository.getUserVerifyByPhone(phone);
    }

    public UserVerify getUserByMacAndSwitcher(String mac, String switcher) {
        return userVerifyRepository.getUserByMacAndSwitcher(mac, switcher);
    }

    public List<UserVerify> getAll() {
        return userVerifyRepository.findAll();
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
