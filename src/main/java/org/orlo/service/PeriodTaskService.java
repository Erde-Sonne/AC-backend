package org.orlo.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orlo.task.PeriodicalSocketClientTask;
import org.orlo.task.base.TaskConfig;
import org.orlo.util.MySend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PeriodTaskService {
    private static final Jedis jedis = new Jedis(TaskConfig.REDIS_IP, 6379);

    @Autowired
    ConfidenceUpdateService confidenceUpdateService;

    public void runLOF() {
        PeriodicalSocketClientTask.RequestGenerator requestGenerator = () -> {
            return  "default";
//            return "key,202.115.22.251/32>8C:16:45:85:20:60,8C:16:45:85:20:60>202.115.22.251/32";
//            System.out.println("**********");
        };
        PeriodicalSocketClientTask.ResponseHandler responseHandler = (String resp) -> {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = df.format(new Date());
            System.out.println(format + "-----------------------------------------------------------");
            assert confidenceUpdateService != null;
            System.out.println(resp);
            confidenceUpdateService.handleJson(resp);
        };

        //间隔20s向信任度计算服务器发出计算请求
        PeriodicalSocketClientTask socketClientTask = new PeriodicalSocketClientTask(TaskConfig.LOF_IP,
                TaskConfig.LOF_PORT, requestGenerator, responseHandler);
        socketClientTask.setDelay(1);
        socketClientTask.setInterval(20);
        socketClientTask.start();
    }


    public void checkConfidence() {
        jedis.select(5);
        Set<String> forbiddenSet = new HashSet<>();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Map<String, String> confidences = jedis.hgetAll("confidence");
                for(String key : confidences.keySet()) {
                    String value = confidences.get(key);
                    if(Float.parseFloat(value) < 4.1 && !forbiddenSet.contains(key)) {
                        forbiddenSet.add(key);
                        System.out.println("信任度太低，访问此资源的权限不足，访问的连接将被断掉！！！");
                        System.out.println("请联系管理员更新信任值........");
                        MySend.sendMsgToController("4", key);
                        jedis.select(6);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String format = df.format(new Date());
                        jedis.lpush("msgQueue", format + "," + key);
                        jedis.select(5);
                    }
                }
            }
        }, 1000, 10000);// 设定指定的时间time,此处为2000毫秒
       /* timer.schedule(new TimerTask() {
            public void run() {
                MySend.sendMsgToController("4", "8C:16:45:85:20:60-202.115.22.251");
                System.out.println("!!!!!!!!!!!!!!!!");
            }
        }, 1000, 10000);// 设定指定的时间time,此处为2000毫秒*/
    }


    public void handleLink() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
              jedis.select(7);
              Set<String> keys = jedis.keys("*");
              HashMap<String, float[]> map = new HashMap<>();
              for (String key : keys) {
                  String[] split = key.split("\\.");
                  float[] lossAndDelay = map.getOrDefault(split[0], new float[]{0.001f, 0.0f});
                  float v = Float.parseFloat(jedis.get(key));
                  if("loss".equals(split[1])) {
                      lossAndDelay[0] = v;
                  } else {
                      lossAndDelay[1] = v;
                  }
                  map.put(split[0], lossAndDelay);
              }
                try {
                    String value = new ObjectMapper().writeValueAsString(map);
                    jedis.select(2);
                    jedis.set("lossAndDelay", value);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 5000);// 设定指定的时间time,此处为5000毫秒
    }
}
