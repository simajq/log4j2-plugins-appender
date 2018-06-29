package com.gangquan360.dingtalk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * 发送钉钉工具类
 * User: simajinqiang
 * Date: 2018/6/9
 * Time: 9:54
 */
public class DingTalkHelper extends Thread {

    private String accessToken;
    private String msg;

    public DingTalkHelper() {

    }

    public DingTalkHelper(String accessToken, String msg) {
        this.accessToken = accessToken;
        this.msg = msg;
    }

    /**
     * 发送钉钉消息
     *
     * @param accessToken accessToken
     * @param msg         消息内容
     */
    public static boolean sendDingTalkMsg(String accessToken, String msg) {
            CloseableHttpResponse response = null;
            try {
                HttpPost httpPost = new HttpPost("https://oapi.dingtalk.com/robot/send?access_token=" + accessToken);
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();//设置请求和传输超时时间
                httpPost.setConfig(requestConfig);
                // 接收参数json列表
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("msgtype", "text");
                JSONObject jsonParamContent = new JSONObject();
                jsonParamContent.put("content", msg);
                jsonParam.put("text", jsonParamContent);
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");//解决中文乱码问题
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                response = HttpHelper.getHttpClient().execute(httpPost);
                String responseText = EntityUtils.toString(response.getEntity());
                if (JSON.parseObject(responseText).get("errcode").equals(0)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HttpHelper.closeQuietly(response);
            }
            return false;
    }

    @Override
    public void run() {
        sendDingTalkMsg(accessToken, msg);
    }
}
