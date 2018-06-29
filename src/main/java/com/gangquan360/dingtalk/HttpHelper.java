package com.gangquan360.dingtalk;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * User: simajinqiang
 * Date: 2018/6/9
 * Time: 9:53
 * http 请求工具类
 */
public class HttpHelper {

    /**
     * 内容类型 - UTF-8
     */
    public static final long SECOND_LONG = 1000L;

    /**
     * HttpClient单例持有
     */
    private static class HttpClientHolder {
        private static final CloseableHttpClient INSTANCE = HttpClients.custom()
                .disableAutomaticRetries()
                .setMaxConnTotal(10240)
                .setMaxConnPerRoute(512)
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout((int) (10 * SECOND_LONG)).setSocketTimeout((int) (10 * SECOND_LONG)).build())// TODO 【要设置下httpclient的默认超时时间】
                .setUserAgent("")
                .build();
    }

    /**
     * @return HttpClient单例
     */
    public static CloseableHttpClient getHttpClient() {
        return HttpClientHolder.INSTANCE;
    }

    /**
     * 安静的关闭，即使抛出异常
     *
     * @param response 响应
     */
    public static void closeQuietly(CloseableHttpResponse response) {
        if (response == null) {
            return;
        }
        try {
            response.close();
        } catch (IOException ignored) {
        }
    }

}
