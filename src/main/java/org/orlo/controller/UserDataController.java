package org.orlo.controller;

import org.orlo.entity.UserData;
import org.orlo.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserDataController {
    @Autowired
    UserDataService userDataService;

    @PostMapping("/check")
    public UserData checkUser(@RequestBody Map<String, String> params){
        String username = params.get("username");
        String password = params.get("password");
        System.out.println(username);
        System.out.println(password);
        UserData user = userDataService.findUserByName(username);
        if(user != null) {
            return user;
        }
       return null;
    }
}
