package org.orlo.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orlo.entity.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDataRepositoryTest {

    @Autowired
    private UserDataRepository userDataRepository;

    @Test
    public void saveUser() {
        UserData user = new UserData();
        user.setAdmin(false);
        user.setLastLogoutTime(10000L);
        user.setLastMac("xx");
        user.setMark(true);
        user.setUserName("yx");
        user.setPassword("12345");
        System.out.println(user);
        userDataRepository.save(user);
    }
}