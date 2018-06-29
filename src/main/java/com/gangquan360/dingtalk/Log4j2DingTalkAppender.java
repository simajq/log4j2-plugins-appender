package com.gangquan360.dingtalk;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * log4j2 发送钉钉的plugins
 * User: simajinqiang
 * Date: 2018/6/9
 * Time: 9:48
 */
@Plugin(name="DingTalk", category = "Core", elementType = "appender", printObject = true)
public class Log4j2DingTalkAppender extends AbstractAppender {

    /**
     * 不同项目,打印到不同的钉钉群中
     */
    private static String ACCESS_TOKEN = null;
    /**
     *间隔时间 默认一分钟
     */
    private static Long INTERVAL_TIME = 60 * 1000L;
    /**
     * 上次钉钉通知时间
     */
    private static Long LAST_DING_TALK = 0L;

    private ExecutorService executorService;


    protected Log4j2DingTalkAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions
            , String accessToken, Long intervalTime) {
        super(name, filter, layout, ignoreExceptions);
        ACCESS_TOKEN = accessToken;
        INTERVAL_TIME = intervalTime;
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Log4j2DingTalkAppender-%s").build();
        executorService = Executors.newSingleThreadExecutor(threadFactory);
    }

    @Override
    public void append(LogEvent logEvent) {
        final byte[] bytes = getLayout().toByteArray(logEvent);
        try {
            if(System.currentTimeMillis() - INTERVAL_TIME > LAST_DING_TALK){
                String projectPath = "[" + System.getProperty("user.dir") +"]"; //当前项目路径
                DingTalkHelper dingTalkHelper = new DingTalkHelper(ACCESS_TOKEN, projectPath + new String(bytes));
                executorService.submit(dingTalkHelper);
                LAST_DING_TALK = System.currentTimeMillis();
            }
        } catch (Exception e) {
            LOGGER.error("DingTalk send error!", e);
        }
    }

    /*  接收配置文件中的参数 */
    @PluginFactory
    public static Log4j2DingTalkAppender createAppender(@PluginAttribute("name") String name,
                                                @PluginAttribute("accessToken") String accessToken,
                                                @PluginAttribute("intervalTime") Long intervalTime,
                                                @PluginElement("Filter") final Filter filter,
                                                @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {
        if (name == null) {
            LOGGER.error("no name defined in conf.");
            return null;
        }
        if (accessToken == null){
            LOGGER.error("no accessToken defined in conf.");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new Log4j2DingTalkAppender(name, filter, layout, ignoreExceptions, accessToken, intervalTime);
    }


}
