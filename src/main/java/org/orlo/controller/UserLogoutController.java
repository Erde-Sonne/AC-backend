package org.orlo.controller;

import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.UserLogin;
import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.orlo.service.UserLoginService;
import org.orlo.service.UserVerifyService;
import org.orlo.util.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.orlo.util.MySend.sendMsgToController;

@RestController
@RequestMapping("/user")
public class UserLogoutController {
    @Autowired
    UserVerifyService userVerifyService;
    @Autowired
    UserLoginService userLoginService;

    @PostMapping("/logout")
    public RespBean userLogout(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        UserVerify userVerify = userVerifyService.getUserByPhone(Long.parseLong(phone));

        UserLogin userLogout = new UserLogin();
        userLogout.setUsername(userVerify.getUsername());
        userLogout.setPassword(userVerify.getPassword());
        userLogout.setMAC(userVerify.getMAC());
        userLoginService.removeRow(userLogout);
        //将登出的Mac发送给onos
        sendMsgToController("3", userVerify.getMAC(), userVerify.getSwitcher());
        return RespBean.logoutSuccess();
    }
}
