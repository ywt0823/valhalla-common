package com.zichan360.bigdata.dataportalcommons.common.utils.encryption;

import java.util.*;

/**
 * @description: Base64加密解密工具类
 * @author: ywt
 * @date: 2020-05-12 17:08:10
 **/
public class Base64Utils extends AbstractEncryptionUtils{

    /**
     * 加密
     *
     * @param password 密码
     * @return 加密后的字符串
     */
    @Override
    protected String encrypt(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    /**
     * 解密
     *
     * @param encryptionPassword 加密密码
     * @return 解密后的密码
     */
    @Override
    protected String decrypt(String encryptionPassword) {
        return Arrays.toString(Base64.getDecoder().decode(encryptionPassword));
    }

}
