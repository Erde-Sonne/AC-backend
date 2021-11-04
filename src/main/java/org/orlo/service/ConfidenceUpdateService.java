package org.orlo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.orlo.repository.UserVerifyRepository;
import org.orlo.task.base.TaskConfig;
import org.orlo.util.MacAndIPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Set;

@Service
public class ConfidenceUpdateService {
    private static final Jedis jedis = new Jedis(TaskConfig.REDIS_IP, 6379);
    private static final double beta = 0.5;
    private static final String DEFAULT_THRESHOLD = "0.2";
    private static final String DEFAULT_CONFIDENCE = "5";

    @Autowired
    UserVerifyRepository userVerifyRepository;
    @Autowired
    IPConfidenceDataService ipConfidenceDataService;

    public void handleJson(String msg) {
        jedis.select(5);
        JSONObject jsonObject = JSON.parseObject(msg);
        Set<String> keys = jsonObject.keySet();
        for(String key : keys) {
            String[] split = key.split("-");
            String mac = split[0];
            String ip = split[1];
            JSONArray array = jsonObject.getJSONArray(key);
            double tmpSum = 0.0;
            int size = array.size();
            for(int i = 0; i < size; i++) {
                tmpSum += array.getDouble(i);
            }
            double score = tmpSum / size;
            String thresholdStr = jedis.hget("threshold", key);
            if(thresholdStr == null) {
                //从数据库取出threshold
                jedis.hset("threshold", key, DEFAULT_THRESHOLD);
                thresholdStr = DEFAULT_THRESHOLD;
            }
            double threshold = Double.parseDouble(thresholdStr);
            String confidenceStr = jedis.hget("confidence", key);
            if(confidenceStr == null) {
                confidenceStr = DEFAULT_CONFIDENCE;
            }
            double confidence = Double.parseDouble(confidenceStr);
            System.out.println("score:" + score + "    threshold:" +threshold);
            if(score > threshold) {
                System.out.println("数据异常！！！");
                confidence = confidence - beta * Math.abs(score - threshold);
            } else {
                System.out.println("数据正常！！！");
                confidence = confidence + beta * Math.abs(score - threshold);
//                confidence = confidence + 0.1;
            }
            jedis.hset("confidence", key, String.valueOf(confidence));
//            System.out.println(MacAndIPUtil.macToLong(mac));
//            System.out.println(MacAndIPUtil.ipToLong(ip));
 /*           IPConfidenceData row = ipConfidenceDataService.getRow
                    (MacAndIPUtil.macToLong(mac), MacAndIPUtil.ipToLong(ip));
            if(row == null) {
                System.out.println("add the new row to db");
                IPConfidenceData confidenceData = new IPConfidenceData();
                confidenceData.setMac(MacAndIPUtil.macToLong(mac)).setIp(MacAndIPUtil.ipToLong(ip)).
                        setConfidence(confidence).setThreshold(threshold);
                ipConfidenceDataService.addRow(confidenceData);
            } else{
                ipConfidenceDataService.updateRow(row);
            }*/
            System.out.println("用户到资源键:" + key + "  信任度值:" + confidence);
        }
    }
}
