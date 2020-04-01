package com.jd.platform.hotkey.worker.netty.client;

import com.jd.platform.hotkey.worker.model.AppInfo;
import com.jd.platform.hotkey.worker.netty.holder.ClientInfoHolder;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 对客户端的管理，新来、断线的管理
 *
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class ClientChangeListener implements IClientChangeListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 客户端新增
     */
    @Override
    public void newClient(String appName, String channelId, ChannelHandlerContext ctx) {
        logger.info("监听到事件");

        boolean appExist = false;
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            if (appName.equals(appInfo.getAppName())) {
                appExist = true;
                appInfo.getMap().put(channelId, ctx);
            }
        }
        if (!appExist) {
            //双重确认，避免创建重复的app
            synchronized (this) {
                boolean exist = false;
                AppInfo appInfo = null;
                for (AppInfo temp : ClientInfoHolder.apps) {
                    if (appName.equals(temp.getAppName())) {
                        exist = true;
                        appInfo = temp;
                    }
                }
                if (!exist) {
                    appInfo = new AppInfo();
                    appInfo.setAppName(appName);
                    ClientInfoHolder.apps.add(appInfo);
                }
                appInfo.getMap().put(channelId, ctx);
            }

        }

        logger.info("new client join. channel id is : " + channelId);
    }

    @Override
    public void loseClient(String channelId) {
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            Map<String, ChannelHandlerContext> map = appInfo.getMap();
            if (map.containsKey(channelId)) {
                map.remove(channelId);
                break;
            }
        }
        logger.info("client removed. channel id is : " + channelId);
    }
}
