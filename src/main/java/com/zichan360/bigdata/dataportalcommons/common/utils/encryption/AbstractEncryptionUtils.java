package com.zichan360.bigdata.dataportalcommons.common.utils.encryption;

import java.util.Map;

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
    protected String encryptPassword(String password) {
        return null;
    }

    /**
     * 解密
     *
     * @param encryptionPassword 加密密码
     * @return 解密后的密码
     */
    protected String decipheringPassword(String encryptionPassword) {
        return null;
    }

    /**
     * 随机生成密码
     *
     * @return 随机生成的密码和加密后的密码
     */
    protected Map<String, String> randomPassword() {
        return null;
    }
}
