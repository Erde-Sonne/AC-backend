package org.orlo.controller;

import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.UserLogin;
import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserLogoutController {
    @PostMapping("/logout")
    public String userLogin(@RequestBody Map<String, String> params) {
        String userName = params.get("username");
        String password = params.get("password");
        return "";
    }
}
