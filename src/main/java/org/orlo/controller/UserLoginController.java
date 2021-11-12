package org.orlo.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.Policy;
import org.orlo.entity.UserLogin;
import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.orlo.service.PolicyService;
import org.orlo.service.UserLoginService;
import org.orlo.service.UserUnVerifyService;
import org.orlo.service.UserVerifyService;
import org.orlo.util.MD5Util;
import org.orlo.util.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import static org.orlo.util.MySend.sendMsgToController;

@RestController
@RequestMapping("/user")
public class UserLoginController {
    static HashMap<String, AttrCheck> attrCheckMap = new HashMap<>();
    static Set<String> userKeys = new HashSet<>();
    static Map<String, UserVerify> userVerifyCache = new HashMap<>();
    private final Map<String, String> safeRouteMap = new HashMap<>();
    public static BlockingQueue<String> blockingQueueA = new LinkedBlockingQueue<>();
    public static BlockingQueue<String> blockingQueueC = new LinkedBlockingQueue<>();
    public static BlockingQueue<String> blockingQueueD = new LinkedBlockingQueue<>();
    ReentrantLock reentrantLock = new ReentrantLock();
    static {
/*        AttrCheck source1 = new AttrCheck();
        source1.setPolicy("中尉,情报部,安全级别：3,3of3,管理员,访问时段：14:00-24:00,2of3");
        AttrCheck source2 = new AttrCheck();
        source2.setPolicy("中尉,作战部,安全级别：3,3of3,管理员,访问时段：4:00-24:00,2of3");
        AttrCheck source3 = new AttrCheck();
        source3.setPolicy("中尉,情报部,安全级别：3,3of3,管理员,访问时段：2:00-24:00,2of3");
        for (int i = 1; i <= 10; i++) {
            attrCheckMap.put("10.0.0." + i, source1);
        }
        for (int i = 11; i <= 24; i++) {
            attrCheckMap.put("10.0.0." + i, source2);
        }
        attrCheckMap.put("internet", source3);*/
      /*  KafkaListenerTask kafkaListenerTask = new KafkaListenerTask(blockingQueueA, blockingQueueC, blockingQueueD);
        kafkaListenerTask.start();
        System.out.println("********************** kafka listener start");

        VerifyByMacTask verifyByMacTask = new VerifyByMacTask(blockingQueueA, userKeys, userVerifyCache, attrCheckMap);
        verifyByMacTask.start();
        System.out.println("********************** verify by mac start");*/
    }

    @Autowired
    UserUnVerifyService userUnVerifyService;
    @Autowired
    UserVerifyService userVerifyService;
    @Autowired
    UserLoginService userLoginService;
    @Autowired
    PolicyService policyService;

    @PostMapping("/login")
    public RespBean userLogin(@RequestBody Map<String, String> params) {
        String phone = params.get("username");
        String password = params.get("password");
        String safeRoute = params.get("safeRoute");
        System.out.println(phone);
        System.out.println(password);
        System.out.println(safeRoute);
        UserUnVerify userUnVerify = userUnVerifyService.getUserByPhone(Long.parseLong(phone));
        UserVerify userVerify = userVerifyService.getUserByPhone(Long.parseLong(phone));
        if(userUnVerify == null && userVerify == null) {
            return RespBean.noUser();
        } else if(userUnVerify != null) {
            return RespBean.verifyUser();
        } else {
            if(!MD5Util.formPassToDBPass(password, "1a2b3c4d").equals(userVerify.getPassword())) {
                return RespBean.passwdError();
            }
            UserLogin userLogin = new UserLogin();
            userLogin.setUsername(userVerify.getUsername());
            userLogin.setPassword(userVerify.getPassword());
            userLogin.setMAC(userVerify.getMAC());
//            safeRouteMap.put(userVerify.getMAC().toUpperCase(), safeRoute);
            userLoginService.addRow(userLogin);
            //将登录成功的Mac发送给onos
            sendMsgToController("2", userVerify.getMAC(), userVerify.getSwitcher());
//            System.out.println(safeRouteMap.toString());
            System.out.println(userVerify.toString());
            //一段时间后失效
//                Timer timer=new Timer();
//                TimerTask task=new TimerTask(){
//                    public void run(){
//                        sendMsgToController("3", userVerify.getMAC(), userVerify.getSwitcher());
//                    }
//                };
//                timer.schedule(task,60000);//延迟xx秒执行
            return RespBean.loginSuccess();
        }
    }

    /**
     * 获取当前时间
     * @return
     */
    private String getNowTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String hourStr = "";
        String minuteStr = "";
        if(minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = String.valueOf(minute);
        }
        return hour + ":" + minuteStr + "-" + hour + ":" + minuteStr;
    }

/*    @PostMapping("/logout")
    public RespBean userLogout(@RequestBody Map<String, String> params) {

    }*/

    @PostMapping("/verifyByMac")
    public RespBean verifyUserByMac(@RequestParam Map<String, String> params) {
        String srcMac = params.get("srcMac");
        String srcIP = params.get("srcIP");
        String dstIP = params.get("dstIP");
        String switcher = params.get("switcher");
        String srcPort = params.get("srcPort");
        String dstPort = params.get("dstPort");
        String protocol = params.get("protocol");
        String beginTime = params.get("beginTime");
        System.out.println(params.toString());
        srcMac = srcMac.toUpperCase();
        String key = srcMac + "&" + switcher;
        UserVerify userByMacAndSwitcher = null;
        if (userKeys.contains(key)) {
            userByMacAndSwitcher = userVerifyCache.get(key);
        } else {
            userByMacAndSwitcher = userVerifyService.getUserByMacAndSwitcher(srcMac, switcher);
            if (userByMacAndSwitcher != null) {
                userKeys.add(key);
                userVerifyCache.put(key, userByMacAndSwitcher);
            }
        }
        if (userByMacAndSwitcher == null) {
            System.out.println("没有该用户，要注册");
            return RespBean.noUser();
        }
        String nowTime = getNowTime();
        System.out.println(nowTime);
        userByMacAndSwitcher.setTime(nowTime);
        AttrCheck attrCheck = null;
        if(isLanDst(dstIP)) {
            attrCheck = attrCheckMap.get("lan");
        } else {
            attrCheck = attrCheckMap.get(dstIP);
            if (attrCheck == null) {
                Policy policy = policyService.getPolicyByIP(dstIP);
                if(policy != null) {
                    AttrCheck check = new AttrCheck();
                    check.setPolicy(policy.getPolicycol());
                    attrCheckMap.put(policy.getDstIP(), check);
                    attrCheck = check;
                } else {
                    attrCheck = attrCheckMap.get("internet");
                }
            }
        }
        if(attrCheck == null) {
            Policy policy = policyService.getPolicyByIP(dstIP);
            if (policy == null) {
                if (isLanDst(dstIP)) {
                    policy = policyService.getPolicyByIP("lan");
                } else {
                    policy = policyService.getPolicyByIP("internet");
                }
            }
            AttrCheck check = new AttrCheck();
            check.setPolicy(policy.getPolicycol());
            attrCheckMap.put(policy.getDstIP(), check);
            attrCheck = check;
        }
        attrCheck.getUserAttr(userByMacAndSwitcher);
        boolean pass = attrCheck.Check();
        if(pass) {
            System.out.println("认证成功了");
            String safeRouting = "1";
//            String tmp = safeRouteMap.getOrDefault(srcMac, "false");
//            if("true".equals(tmp)) {
//                safeRouting = "1";
//            }
            sendMsgToController(srcMac, dstIP, switcher, srcPort, dstPort, protocol, beginTime, safeRouting);
            return RespBean.verifySuccess();
        }
        System.out.println("认证失败");
        return RespBean.noAuthorize();
    }

    private boolean isLanDst(String dst) {
        String[] split = dst.split("10.0.0.");
        return split.length == 2;
    }
/*    public static void sendMsgToController(String srcMac, String dstIP, String switcher,
                                           String srcPort, String dstPort, String protocol) {

            String msgToController = String.format("{\"info\":\"1\", \"srcMac\":\"%s\", \"dstIP\":\"%s\", \"switcher\":\"%s\", " +
                            "\"srcPort\":\"%s\", \"dstPort\":\"%s\", \"protocol\":\"%s\"}",
                    srcMac, dstIP, switcher, srcPort, dstPort, protocol);
            SocketClientTask socketClientTask = new SocketClientTask(msgToController, (o) -> {
//                System.out.println("send ok");
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

    public static void sendMsgToController(String info, String loginMac, String switcher) {
        String msgToController = String.format("{\"info\":\"%s\", \"loginMac\":\"%s\", \"switcher\":\"%s\"}",
                info, loginMac, switcher);
        SocketClientTask socketClientTask = new SocketClientTask(msgToController, (o) -> {
//                System.out.println("send ok");
        },
                TaskConfig.CONTROLLER_IP, TaskConfig.CONTROLLER_PORT);
        socketClientTask.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketClientTask.stop();
    }*/
}