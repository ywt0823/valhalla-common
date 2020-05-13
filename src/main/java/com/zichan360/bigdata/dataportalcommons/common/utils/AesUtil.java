package com.zichan360.bigdata.dataportalcommons.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author: sunweihong
 * @date: 2018/4/24 11:27
 * @description: AES对数据加密解密
 */
public class AesUtil {
    private static final Logger LOG = LogManager.getLogger(AesUtil.class);

    /**
     * 加密
     * @param sSrc  加密字段
     * @param sKey  key秘钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String sKey) {
        try {
            if (sKey == null) {
                LOG.info("Key为空null");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return AesUtil.parseByte2HexStr(encrypted);
        } catch (Exception e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
            return null;
        }
    }
    /**
     * 解密
     * @param sSrc  解密字段
     * @param sKey  key秘钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                LOG.info("Key为空null");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = AesUtil.parseHexStr2Byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                LOG.error(CommonUtil.wrapperErrorLog(e));
                return null;
            }
        } catch (Exception ex) {
            LOG.info(ex.toString());
            return null;
        }
    }


    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}