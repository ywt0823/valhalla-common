package com.ywt.valhalla.common.utils.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.URL;

/**
 * HttpClient返回函数式接口
 *
 * @author ywt
 * @date 2020年9月23日 14:29:06
 * @since 1.3.9
 */
@FunctionalInterface
public interface HttpResult {
    /**
     * 运行返回结果
     *
     * @param apiUrl      请求URL
     * @param body        请求体
     * @param contentType 提交类型
     * @return
     */
    CloseableHttpResponse apply(URL apiUrl, Object body, ContentType contentType) throws IOException;
}
