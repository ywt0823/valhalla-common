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
import java.util.HashMap;
import java.util.Map;
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
            Claims claims = JwtTokenUtil.parseToken(jwtToken);
            if (Optional.ofNullable(claims).isPresent()) {
                Date expiredDate = claims.getExpiration();
                Date nowDate = new Date();
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
            CommonUtil.wrapperErrorLog(e);
            //过期
            return 403;
        }
    }

    public static void main(String[] args) {
        Map c = new HashMap<>();
        c.put("name","1");
        c.put("pa",2);
        System.out.println(generateToken(c, 1000L));
        Integer map = checkToken("728F461C057A6974E5F91B5E2D681395E9C3B9B27A90F2774770FB5130F8AE0B0263BB7F3A15C5E12EF2289260FF4C38581F6983641D0CF7C2BE9A9D40D739869E3662E0208A30C3E5A03A0CA0D3EB82C4B26BC77151BFCAA58F05C4E4614E653D018BBA0509204356BEBBA859C3C83B36379D512DA35B58F6BB0570E92BBCE3321721697DAC1F19CED5DFC1DB5A631C3554888F7DE0378715E8DB42775FCCCB9CF6D8EB3C8E530224EDABCA11FAE5C498EE5C92D543CB8764C1970A04EAA2AAF8F09DA6520EA7C5086AC907AB5C489EC91925F1418BE80ADC964FDF3914FA7A656485571176D53A8EC63B41C145D89635B061C02961C02BB1348C56A20089BC622108C651A881D56E30353BFE359AFE");
        System.out.println(map);
    }

}
