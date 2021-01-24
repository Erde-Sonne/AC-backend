package org.orlo.controller;

import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.orlo.service.UserUnVerifyService;
import org.orlo.service.UserVerifyService;
import org.orlo.task.SocketClientTask;
import org.orlo.task.base.TaskConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    UserUnVerifyService userUnVerifyService;
    @Autowired
    UserVerifyService userVerifyService;
    @PostMapping("/login")
    public String userLogin(@RequestBody Map<String, String> params) {
        String userName = params.get("username");
        String password = params.get("password");
        UserUnVerify userUnVerify = userUnVerifyService.getUserByNameAndPassword(userName, password);
        UserVerify userVerify = userVerifyService.getUserByNameAndPassword(userName, password);
        if(userUnVerify == null && userVerify == null) {
            return "用户名或者密码错误";
        } else if(userUnVerify != null) {
            return "当前用户正在审核中，请联系管理员";
        } else {
            AttrCheck Check = new AttrCheck();
            Check.getUserAttr(userVerify);
            Check.setPolicy("学生,电子科技大学,4*securityLevel*,3of3,管理员,*time*14:00-24:00,2of3");
            boolean pass = Check.Check();
            if(pass) {
                String msgToController = "{\"status\":1, \"toInternet\":1 , \"hostId\":9, \"dstIds\":[2,5]}";
                SocketClientTask socketClientTask = new SocketClientTask(msgToController, System.out::println,
                        TaskConfig.CONTROLLER_IP, TaskConfig.CONTROLLER_PORT);
                socketClientTask.start();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                socketClientTask.stop();
                return "登录成功";
            } else {
                return "失败，未得到授权";
            }
        }
    }
}
