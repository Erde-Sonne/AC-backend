package org.orlo.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.Policy;
import org.orlo.entity.UserUnVerify;
import org.orlo.entity.UserVerify;
import org.orlo.service.PolicyService;
import org.orlo.service.UserUnVerifyService;
import org.orlo.service.UserVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/administrator")
public class UserVerifyController {
    @Autowired
    UserUnVerifyService userUnVerifyService;
    @Autowired
    UserVerifyService userVerifyService;
    @Autowired
    PolicyService policyService;

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
        String mac = params.get("mac");
        String switcher = params.get("switcher");
//        System.out.println("******username:" + phone + "*****password:" + password);
        UserVerify userToBeUpdate = userVerifyService.getUserByPhone(Long.parseLong(phone));
        userToBeUpdate.setType(params.get("type"));
        userToBeUpdate.setTime(params.get("time"));
        userToBeUpdate.setSwitcher(switcher);
        userToBeUpdate.setSafe(params.get("safe"));
        userToBeUpdate.setPort(params.get("port"));
        userToBeUpdate.setMAC(mac);
        userToBeUpdate.setIp(params.get("ip"));
        userToBeUpdate.setDevice(params.get("device"));
        userToBeUpdate.setDepartment(params.get("department"));
        userVerifyService.updateUserVerify(userToBeUpdate);
        Set<String> userKeys = UserLoginController.userKeys;
        Map<String, UserVerify> userVerifyCache = UserLoginController.userVerifyCache;
        mac = mac.toUpperCase();
        String key = mac + "&" + switcher;
        if (userKeys.contains(key)) {
            UserVerify userByMacAndSwitcher = userVerifyService.getUserByMacAndSwitcher(mac, switcher);
            if (userByMacAndSwitcher != null) {
                userVerifyCache.put(key, userByMacAndSwitcher);
            }
        }
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

    @PostMapping("/setPolicy")
    public String setPolicy(@RequestBody Map<String,String> params){
        Policy policy = new Policy();
        String policyString = params.get("policyString");
        policyString = policyString.substring(0,policyString.length() - 1);
        policy.setPolicycol(policyString);
        policy.setDstIP(params.get("IpAddr"));
        policyService.setPolicy(policy);
        return "success";
    }

    @PostMapping("/deletePolicy")
    public String deletePolicy(@RequestBody Map<String, String> params){
        policyService.deletePolicyByIP(params.get("IpAddr"));
        return "success";
    }

    @PostMapping("/updatePolicy")
    public String updatePolicy(@RequestBody Map<String, String> params){
        String policyStr = params.get("policyStr");
        String ipAddr = params.get("IpAddr");
        policyService.updatePolicy(policyStr, ipAddr);
        HashMap<String, AttrCheck> attrCheckMap = UserLoginController.attrCheckMap;
        AttrCheck attrCheck = new AttrCheck();
        attrCheck.setPolicy(policyStr);
        attrCheckMap.put(ipAddr, attrCheck);
        return "success";
    }
}

