package com.zichan360.bigdata.dataportalcommons.common.utils.encryption;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description: MD5加解密工具类
 * @author: ywt
 * @date: 2020-05-12 13:02:34
 **/
public class Md5Utils extends AbstractEncryptionUtils {


    /**
     * 加密
     *
     * @param password 密码
     * @return 加密后的字符串
     */
    @Override
    public String encrypt(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            char[] passwordArray = password.toCharArray();
            byte[] byteArray = new byte[passwordArray.length];
            for (int i = 0; i < passwordArray.length; i++) {
                byteArray[i] = (byte) passwordArray[i];
            }
            byte[] md5Bytes = messageDigest.digest(byteArray);
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 随机生成密码
     *
     * @return 密码
     */
    public Map<String, String> randomPassword() {
        Random random = new Random();
        Map<String, String> result = new HashMap<>(2);
        StringBuilder sb = new StringBuilder();
        int passwordRandomLength = 6;
        for (int i = 0; i <= passwordRandomLength; i++) {
            String str = "abcdefghijklmnopqrstuvwxyz0123456789";
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        result.put("password", sb.toString());
        result.put("encryptPassword", this.encrypt(sb.toString()));
        return result;
    }
}
