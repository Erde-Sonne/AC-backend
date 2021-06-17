package org.orlo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @GetMapping("/reqVerifyUser")
    public String reqUnVerifyUser() {
        List<UserVerify> all = userVerifyService.getAll();
        ObjectMapper mapper = new ObjectMapper();
        String out = "";
        try {
            out = mapper.writeValueAsString(all);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return out;
    }

    @PostMapping("/updateVerifyUser")
    public String updateVerifyUser(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        System.out.println("******username:" + phone + "*****password:" + password);
        UserVerify userToBeUpdate = userVerifyService.getUserByPhone(Long.parseLong(phone));
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

    @PostMapping("/deleteVerifyUser")
    public String deleteUnVerifyUser(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        System.out.println("******username:" + phone + "*****password:" + password);
        UserVerify toBedeleteuser = userVerifyService.getUserByPhone(Long.parseLong(phone));
        userVerifyService.removeUserVerify(toBedeleteuser);
        return "成功移除";
    }
}
