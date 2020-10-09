package com.zichan360.bigdata.dataportalcommons.common.utils.httpclient;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.istack.NotNull;
import com.zichan360.bigdata.dataportalcommons.common.utils.common.LogWrapperUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Httpclient工具类，在引用时记得把log等级调成debug或者更低，否则会导致日志打印很多信息 (log4j.logger.org.apache.commons.httpclient=DEBUG)
 *
 * @author ywt
 * @date 2019年2月9日 09:00:02
 * @since 1.3.9
 **/
public class HttpClientUtil {
    /**
     * 表单提交
     *
     * @param url      请求路径
     * @param map      参数K-V
     * @param encoding 编码
     * @param token    Token
     * @return 返回结果
     */
    public static JSONObject sendPostByFormData(@NotNull final String url, final Map<String, String> map, final String encoding, final String token) throws MalformedURLException {
        return getResult(Objects.requireNonNull(sendData(new URL(url), map, ContentType.APPLICATION_FORM_URLENCODED, (apiUrl, body, contentType) -> {
            // 创建post方式请求对象
            HttpPost httpPost = getHttpPost(url);
            // 装填参数
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            // 设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, encoding));
            // 设置header信息
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setHeader("Content-type", contentType.getMimeType());
            if (!StringUtils.isEmpty(token)) {
                httpPost.setHeader("Authorization", "Bearer " + token);
            }
            return HttpClients.createDefault().execute(httpPost);
        })), encoding);
    }


    /**
     * body 提交
     *
     * @param url      请求路径
     * @param json     参数
     * @param encoding 编码
     * @param token    Token
     * @return 返回结果
     * @throws MalformedURLException
     */
    public static JSONObject sendPostDataByJson(@NotNull final String url, final String json, final String encoding, final String token) throws MalformedURLException {
        return getResult(Objects.requireNonNull(sendData(new URL(url), json, ContentType.APPLICATION_FORM_URLENCODED, (apiUrl, body, contentType) -> {
            // 创建post方式请求对象
            HttpPost httpPost = getHttpPost(url);
            // 设置参数到请求对象中
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            stringEntity.setContentEncoding(encoding.isEmpty() ? ContentType.APPLICATION_JSON.toString() : encoding);
            httpPost.setEntity(stringEntity);
            if (!StringUtils.isEmpty(token)) {
                httpPost.setHeader("Authorization", "Bearer " + token);
            }
            // 设置header信息
            return HttpClients.createDefault().execute(httpPost);
        })), encoding);
    }


    /**
     * GET提交
     *
     * @param url      请求路径
     * @param encoding 编码
     * @param token    token
     * @return 返回结果
     * @throws MalformedURLException
     */
    public static JSONObject sendGetData(@NotNull final String url, final String encoding, final String token) throws MalformedURLException {
        // 通过请求对象获取响应对象
        return getResult(Objects.requireNonNull(sendData(new URL(url), null, null, (apiUrl, body, contentType) -> {
            HttpGet httpGet = getHttpGet(url);
            if (!StringUtils.isEmpty(token)) {
                httpGet.setHeader("Authorization", "Bearer " + token);
            }
            return HttpClients.createDefault().execute(httpGet);
        })), encoding);
    }


    /**
     * 获取HttpPost实体类
     *
     * @param url 路径
     * @return
     */
    private static HttpPost getHttpPost(String url) {
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000).build();
        httpPost.setConfig(requestConfig);
        return httpPost;
    }


    /**
     * 获取HttpGet实体类
     *
     * @param url 路径
     * @return
     */
    private static HttpGet getHttpGet(String url) {
        // 创建post方式请求对象
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000).build();
        httpGet.setConfig(requestConfig);
        return httpGet;
    }

    private static CloseableHttpResponse sendData(URL apiUrl, Object body, ContentType contentType, HttpResult httpResult) {
        try {
            return httpResult.apply(apiUrl, body, contentType);
        } catch (Exception e) {
            LogWrapperUtil.wrapperErrorLog(e);
            return null;
        }
    }


    private static JSONObject getResult(CloseableHttpResponse response, String encoding) {
        String result = "";
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                result = EntityUtils.toString(response.getEntity(), encoding);
            } catch (IOException e) {
                LogWrapperUtil.wrapperErrorLog(e);
            }
        } else {
            LogWrapperUtil.wrapperNormalLog(JSON.parseObject(JSON.toJSONString(response)).toJSONString());
        }
        // 释放链接
        try {
            response.close();
        } catch (IOException e) {
            LogWrapperUtil.wrapperErrorLog(e);
        }
        return JSON.parseObject(result);
    }

}
