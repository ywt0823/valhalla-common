package com.zichan360.bigdata.dataportalcommons.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 */
public class JwtTokenUtil {

    private static final Logger LOG = LogManager.getLogger(JwtTokenUtil.class);
    private static final String reEncryptionKey = "3nTBnYv3xqhihQwI_W5H9hIwSfGHsvqs";
    private static final char[] jksKeyPass = "LQjbl3_Csc52PDUWdmfa6KxwARonhJ7N".toCharArray();
    private static final char[] jksStorePass = "Jo_rUjYwW40Gb6uAFi7s1ZzalhEcRyKm".toCharArray();
    private static final String alias = "jwt";

    /**
     * 寻找证书文件
     */
    private static InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jwt.jks");
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static {
        try {
            //将jwt的密码存储在jks文件中
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, jksKeyPass);
            privateKey = (PrivateKey) keyStore.getKey(alias, jksStorePass);
            publicKey = keyStore.getCertificate(alias).getPublicKey();
        } catch (Exception e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
        }
    }

    public static String generateToken(Map<String, Object> claims, Long expirationSeconds) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationSeconds * 3600L);
        String originToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        return AesUtil.encrypt(originToken, reEncryptionKey);
    }

    public static Map<String, Object> parseToken(String token) {
        Map<String, Object> claims = new HashMap<>();
        try {
            token = AesUtil.decrypt(token, reEncryptionKey);
            claims = Jwts.parser()
                    .setSigningKey(publicKey)
//                    .requireExpiration(new Date(System.currentTimeMillis()))
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
        }
        return claims;
    }

    public static String parseTokenToUserName(String token) {
        Map<String, Object> claims = new HashMap<>();
        String userName = "";
        try {
            token = AesUtil.decrypt(token, reEncryptionKey);
            claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token).getBody();
            userName = (String) claims.get(BasicConstant.CLAIM_USER_IDENTIFICATION);
        } catch (Exception e) {
        }
        return userName;
    }

    public static String parseTokenToRoleId(String token) {
        String roleId = "0";
        try {
            token = AesUtil.decrypt(token, reEncryptionKey);
            Map<String, Object> claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token).getBody();
            roleId = (String) claims.get(BasicConstant.CLAIM_ROLE_IDENTIFICATION);
        } catch (Exception e) {

        }
        return roleId;

    }

    public static String parseTokenToRealName(String token) {
        String userNameCn = "";
        try {
            token = AesUtil.decrypt(token, reEncryptionKey);
            Map<String, Object> claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token).getBody();
            userNameCn = (String) claims.get(BasicConstant.CLAIM_USER_CN_IDENTIFICATION);
        } catch (Exception e) {

        }
        return userNameCn;

    }

    /**
     * 检查token
     *
     * @return
     */
    public static Integer checkToken(String jwtToken) {
        try {
            Map<String, Object> claims = JwtTokenUtil.parseToken(jwtToken);
            if (claims.isEmpty()){
                return 401;
            }
            Long expiration = new Long((Integer) claims.get("exp"));
            Long currentTime = System.currentTimeMillis();
            if (currentTime > expiration) {
                return 200;
            } else {
                return 403;
            }
        }catch (Exception e){
            CommonUtil.wrapperErrorLog(e);
            return 401;
        }
    }

}
