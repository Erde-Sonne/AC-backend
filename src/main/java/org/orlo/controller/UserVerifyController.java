package org.orlo.controller;

import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.orlo.service.UserUnVerifyService;
import org.orlo.service.UserVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/administrator")
public class UserVerifyController {
    @Autowired
    UserUnVerifyService userUnVerifyService;
    @Autowired
    UserVerifyService userVerifyService;

    @GetMapping("/getAllUnVerifyUser")
    public List<UserUnVerify> getUnVerifyUser() {
        return userUnVerifyService.getAll();
    }
    @PostMapping("/authoried")
    public void authoriedUser() {
        UserUnVerify userUnVerify = new UserUnVerify();
        UserVerify userVerify = new UserVerify();
        userUnVerifyService.removeUnVerifyUser(userUnVerify);
        userVerifyService.addUserVerify(userVerify);
    }

    @PostMapping("/updateVerifyUser")
    public String updateVerifyUser(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        System.out.println("******username:" + username + "*****password:" + password);
        UserVerify userToBeUpdate = userVerifyService.getUserByNameAndPassword(username, password);
        userToBeUpdate.setType(params.get("type"));
        userToBeUpdate.setTime(params.get("time"));
        userToBeUpdate.setSwitcher(params.get("switcher"));
        userToBeUpdate.setSafe(params.get("safe"));
        userToBeUpdate.setPort(params.get("port"));
        userToBeUpdate.setMAC(params.get("mac"));
        userToBeUpdate.setIp(params.get("ip"));
        userToBeUpdate.setDevice(params.get("device"));
        userToBeUpdate.setDepartment(params.get("department"));
        userVerifyService.updateUserVerify(userToBeUpdate);
        return "成功更改";
    }

}
