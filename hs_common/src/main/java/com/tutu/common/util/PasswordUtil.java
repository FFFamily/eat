package com.tutu.common.util;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

public class PasswordUtil {
    private static final String LEGACY_AES_KEY = "e10adc3949ba59a";
    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.*");
    private static final Pattern MD5_PATTERN = Pattern.compile("^[a-fA-F0-9]{32}$");

    /**
     * 生成不可逆哈希（BCrypt）
     */
    public static String encode(String password) {
        return BCRYPT_ENCODER.encode(password);
    }

    /**
     *
     * @param password
     * @return
     */
    /**
     * 仅用于兼容旧 AES 密码的解密（BCrypt 无法解密）
     */
    public static String decode(String password) {
        try {
            return SaSecureUtil.aesDecrypt(LEGACY_AES_KEY, password);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param oldPassword
     * @param realPassword
     * @return
     */
    /**
     * 密码校验（支持 BCrypt / 旧 AES / 旧 MD5）
     */
    public static boolean match(String rawPassword, String storedPassword) {
        if (StrUtil.isBlank(rawPassword) || StrUtil.isBlank(storedPassword)) {
            return false;
        }
        if (isBcrypt(storedPassword)) {
            return BCRYPT_ENCODER.matches(rawPassword, storedPassword);
        }
        if (isMd5(storedPassword)) {
            return SaSecureUtil.md5(rawPassword).equalsIgnoreCase(storedPassword);
        }
        String legacy = decode(storedPassword);
        return rawPassword.equals(legacy);
    }

    /**
     * 是否需要升级为 BCrypt
     */
    public static boolean needsUpgrade(String storedPassword) {
        return StrUtil.isNotBlank(storedPassword) && !isBcrypt(storedPassword);
    }

    private static boolean isBcrypt(String storedPassword) {
        return BCRYPT_PATTERN.matcher(storedPassword).matches();
    }

    private static boolean isMd5(String storedPassword) {
        return MD5_PATTERN.matcher(storedPassword).matches();
    }
}
