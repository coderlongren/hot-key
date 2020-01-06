package com.jd.platform.worker.netty.filter;

import io.netty.channel.ChannelHandlerContext;

/**
 * 对netty来的消息，进行过滤处理
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
public interface INettyMsgFilter {
    boolean chain(String message, ChannelHandlerContext ctx);
}
