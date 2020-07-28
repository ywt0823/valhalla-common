//package com.zichan360.bigdata.dataportalcommons.common.utils;
//
//import org.springframework.http.HttpStatus;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.time.Duration;
//import java.util.Optional;
//
///**
// * @author ywt
// * @Date 2019年2月9日 09:00:02
// **/
//public class HttpClientUtil {
//    private long timeout = 15;
//
//    public long getTimeout() {
//        return timeout;
//    }
//
//    public void setTimeout(long timeout) {
//        this.timeout = timeout;
//    }
//
//    /**
//     * @return java.lang.String
//     * @Author ywt
//     * @Description //TODO 同步发送post请求
//     * @Date 2019-03-11 17:02
//     * @Param [apiUrl, body, contentType]
//     **/
//    public String doPost(String apiUrl, String body, String contentType) {
//        return sendData(apiUrl, body, contentType, (url, content, type) -> {
//            HttpRequest httpRequest = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .timeout(Duration.ofSeconds(getTimeout()))
////                    .header("Content-Type", "application/x-www-form-urlencoded")
////                    .header("Content-Type", "application/json")
//                    .header("Content-Type", type)
//                    .POST(HttpRequest
//                            .BodyPublishers
//                            .ofString(content))
//                    .build();
//            return httpRequest;
//
//        });
//    }
//
//    /**
//     * @return java.lang.String
//     * @Author ywt
//     * @Description //TODO 同步 发送get请求
//     * @Date 2019-03-11 17:03
//     * @Param [apiUrl]
//     **/
//    public String doGet(String apiUrl) {
//        return sendData(apiUrl, "", "", (url, content, type) -> {
//            HttpRequest httpRequest = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .timeout(Duration.ofSeconds(getTimeout()))
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .GET()
//                    .build();
//            return httpRequest;
//        });
//    }
//
//    /**
//     * @return java.lang.String
//     * @Author ywt
//     * @Description //TODO 发送http请求
//     * @Date 2019-03-11 17:01
//     * @Param [apiUrl, body, contentType, IHttpRequest]
//     **/
//    private String sendData(String apiUrl, String body, String contentType, IHttpRequest<String, HttpRequest> IHttpRequest) {
//        HttpClient httpClient = HttpClient.newBuilder().build();
//        HttpRequest httpRequest = IHttpRequest.apply(apiUrl, body, contentType);
//        try {
//            HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            int statusCode = httpResponse.statusCode();
//            if (statusCode == HttpStatus.OK.value()) {
//                return Optional.ofNullable(httpResponse.body()).map(Object::toString).orElse(null);
//            } else {
//                return null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
