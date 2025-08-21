package com.tutu.common.util;

import cn.dev33.satoken.secure.SaSecureUtil;

public class PasswordUtil {
    private static final String key = "e10adc3949ba59a";
    /**
     *
     * @param password
     * @return
     */
    // GghQPOYAw5C1B8VWXROXOw== test
    public static String encode(String password){
        return SaSecureUtil.aesEncrypt(key, password);
    }

    /**
     *
     * @param password
     * @return
     */
    public static String decode(String password){
        try {
            return SaSecureUtil.aesDecrypt(key, password);
        }catch (Exception e){
            return null;
        }
    }

    /**
     *
     * @param oldPassword
     * @param realPassword
     * @return
     */
    public static boolean match(String oldPassword,String realPassword){
        return SaSecureUtil.aesDecrypt(key, oldPassword).equals(realPassword);
    }
}
