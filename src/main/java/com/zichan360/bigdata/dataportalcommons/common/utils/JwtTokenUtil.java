package com.zichan360.bigdata.dataportalcommons.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
                //负载
                .setClaims(claims)
                //签发时间
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(expirationDate)
                //加密方式
                .signWith(SignatureAlgorithm.RS256, privateKey)
                //生成
                .compact();
        return AesUtil.encrypt(originToken, reEncryptionKey);
    }

    public static Claims parseToken(String token) {
        try {
            token = AesUtil.decrypt(token, reEncryptionKey);
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }

    }

    public static String parseTokenToUserName(String token) {
        Claims claims = parseToken(token);
        try {
            return String.valueOf(Objects.requireNonNull(claims).get("userName"));
        } catch (Exception e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
            return null;
        }
    }

    public static String parseTokenToRealName(String token) {
        Claims claims = parseToken(token);
        try {
            return String.valueOf(Objects.requireNonNull(claims).get("realName"));
        } catch (Exception e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
            return null;
        }
    }

    public static String parseTokenToUserId(String token) {
        Claims claims = parseToken(token);
        try {
            return String.valueOf(Objects.requireNonNull(claims).get("userId"));
        } catch (Exception e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
            return null;
        }
    }

    /**
     * 检查token
     *
     * @return
     */
    public static Integer checkToken(String jwtToken) {
        try {
            Claims claims = JwtTokenUtil.parseToken(jwtToken);
            if (Optional.ofNullable(claims).isPresent()) {
                Date expiredDate = claims.getExpiration();
                //过期时间前5min
                Date nowDate = new Date(System.currentTimeMillis() - 300000L);
                if (expiredDate.after(nowDate)) {
                    //没有过期
                    return 200;
                } else {
                    //过期
                    return 403;
                }
            } else {
                //token中没有信息,过期
                return 403;
            }
        } catch (ExpiredJwtException e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
            //过期
            return 403;
        }
    }
}
