package com.ywt.valhalla.common.utils.encryption;

/**
 * @description: 加解密抽象类
 * @author: ywt
 * @date: 2020-05-12 13:10:38
 **/
public abstract class AbstractEncryptionUtils {

    /**
     * 加密
     *
     * @param password 密码
     * @return 加密后的字符串
     */
    protected String encrypt(String password) {
        return null;
    }

    /**
     * 解密
     *
     * @param encryptionPassword 加密密码
     * @return 解密后的密码
     */
    protected String decrypt(String encryptionPassword) {
        return null;
    }

}
