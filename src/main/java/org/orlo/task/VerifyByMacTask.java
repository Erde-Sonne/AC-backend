package org.orlo.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.orlo.attrTree.AttrCheck;
import org.orlo.entity.UserVerify;
import org.orlo.service.UserUnVerifyService;
import org.orlo.service.UserVerifyService;
import org.orlo.task.base.AbstractStoppableTask;
import org.orlo.util.MQDict;
import org.orlo.util.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;


public class VerifyByMacTask extends AbstractStoppableTask {
    @Autowired
    UserUnVerifyService userUnVerifyService;
    @Autowired
    UserVerifyService userVerifyService;

    BlockingQueue<String> blockingQueue;
    Set<String> userKeys;
    Map<String, UserVerify> userVerifyCache;
    HashMap<String, AttrCheck> attrCheckMap;
    public  VerifyByMacTask(BlockingQueue<String> blockingQueue, Set<String> userKeys,
                            Map<String, UserVerify> userVerifyCache, HashMap<String, AttrCheck> attrCheckMap) {
        this.blockingQueue = blockingQueue;
        this.userKeys = userKeys;
        this.userVerifyCache = userVerifyCache;
        this.attrCheckMap = attrCheckMap;
    }

    @Override
    public void run() {
        isRunning.set(true);
        while (!stopRequested) {
            try {
                String take = blockingQueue.take();
                verifyUser(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopRequested = true;
            }
        }
        isRunning.set(false);
    }

    private void verifyUser(String data) {
        JSONObject params = JSON.parseObject(data);
        String srcMac = params.getString("srcMac");
        String srcIP = params.getString("srcIP");
        String dstIP = params.getString("dstIP");
        String switcher = params.getString("switcher");
        String srcPort = params.getString("srcPort");
        String dstPort = params.getString("dstPort");
        String protocol = params.getString("protocol");
        System.out.println(params.toString());
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
            return;
        }

        AttrCheck attrCheck = attrCheckMap.get(dstIP);
        if(attrCheck == null) {
            AttrCheck internet = attrCheckMap.get("internet");
            internet.getUserAttr(userByMacAndSwitcher);
            boolean pass = internet.Check();
            if (pass) {
                System.out.println("认证成功了哦");
                sendMsgToKafka(srcMac, dstIP, switcher, srcPort, dstPort, protocol);
                return;
            }
            System.out.println("认证失败");
            return;
        }
        attrCheck.getUserAttr(userByMacAndSwitcher);
        boolean pass = attrCheck.Check();
        if(pass) {
            System.out.println("认证成功了");
            sendMsgToKafka(srcMac, dstIP, switcher, srcPort, dstPort, protocol);
            return;
        }
        System.out.println("认证失败");

    }

    private void sendMsgToKafka(String srcMac, String dstIP, String switcher,
                                String srcPort, String dstPort, String protocol) {
        String msg = String.format("{\"srcMac\":\"%s\", \"dstIP\":\"%s\", \"switcher\":\"%s\", " +
                        "\"srcPort\":\"%s\", \"dstPort\":\"%s\", \"protocol\":\"%s\"}",
                srcMac, dstIP, switcher, srcPort, dstPort, protocol);
        //消息实体
        ProducerRecord<String , String> record = new ProducerRecord<String, String>(MQDict.PRODUCER_TOPICB, msg);
        //发送消息
        Producer.producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e){
                        System.out.println("send error" + e.getMessage());
                }else {
                    System.out.println(String.format("offset:%s,partition:%s",recordMetadata.offset(),recordMetadata.partition()));
                }
            }
        });
//        Producer.producer.close();
    }
}
