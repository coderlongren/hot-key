package com.jd.platform.worker.netty.filter;

import com.jd.platform.worker.netty.flush.FlushUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.jd.platform.common.tool.Constant.PING;
import static com.jd.platform.common.tool.Constant.PONG;

/**
 * 心跳包处理
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
@Order(1)
public class HeartBeatFilter implements INettyMsgFilter {
    @Override
    public boolean chain(String message, ChannelHandlerContext ctx) {
        if (PING.equals(message)) {
            FlushUtil.flush(ctx, PONG);
            return false;
        }
        return true;

    }
}
