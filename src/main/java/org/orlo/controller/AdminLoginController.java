package org.orlo.controller;


import org.orlo.entity.AdminUser;
import org.orlo.service.AdminUserService;
import org.orlo.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/admin")
public class AdminLoginController {
    @Autowired
    AdminUserService adminUserService;

    @PostMapping("/login")
    public String userLogin(@RequestBody Map<String, String> params) {
        String phone = params.get("username");
        String password = params.get("password");
        System.out.println(phone);
        System.out.println(password);
        AdminUser adminUser = adminUserService.getAdminByPhone(Long.parseLong(phone));
        if(adminUser == null) {
            return "没有该管理员用户";
        } else {
            if(!MD5Util.formPassToDBPass(password, "1a2b3c4d").equals(adminUser.getPassword())) {
                return "密码错误！！！";
            }
            return "登录成功";
        }

    }
}
