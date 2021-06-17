package org.orlo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orlo.entity.UserUnVerify;
import org.orlo.service.UserUnVerifyService;
import org.orlo.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/saveUser")
    public String saveUser(@RequestBody Map<String, String> params) {
        UserUnVerify userUnVerify = new UserUnVerify();
        String username = params.get("username");
        String phone = params.get("phone");
        String password = params.get("password");
        String department = params.get("department");
        String type = params.get("type");
        String device = params.get("device");
        String safe = params.get("safe");
        String time = params.get("time");
        String ip = params.get("ip");
        String mac = params.get("mac");
        String switcher = params.get("switcher");
        String port = params.get("port");
        userUnVerify.setUsername(username);
        userUnVerify.setPhone(Long.parseLong(phone));
        userUnVerify.setPassword(MD5Util.inputPassToDBPass(password));
        userUnVerify.setDepartment(department);
        userUnVerify.setType(type);
        userUnVerify.setDevice(device);
        userUnVerify.setSafe(safe);
        userUnVerify.setTime(time);
        userUnVerify.setIp(ip);
        userUnVerify.setMAC(mac);
        userUnVerify.setSwitcher(switcher);
        userUnVerify.setPort(port);
        userUnVerify.setSalt("1a2b3c4d");
        userUnVerifyService.addRow(userUnVerify);
        return "";
    }

    @PostMapping("/deleteUnVerifyUser")
    public String deleteUnVerifyUser(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        System.out.println("******username:" + phone + "*****password:" + password);
        UserUnVerify toBedeleteuser = userUnVerifyService.getUserByPhone(Long.parseLong(phone));
        userUnVerifyService.removeUnVerifyUser(toBedeleteuser);
        return "成功移除";
    }
    @GetMapping("/reqUnVerifyUser")
    public String reqUnVerifyUser() {
        List<UserUnVerify> all = userUnVerifyService.getAll();
        ObjectMapper mapper = new ObjectMapper();
        String out = "";
        try {
            out = mapper.writeValueAsString(all);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return out;
    }
}
