package org.orlo.controller;

import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.orlo.service.UserUnVerifyService;
import org.orlo.service.UserVerifyService;
import org.orlo.task.SocketClientTask;
import org.orlo.task.base.TaskConfig;
import org.orlo.util.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/user")
public class UserLoginController {
    static HashMap<String, AttrCheck> attrCheckMap = new HashMap<>();
    ReentrantLock reentrantLock = new ReentrantLock();
    static {
        AttrCheck source1 = new AttrCheck();
        source1.setPolicy("学生,电子科技大学,4*securityLevel*,3of3,管理员,*time*14:00-24:00,2of3");
        AttrCheck source2 = new AttrCheck();
        source2.setPolicy("学生,电子科技大学,4*securityLevel*,3of3,管理员,*time*14:00-24:00,2of3");
        AttrCheck source3 = new AttrCheck();
        source3.setPolicy("学生,电子科技大学,4*securityLevel*,3of3,管理员,*time*14:00-24:00,2of3");
        for (int i = 1; i <= 12; i++) {
            attrCheckMap.put("10.0.0." + i, source1);
        }
        attrCheckMap.put("internet", source3);
    }

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
    @PostMapping("/verifyByMac")
    public String verifyUserByMac(@RequestParam Map<String, String> params) {
        String srcMac = params.get("srcMac");
        String srcIP = params.get("srcIP");
        String dstIP = params.get("dstIP");
        String switcher = params.get("switcher");
        String srcPort = params.get("srcPort");
        String dstPort = params.get("dstPort");
        String protocol = params.get("protocol");
        System.out.println(params.toString());
        UserVerify userByMacAndSwitcher = userVerifyService.getUserByMacAndSwitcher(srcMac, switcher);
        if (userByMacAndSwitcher == null) {
            System.out.println("没有该用户，要注册");
            return "false";
        }
        AttrCheck attrCheck = attrCheckMap.get(dstIP);
        if(attrCheck == null) {
            System.out.println("没有该资源的属性树,配置接入互联网");
            AttrCheck internet = attrCheckMap.get("internet");
            internet.getUserAttr(userByMacAndSwitcher);
            boolean pass = internet.Check();
            if (pass) {
                System.out.println("认证成功了哦");
                sendMsgToController(srcMac, dstIP, switcher, srcPort, dstPort, protocol);
                return "success";
            }
            System.out.println("认证失败");
            return "false";
        }
        attrCheck.getUserAttr(userByMacAndSwitcher);
        boolean pass = attrCheck.Check();
        if(pass) {
            System.out.println("认证成功了");
            sendMsgToController(srcMac, dstIP, switcher, srcPort, dstPort, protocol);
            return "success";
        }
        System.out.println("认证失败");
        return "false";
    }

    public void sendMsgToController(String srcMac, String dstIP, String switcher,
                                           String srcPort, String dstPort, String protocol) {

            String msgToController = String.format("{\"srcMac\":\"%s\", \"dstIP\":\"%s\", \"switcher\":\"%s\", " +
                            "\"srcPort\":\"%s\", \"dstPort\":\"%s\", \"protocol\":\"%s\"}",
                    srcMac, dstIP, switcher, srcPort, dstPort, protocol);
            SocketClientTask socketClientTask = new SocketClientTask(msgToController, (o) -> {
                System.out.println("send ok");
            },
                    TaskConfig.CONTROLLER_IP, TaskConfig.CONTROLLER_PORT);
            socketClientTask.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socketClientTask.stop();

    }
}
