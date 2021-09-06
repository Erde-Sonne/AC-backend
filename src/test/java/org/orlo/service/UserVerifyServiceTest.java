package org.orlo.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orlo.entity.UserVerify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserVerifyServiceTest {
    @Autowired
    UserVerifyService userVerifyService;
    @Test
    public void addUser() {
        UserVerify userVerify = new UserVerify();
        userVerify.setPhone(18848497750L);
        userVerify.setUsername("XXX");
        userVerify.setPassword("58142c51c4dfd31b53210924501e1dba");
        userVerify.setSalt("1a2b3c4d");
        userVerify.setDepartment("情报部");
        userVerify.setType("中尉");
        userVerify.setDevice("电脑");
        userVerify.setSafe("4");
        userVerify.setTime("18:00-22:00");
        userVerify.setIp("192.168.1.1/16");
        userVerify.setMAC("8c:16:45:85:20:60");
//        userVerify.setMAC("00:0c:29:40:bf:09");
        userVerify.setSwitcher("9");
        userVerify.setPort("1");

        userVerifyService.addUserVerify(userVerify);

    }

}