package org.orlo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orlo.entity.UserData;
import org.orlo.entity.UserFlowData;
import org.orlo.service.UserDataService;
import org.orlo.service.UserFlowDataService;
import org.orlo.task.KafkaListenerTask;
import org.orlo.task.PeriodicalSocketClientTask;
import org.orlo.task.base.TaskConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/data")
public class UserDataController {

    static {
        PeriodicalSocketClientTask.RequestGenerator requestGenerator = () -> {
            String req = "default";
            return req;
        };
        PeriodicalSocketClientTask.ResponseHandler responseHandler = (String resp) -> {
            System.out.println(resp);
            JSONObject jsonObject = JSON.parseObject(resp);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = df.format(new Date());
            System.out.println(format + "--------------------------------------------------------------------------------------------------");

        };
        //间隔20s向信任度计算服务器发出计算请求
 /*       PeriodicalSocketClientTask socketClientTask = new PeriodicalSocketClientTask(TaskConfig.LOF_IP,
                TaskConfig.LOF_PORT, requestGenerator, responseHandler);
        socketClientTask.setDelay(1);
        socketClientTask.setInterval(20);
        socketClientTask.start();*/
    }

    Set<String> redisKeys = new CopyOnWriteArraySet<>();
    Jedis jedis = new Jedis("127.0.0.1", 6379);
    @Autowired
    UserDataService userDataService;
    @Autowired
    UserFlowDataService userFlowDataService;

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

    @PostMapping("/postData")
    public void postData(@RequestParam Map<String, String> params) {
        String data = params.get("data");
//        System.out.println(data);
        JSONObject jsonObject = JSON.parseObject(data);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        System.out.println(format + "--------------------------------------------------------------------------------------------------");
        //每一次上传数据时，将key存为一个set
        HashSet<String> currentKeys = new HashSet<>();
        for(int i = 0; i < 12; i++) {
            JSONArray jsonArray = jsonObject.getJSONArray(String.valueOf(i));
            if (jsonArray == null || jsonArray.isEmpty()) {
                continue;
            }
            int size = jsonArray.size();
            for (int j = 0; j < size; j++) {
                JSONObject oneData = jsonArray.getJSONObject(j);
                String srcIP = oneData.getString("srcIP");
                String srcMAC = oneData.getString("srcMAC");
                String dstIP = oneData.getString("dstIP");
                String dstMAC = oneData.getString("dstMAC");
                String srcPort = oneData.getString("srcPort");
                String dstPort = oneData.getString("dstPort");
                String protocol = oneData.getString("protocol");
                Long time = oneData.getLong("time");
                Long packets = oneData.getLong("packets");
                Long bytes = oneData.getLong("bytes");
                Long life = oneData.getLong("life");
                Long maxflow = 0L;
                String key = "";
                if (!srcIP.equals("")) {
                    key = srcIP + ">" + dstMAC;
                } else if(!srcMAC.equals("")) {
                    key = srcMAC + ">" + dstIP;
                }
                String preData = jedis.lindex(key, -1);
                //取出前一条数据
                if (preData != null) {
                    JSONObject preJson = JSONObject.parseObject(preData);
                    Long preBytes = preJson.getLong("bytes");
                    maxflow = (bytes - preBytes) / 20;
                    Long preMaxflow = preJson.getLong("maxflow");
                    maxflow = Math.max(maxflow, preMaxflow);
                } else {
                    //该流第一条数据
                    maxflow = bytes / 20;
                }

                JSONObject tmp = new JSONObject();
                tmp.put("time",time);
                tmp.put("packets",packets);
                tmp.put("bytes",bytes);
                tmp.put("life",life);
                tmp.put("maxflow",maxflow);
                tmp.put("srcPort",srcPort);
                tmp.put("dstPort",dstPort);
                tmp.put("protocol",protocol);

                jedis.lpush(key,tmp.toJSONString());
                System.out.println( "key:" + key + "  -->" + tmp.toJSONString());
                currentKeys.add(key);
                redisKeys.add(key);
            }
        }


        /**
         * 检查redis中的key是否在当前请求的key set 中
         */

        redisKeys.forEach((key) -> {
            //如果当前请求的key集合中没有redis中的key,说明该访问资源流表已经失效，要清除redis中的key并且把数据存入mysql
           if (!currentKeys.contains(key)) {
               //首先，会发送消息到计算信任度服务器，


               List<String> lrange = jedis.lrange(key, 0, -1);
               if (lrange == null) {
                   return;
               }
               String startData = lrange.get(0);
               JSONObject parseObject = JSONObject.parseObject(startData);
               String time = parseObject.getString("time");
               UserFlowData userFlowData = new UserFlowData();
               userFlowData.setKeyValue(key);
               userFlowData.setStartTime(time);
               ObjectMapper mapper = new ObjectMapper();
               try {
                   String jsonData = mapper.writeValueAsString(lrange);
                   userFlowData.setJsonData(jsonData);
               } catch (JsonProcessingException e) {
                   e.printStackTrace();
               }
               userFlowDataService.saveData(userFlowData);
               System.out.println(key + "已经储存到mysql数据库了" + "删除redis中的key");
               jedis.del(key);
               //将key从set中移除
               redisKeys.remove(key);
           }
        });

    }

    @PostMapping("/postDynamicData")
    public void postDynamicData(@RequestParam Map<String, String> params) {
        String data = params.get("data");
        JSONObject jsonObject = JSON.parseObject(data);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        jedis.lpush("dynamicData", data);
        System.out.println(format + "--------------------------------------------------------------------------------------------------");
        System.out.println(data);
    }

}
