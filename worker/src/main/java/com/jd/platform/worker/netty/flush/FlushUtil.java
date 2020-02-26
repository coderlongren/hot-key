package com.jd.platform.worker.netty.flush;

import com.jd.platform.common.model.HotKeyMsg;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
public class FlushUtil {
    private static Logger logger = LoggerFactory.getLogger("flushUtil");

    /**
     * 往channel里输出消息
     */
    public static void flush(ChannelHandlerContext channelHandlerContext, HotKeyMsg msg) {

        if (channelHandlerContext.channel().isWritable()) {
            channelHandlerContext.channel().writeAndFlush(msg).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.warn("unexpected key. msg:{} fail:{}", msg, future.cause().getMessage());
                }
            });
        } else {
            try {
                //同步发送
                channelHandlerContext.channel().writeAndFlush(msg).sync();
            } catch (InterruptedException e) {
                logger.info("write and flush msg exception. msg:[{}]", msg, e);
            }
        }
    }
}
