package com.jd.platform.worker.netty.holder;

import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.MessageType;
import com.jd.platform.common.tool.FastJsonUtils;
import com.jd.platform.worker.model.AppInfo;
import com.jd.platform.worker.netty.flush.FlushUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 保存所有与server连接的客户端信息
 * @author wuweifeng wrote on 2019-12-04
 * @version 1.0
 */
public class ClientInfoHolder {
    public static volatile List<AppInfo> apps = new ArrayList<>();


    /**
     * 给客户端推key信息
     */
    public static void pushToApp(String appName, HotKeyModel hotKeyModel) {
        System.out.println(apps);
        for(AppInfo appInfo : apps) {
            if (appName.equals(appInfo.getAppName())) {
                Map<String, ChannelHandlerContext> map = appInfo.getMap();
                for (ChannelHandlerContext channel : map.values()) {
                    System.out.println("flush");
                    FlushUtil.flush(channel, new HotKeyMsg(MessageType.RESPONSE_NEW_KEY, FastJsonUtils.convertObjectToJSON(hotKeyModel)));
                }

                return;
            }
        }
    }



}