package org.orlo.util;

import java.util.HashMap;

public class Helper {
    public static HashMap<String, String> macToDeviceId = new HashMap<>();
    static {
        for (int i = 0; i < 9; i++) {
            macToDeviceId.put("00:00:00:00:00:0" + (i + 1) , String.valueOf(i));
        }
        macToDeviceId.put("00:00:00:00:00:0A", "9");
        macToDeviceId.put("00:00:00:00:00:0B", "10");
        macToDeviceId.put("00:00:00:00:00:0C", "11");
    }

}
