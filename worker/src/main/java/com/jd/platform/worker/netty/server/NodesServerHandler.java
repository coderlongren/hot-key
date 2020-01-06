package com.jd.platform.worker.netty.server;

import com.jd.platform.worker.netty.client.IClientChangeListener;
import com.jd.platform.worker.netty.filter.INettyMsgFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里处理所有netty事件。
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NodesServerHandler extends SimpleChannelInboundHandler<String> {
    /**
     * 客户端状态监听器
     */
    private IClientChangeListener clientEventListener;
    /**
     * 请自行维护Filter的添加顺序
     */
    private List<INettyMsgFilter> messageFilters = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        System.out.println(message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        ctx.writeAndFlush(message);
//        for (INettyMsgFilter messageFilter : messageFilters) {
//            boolean doNext = messageFilter.chain(message, ctx);
//            if (!doNext) {
//                return;
//            }
//        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (clientEventListener != null) {
            clientEventListener.loseClient(ctx.channel().id().toString());
        }
        ctx.close();
        super.channelInactive(ctx);
    }

    public void setClientEventListener(IClientChangeListener clientEventListener) {
        this.clientEventListener = clientEventListener;
    }

    public void addMessageFilter(INettyMsgFilter iNettyMsgFilter) {
        if (iNettyMsgFilter != null) {
            messageFilters.add(iNettyMsgFilter);
        }
    }

    public void addMessageFilters(List<INettyMsgFilter> iNettyMsgFilters) {
        if (!CollectionUtils.isEmpty(iNettyMsgFilters)) {
            messageFilters.addAll(iNettyMsgFilters);
        }
    }
}
