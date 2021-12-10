package org.orlo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orlo.entity.UserData;
import org.orlo.entity.UserFlowData;
import org.orlo.service.ConfidenceUpdateService;
import org.orlo.service.PeriodTaskService;
import org.orlo.service.UserDataService;
import org.orlo.service.UserFlowDataService;
import org.orlo.task.KafkaListenerTask;
import org.orlo.task.PeriodicalSocketClientTask;
import org.orlo.task.SocketClientTask;
import org.orlo.task.base.TaskConfig;
import org.orlo.util.MySend;
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

    @Autowired
    UserDataService userDataService;
    @Autowired
    UserFlowDataService userFlowDataService;

    static String REDISHOST = TaskConfig.REDIS_IP;

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
        Jedis jedis = new Jedis(REDISHOST, 6379);
        String data = params.get("data");
//        System.out.println(data);
        JSONObject jsonObject = JSON.parseObject(data);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        System.out.println(format + "--------------------------------------------------------------------------------------------------");
        //每一次上传数据时，将key存为一个set
        HashSet<String> currentKeys = new HashSet<>();
        for(int i = 0; i < 25; i++) {
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
                double maxflow = 0L;
                double avgflow = 0L;
                String key = "";
                if (!srcIP.equals("")) {
                    key = srcIP + ">" + dstMAC;
                } else if(!srcMAC.equals("")) {
                    key = srcMAC + ">" + dstIP;
                }
                String preData = jedis.get(key);
                //取出前一条数据
                if (preData != null) {
                    JSONObject preJson = JSONObject.parseObject(preData);
                    Long preBytes = preJson.getLong("bytes");
                    avgflow = Math.abs(bytes - preBytes) / 10.0;
                    Long preMaxflow = preJson.getLong("maxflow");
                    maxflow = Math.max(avgflow, preMaxflow);
                } else {
                    //该流第一条数据
                    avgflow = bytes / 10.0;
                    maxflow = avgflow;
                }
                handlePerFlowData(jedis, key, time, packets, bytes, life, avgflow, maxflow, srcPort, dstPort, protocol);
                jedis.select(1);
                JSONObject tmp = new JSONObject();
                tmp.put("time",time);
                tmp.put("packets",packets);
                tmp.put("bytes",bytes);
                tmp.put("life",life);
                tmp.put("maxflow",maxflow);
                tmp.put("srcPort",srcPort);
                tmp.put("dstPort",dstPort);
                tmp.put("protocol",protocol);
                tmp.put("avgflow", avgflow);
                jedis.set(key,tmp.toJSONString());
//                System.out.println( "key:" + key + "  -->" + tmp.toJSONString());
                currentKeys.add(key);
            }
        }

        /**
         * 检查redis中的key是否在当前请求的key set 中
         */
//        for (String key : currentKeys) {
//            System.out.print(key + "  ");
//        }
       jedis.select(1);
        Set<String> redisKeys = jedis.keys("*");
        for (String key : redisKeys) {
            //如果当前请求的key集合中没有redis中的key,说明该访问资源流表已经失效，要清除redis中的key并且把数据存入mysql
            if (!currentKeys.contains(key)) {
                //首先，会发送消息到计算信任度服务器，
//               MySend.sendMsgToLOF("save," + key);
//                System.out.println("删除redis中的key:" + key);
                jedis.expire(key, 3);
            }
        }
        jedis.select(9);
        for(String key : jedis.keys("*")) {
            if(currentKeys.contains(key)) {
                continue;
            }
            jedis.expire(key, 3);
        }

        jedis.select(10);
        for(String key : jedis.keys("*")) {
            if(currentKeys.contains(key)) {
                continue;
            }
            jedis.expire(key, 3);
        }
    }

    @PostMapping("/postDynamicData")
    public void postDynamicData(@RequestParam Map<String, String> params) {
        Jedis jedis = new Jedis(REDISHOST, 6379);
        jedis.select(2);
        String data = params.get("data");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        jedis.set("dynamicData", data);
//        System.out.println(format + "--------------------------------------------------------------------------------------------------");
//        System.out.println(data);
        handleDynamic(jedis, data);
    }

    /**
     * 处理交换机动态数据，为了界面的显示
     * @param data
     */
    private void handleDynamic(Jedis jedis, String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Set<String> keys = jsonObject.keySet();
        JSONObject showJson = new JSONObject();
        for(String key : keys) {
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            JSONObject tmp = new JSONObject();
            tmp.put("averagepkts", jsonArray.getLong(0));
            tmp.put("maxpkts", jsonArray.getLong(1));
            tmp.put("averagebytes", jsonArray.getLong(2));
            tmp.put("maxbytes", jsonArray.getLong(3));
            tmp.put("totalbytes", jsonArray.getLong(4));
            tmp.put("totalflownums", jsonArray.getLong(5));
            tmp.put("totalflowrate", jsonArray.getLong(6));
            showJson.put(key, tmp.toJSONString());
        }
        jedis.set("dynamicDataShow", showJson.toJSONString());
    }


    /**
     * 处理单条流的数据，用于界面显示
     * @param time
     * @param packets
     * @param bytes
     * @param life
     * @param maxflow
     * @param srcPort
     * @param dstPort
     * @param protocol
     */
    private void handlePerFlowData(Jedis jedis, String key, long time, long packets, long bytes, long life,
                                   double avgflow, double maxflow, String srcPort, String dstPort, String protocol) {
        jedis.select(3);
        if(!jedis.exists(key)) {
            //设置过期时间为1天
            jedis.setex(key, 1800, "0");
        }
        int visitCnt = Integer.parseInt(jedis.get(key));
        jedis.select(1);
        Boolean exists = jedis.exists(key);
        if(!exists) {
            //访问次数加1
            visitCnt++;
            jedis.select(3);
            jedis.set(key, String.valueOf(visitCnt));
        }
        JSONObject data = new JSONObject();
        data.put("visitCnt", visitCnt);
        data.put("srcPort",srcPort);
        data.put("dstPort",dstPort);
        data.put("averageFlowRate", avgflow);
        data.put("maxflow",maxflow);
        data.put("time",time);
        data.put("life",life);
        data.put("protocol",protocol);
        jedis.select(9);
        //存储数据
        jedis.set(key, data.toJSONString());
        jedis.select(10);
        //存储数据
        jedis.lpush(key, data.toJSONString());
    }


    @PostMapping("/path")
    public void path(@RequestParam Map<String, String> params) {
        Jedis jedis = new Jedis(REDISHOST, 6379);
        jedis.select(8);
        String macAddress = params.get("macAddress");
        String path = params.get("path");
        //去掉首尾的[]
        path = path.substring(1, path.length() - 1);
        String safeRoute = params.get("safeRoute");
        String costTime = params.get("costTime");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", path);
        jsonObject.put("safeRoute", safeRoute);
        jsonObject.put("costTime", costTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        jedis.set(macAddress, jsonObject.toJSONString());
//        System.out.println(format + "--------------------------------------------------------------------------------------------------");
//        System.out.println(params);
    }
}
