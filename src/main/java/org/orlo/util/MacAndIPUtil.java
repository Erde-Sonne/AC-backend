package org.orlo.util;

import java.net.UnknownHostException;

public class MacAndIPUtil {
    public static long macToLong(String mac) {
        String replace = mac.replace(":", "");
        return Long.parseLong(replace, 16);
    }

    public static String longToMac(long mac) {
        String hexString = Long.toHexString(mac);
        char[] array = hexString.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        int cnt = 0;
        for(int i = 0; i < array.length; i++) {
            stringBuilder.append(array[i]);
            cnt++;
            if(i != array.length -1 && cnt % 2 == 0) {
                stringBuilder.append(":");
            }
        }
        return stringBuilder.toString();
    }

    public static long ipToLong(String ipAddress) {

        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {

            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);

        }

        return result;
    }
    /* http://www.java2s.com/*/
    public static String longToIp(long ip) {
        StringBuilder sb = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));

            if (i < 3) {
                sb.insert(0, '.');
            }

            ip >>= 8;
        }

        return sb.toString();
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(macToLong("ff:ff:ff:ff:ff:ff"));
        System.out.println(longToMac(281474976710655L));
        System.out.println(ipToLong("192.168.1.49"));
        System.out.println(longToIp(3232235825L));
    }
}
