package com.jd.platform.client.netty;

import com.jd.platform.client.Context;
import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.MessageType;
import com.jd.platform.common.tool.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<HotKeyMsg> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private volatile boolean active = false;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.ALL_IDLE){
                //向服务端发送消息
                ctx.writeAndFlush(new HotKeyMsg(MessageType.PING, Constant.PING)) ;
            }
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.err.println("channelActive");
        ctx.writeAndFlush(new HotKeyMsg(MessageType.APP_NAME, Context.appName));
        active = true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        active = false;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HotKeyMsg msg) throws Exception {
        System.err.println(msg);
        if (MessageType.PONG == msg.getMessageType()) {
            logger.info("heart beat");
            return;
        }
    }

    public boolean isActive() {
        return active;
    }
}
