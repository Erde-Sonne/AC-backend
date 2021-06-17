package org.orlo.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orlo.entity.UserUnVerify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest
public class UserUnVerifyServiceTest {
    @Autowired
    UserUnVerifyService unVerifyService;
    @Test
    public void addUnVerifyUser() {
        for(int i = 0; i < 10; i++) {
            UserUnVerify userUnVerify = new UserUnVerify();
            userUnVerify.setPhone(18848497700L + i);
            userUnVerify.setUsername("A" + i);
            userUnVerify.setPassword("0bdf980dd885bf2402579a154da4591b");
            userUnVerify.setSalt("1a2b3c4d");
            userUnVerify.setDepartment("电子科技大学");
            userUnVerify.setType("学生" + i);
            userUnVerify.setDevice("手机" + i);
            userUnVerify.setSafe("4");
            userUnVerify.setTime("18:00-22:00");
            userUnVerify.setIp("192.168.1.1/16");
            userUnVerify.setMAC("00:00:00:ff:00:00");
            userUnVerify.setSwitcher("s1");
            userUnVerify.setPort("1");
            unVerifyService.addRow(userUnVerify);
        }

    }
    @Test
    public void deleteUnVerifyUser() {
        String username = "A1";
        String password = "123456";
        UserUnVerify toBedeleteuser = unVerifyService.getUserByPhone(Long.parseLong(username));
        System.out.println(toBedeleteuser);
        unVerifyService.removeUnVerifyUser(toBedeleteuser);
    }

}