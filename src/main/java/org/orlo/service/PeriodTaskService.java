package org.orlo.service;

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
    private static final Jedis jedis = new Jedis("127.0.0.1", 6379);

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
        jedis.select(1);
        Set<String> forbiddenSet = new HashSet<>();
        Timer timer = new Timer();
/*        timer.schedule(new TimerTask() {
            public void run() {
                Map<String, String> confidences = jedis.hgetAll("confidence");
                for(String key : confidences.keySet()) {
                    String value = confidences.get(key);
                    if(Float.parseFloat(value) < 4.8 && !forbiddenSet.contains(key)) {
                        forbiddenSet.add(key);
                        MySend.sendMsgToController("4", key);
                    }
                }
            }
        }, 1000, 1000);// 设定指定的时间time,此处为2000毫秒*/
        timer.schedule(new TimerTask() {
            public void run() {
                MySend.sendMsgToController("4", "8C:16:45:85:20:60-202.115.22.251");
                System.out.println("!!!!!!!!!!!!!!!!");
            }
        }, 1000, 10000);// 设定指定的时间time,此处为2000毫秒
    }
}
