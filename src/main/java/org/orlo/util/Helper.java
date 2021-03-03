package org.orlo.util;

import java.util.HashMap;

public class Helper {
    public static HashMap<String, String> ipToDeviceId = new HashMap<>();
    static {
        for (int i = 0; i < 12; i++) {
            ipToDeviceId.put("10.0.0." + (i + 1) , String.valueOf(i));
        }
    }

}
