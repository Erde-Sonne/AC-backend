package org.orlo.controller;

import org.orlo.entity.UserUnVerify;
import org.orlo.service.UserUnVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserUnVerifyController {
    @Autowired
    UserUnVerifyService userUnVerifyService;
    @PostMapping("/signin")
    public String signIn() {
        UserUnVerify userUnVerify = new UserUnVerify();
        userUnVerifyService.addRow(userUnVerify);
        return "注册申请已经提交";
    }
    @PostMapping("/deleteUnVerifyUser")
    public String deleteUnVerifyUser(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        System.out.println("******username:" + username + "*****password:" + password);
        UserUnVerify toBedeleteuser = userUnVerifyService.getUserByNameAndPassword(username, password);
        userUnVerifyService.removeUnVerifyUser(toBedeleteuser);
        return "成功移除";
    }
}
