package org.orlo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.orlo.repository.UserVerifyRepository;
import org.orlo.util.MySend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class ConfidenceUpdateService {
    static Jedis jedis = new Jedis("127.0.0.1", 6379);

    @Autowired
    UserVerifyRepository userVerifyRepository;


    public void handleJson(String msg) {
        JSONObject jsonObject = JSON.parseObject(msg);
        JSONArray data = jsonObject.getJSONArray("data");
        for(int i = 0; i < data.size(); i++) {
            JSONObject dataJSONObject = data.getJSONObject(i);
            String key = dataJSONObject.getString("key");
            String value = dataJSONObject.getString("value");
            String[] split = key.split(">");
            String mac = split[0];
            String ip = split[1];
            String threshold = jedis.hget(mac + "threshold", ip);
            if(threshold == null) {
                //从数据库取出threshold
                jedis.hset(mac + "threshold", ip, "5");
                threshold = "5";
            }
            jedis.hset(mac + "confidence", ip, value);
            if(Integer.parseInt(value) < Integer.parseInt(threshold)) {
                System.out.println("error  confidence low");
                MySend.sendMsgToController("4", key);
            } else {
                System.out.println("normal");
            }
        }


    }
}
