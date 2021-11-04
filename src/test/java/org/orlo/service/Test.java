package org.orlo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
    @org.junit.Test
    public void test() {
        String data = "{ \"5\":[{\"time\":1614501911655,\"srcIP\":\"10.0.0.6/32\", \"srcMAC\":\"\", \"dstIP\":\"\", \"dstMAC\":\"00:0C:29:D8:8E:96\", \"packets\":78, \"bytes\":7644, \"life\":80}]}";
        System.out.println(data);
        Jedis jedis = new Jedis("192.168.1.36", 6379);
        JSONObject jsonObject = JSON.parseObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray(String.valueOf(5));
            int size = jsonArray.size();
            for (int j = 0; j < size; j++) {
                JSONObject oneData = jsonArray.getJSONObject(j);
                String srcIP = oneData.getString("srcIP");
                String srcMAC = oneData.getString("srcMAC");
                String dstIP = oneData.getString("dstIP");
                String dstMAC = oneData.getString("dstMAC");
                Long time = oneData.getLong("time");
                Long packets = oneData.getLong("packets");
                Long bytes = oneData.getLong("bytes");
                Long life = oneData.getLong("life");
                String key = "";
                if (!srcIP.equals("")) {
                    key = srcIP + ">" + dstMAC;
                } else if(!srcMAC.equals("")) {
                    key = srcMAC + ">" + dstIP;
                }
                JSONObject tmp = new JSONObject();
                tmp.put("time",time);
                tmp.put("packets",packets);
                tmp.put("bytes",bytes);
                tmp.put("life",life);
                jedis.lpush(key,tmp.toJSONString());
            }

    }

    @org.junit.Test
    public void Test2() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        System.out.println(hour + ":" + minute);
    }
}
