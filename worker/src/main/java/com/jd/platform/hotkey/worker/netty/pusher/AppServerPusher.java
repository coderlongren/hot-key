package com.jd.platform.hotkey.worker.netty.pusher;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.worker.netty.holder.ClientInfoHolder;
import com.jd.platform.hotkey.worker.model.AppInfo;
import com.jd.platform.hotkey.worker.netty.flush.FlushUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 推送到各客户端服务器
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
@Component
public class AppServerPusher implements IPusher {

    /**
     * 给客户端推key信息
     */
    @Override
    public void push(HotKeyModel model) {
        for (AppInfo appInfo : ClientInfoHolder.apps) {
            if (model.getAppName().equals(appInfo.getAppName())) {
                Map<String, ChannelHandlerContext> map = appInfo.getMap();
                for (ChannelHandlerContext channel : map.values()) {
                    FlushUtil.flush(channel, new HotKeyMsg(MessageType.RESPONSE_NEW_KEY, FastJsonUtils.convertObjectToJSON(model)));
                }

                return;
            }
        }

    }

    @Override
    public void remove(HotKeyModel model) {
        push(model);
    }
}
