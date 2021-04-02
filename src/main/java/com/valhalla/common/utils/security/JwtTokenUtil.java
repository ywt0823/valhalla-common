package com.valhalla.common.utils.security;

import com.valhalla.common.BasicConstant;
import com.valhalla.common.utils.common.LogWrapperUtil;
import com.valhalla.common.utils.encryption.Base64Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
public class JwtTokenUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final Base64Utils base64Utils = new Base64Utils();
    private static final String privateKey = "valhalla";

    public static String generateToken(Map<String, Object> claims, Long expirationSeconds, TimeUnit timeUnit) {
        Date expirationDate = new Date(System.currentTimeMillis() + timeUnit.toMillis(expirationSeconds));
        String originToken = Jwts.builder()
                //负载
                .setClaims(claims)
                //签发时间
                .setIssuedAt(new Date())
                //过期时间
                .setExpiration(expirationDate)
                //加密方式
                .signWith(SignatureAlgorithm.HS512, privateKey)
                //生成
                .compact();
        return base64Utils.encrypt(originToken);
    }

    public static Claims parseToken(String token) {
        try {
            token = base64Utils.decrypt(token);
            return Jwts.parser()
                    .setSigningKey(privateKey)
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }

    }

    public static String parseTokenToUserName(String token) {
        Claims claims = parseToken(token);
        try {
            return String.valueOf(Objects.requireNonNull(claims).get(BasicConstant.CLAIM_USER_NAME));
        } catch (Exception e) {
            LOG.error(LogWrapperUtil.wrapperErrorLog(e));
            return null;
        }
    }

    public static String parseTokenToRealName(String token) {
        Claims claims = parseToken(token);
        try {
            return String.valueOf(Objects.requireNonNull(claims).get(BasicConstant.CLAIM_REAL_NAME));
        } catch (Exception e) {
            LOG.error(LogWrapperUtil.wrapperErrorLog(e));
            return null;
        }
    }

    public static String parseTokenToUserId(String token) {
        Claims claims = parseToken(token);
        try {
            return String.valueOf(Objects.requireNonNull(claims).get(BasicConstant.CLAIM_USER_ID));
        } catch (Exception e) {
            LOG.error(LogWrapperUtil.wrapperErrorLog(e));
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
            LOG.error(LogWrapperUtil.wrapperErrorLog(e));
            //过期
            return 403;
        }
    }

    /**
     * 从request的header中解析出对应的userId
     *
     * @param request
     * @return
     */
    public static String parseUserIdFromRequest(HttpServletRequest request) {
        String userId = "";
        try {
            String token = request.getHeader(BasicConstant.AUTHORIZATION_HEADER).substring("Bearer ".length());
            userId = parseTokenToUserId(token);
        } catch (Exception e) {
        }
        return userId;

    }

    /**
     * 从request的header中解析出对应的userName
     *
     * @param request
     * @return
     */
    public static String parseUserNameFromRequest(HttpServletRequest request) {
        String userName = "";
        try {
            String token = request.getHeader(BasicConstant.AUTHORIZATION_HEADER).substring("Bearer ".length());
            userName = parseTokenToUserName(token);
        } catch (Exception e) {
        }
        return userName;

    }

    /**
     * 从request的header中解析出对应的realName
     *
     * @param request
     * @return
     */
    public static String parseRealNameFromRequest(HttpServletRequest request) {
        String realName = "";
        try {
            String token = request.getHeader(BasicConstant.AUTHORIZATION_HEADER).substring("Bearer ".length());
            realName = parseTokenToRealName(token);
        } catch (Exception e) {
        }
        return realName;

    }

    /**
     * 判断是否是admin用户
     *
     * @param httpServletRequest httpServletRequest
     * @return
     */
    public static Boolean isAdminUser(HttpServletRequest httpServletRequest) {
        String userId = parseUserIdFromRequest(httpServletRequest);
        return "1".equals(userId);
    }
}
