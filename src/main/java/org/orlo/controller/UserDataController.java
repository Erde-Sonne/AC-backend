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
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.select(1);
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
                handlePerFlowData(jedis, key, time, packets, bytes, life, maxflow, srcPort, dstPort, protocol);
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
            }
        }

        /**
         * 检查redis中的key是否在当前请求的key set 中
         */
        jedis.select(1);
        Set<String> redisKeys = jedis.keys("*");
        for (String key : redisKeys) {
            //如果当前请求的key集合中没有redis中的key,说明该访问资源流表已经失效，要清除redis中的key并且把数据存入mysql
            if (!currentKeys.contains(key)) {
                //首先，会发送消息到计算信任度服务器，
//               MySend.sendMsgToLOF("save," + key);

                List<String> lrange = jedis.lrange(key, 0, -1);
                if (lrange == null || lrange.size() == 0) {
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
                //key 5s后过期
                jedis.expire(key, 5);
//               jedis.del(key);
            }
        }

        jedis.select(9);
        for(String key : jedis.keys("*")) {
            if(currentKeys.contains(key)) {
                continue;
            }
            jedis.del(key);
        }
    }

    @PostMapping("/postDynamicData")
    public void postDynamicData(@RequestParam Map<String, String> params) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.select(2);
        String data = params.get("data");
        JSONObject jsonObject = JSON.parseObject(data);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        jedis.lpush("dynamicData", data);
        System.out.println(format + "--------------------------------------------------------------------------------------------------");
        System.out.println(data);
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
                                   long maxflow, String srcPort, String dstPort, String protocol) {
        jedis.select(3);
        if(!jedis.exists(key)) {
            //设置过期时间为1天
            jedis.setex(key, 86400, "0");
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
        double averageFlowRate = bytes * 1.0 / life;
        JSONObject data = new JSONObject();
        data.put("visitCnt", visitCnt);
        data.put("srcPort",srcPort);
        data.put("dstPort",dstPort);
        data.put("averageFlowRate", averageFlowRate);
        data.put("maxflow",maxflow);
        data.put("time",time);
        data.put("life",life);
        data.put("protocol",protocol);
        jedis.select(9);
        //存储数据
        jedis.set(key, data.toJSONString());
        jedis.select(1);
    }
}
