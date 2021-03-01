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
        userVerify.setUsername("YX");
        userVerify.setPassword("123456");
        userVerify.setDepartment("电子科技大学");
        userVerify.setType("学生");
        userVerify.setDevice("电脑");
        userVerify.setSafe("4");
        userVerify.setTime("18:00-22:00");
        userVerify.setIp("192.168.1.1/16");
        userVerify.setMAC("00:0C:29:D8:8E:96");
        userVerify.setSwitcher("9");
        userVerify.setPort("1");



        UserVerify userVerify1 = new UserVerify();
        userVerify1.setUsername("C");
        userVerify1.setPassword("123456");
        userVerify1.setDepartment("电子科技大学");
        userVerify1.setType("学生");
        userVerify1.setDevice("手机");
        userVerify1.setSafe("5");
        userVerify1.setTime("16:00-24:00");
        userVerify1.setIp("192.168.1.1/16");
        userVerify1.setMAC("00:00:00:ff:00:00");
        userVerify1.setSwitcher("s1");
        userVerify1.setPort("1");

        userVerifyService.addUserVerify(userVerify);
        userVerifyService.addUserVerify(userVerify1);

    }

}