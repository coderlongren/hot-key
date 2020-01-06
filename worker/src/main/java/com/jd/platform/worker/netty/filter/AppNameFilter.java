package com.jd.platform.worker.netty.filter;

import com.jd.platform.worker.netty.client.IClientChangeListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 客户端上报自己的appName
 *
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
@Order(2)
public class AppNameFilter implements INettyMsgFilter {
    @Resource
    private IClientChangeListener clientEventListener;

    @Override
    public boolean chain(String message, ChannelHandlerContext ctx) {
        if (message.startsWith("appName")) {
            String appName = message.split("-")[1];
            if (clientEventListener != null) {
                clientEventListener.newClient(appName, ctx.channel().id().toString(), ctx);
            }
            return false;
        }

        return true;
    }


}
