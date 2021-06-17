package org.orlo.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private static final String salt = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassToFormPass(String inputPass) {
//        String src = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(4) + salt.charAt(6);
        return md5(inputPass);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String src = salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(4) + salt.charAt(6);
        return md5(src);
    }

    public static String inputPassToDBPass(String inputPass) {
       String formPass = inputPassToFormPass(inputPass);
       return formPassToDBPass(formPass, salt);
    }
}
