package com.jd.platform.worker.netty.server;

import com.jd.platform.worker.netty.client.IClientChangeListener;
import com.jd.platform.worker.netty.encoder.DelimiterBasedFrameEncoder;
import com.jd.platform.worker.netty.filter.INettyMsgFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * 该server用于给各个微服务实例连接用。
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NodesServer {
    private IClientChangeListener clientChangeListener;
    private List<INettyMsgFilter> messageFilters;

    public void startNettyServer(int port) throws Exception {
        //配置线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //出来网络io事件，如记录日志、对消息编解码等
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * handler类
     */
    private class ChildChannelHandler extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch)  {
            NodesServerHandler serverHandler = new NodesServerHandler();
            serverHandler.setClientEventListener(clientChangeListener);
            serverHandler.addMessageFilters(messageFilters);
            ByteBuf buf = Unpooled.copiedBuffer("$".getBytes());
            ch.pipeline()
                    .addLast(new DelimiterBasedFrameDecoder(1024, buf))
                    .addLast(new StringDecoder())
                    .addLast(new DelimiterBasedFrameEncoder())
                    .addLast(serverHandler);
        }
    }

    public void setClientChangeListener(IClientChangeListener clientChangeListener) {
        this.clientChangeListener = clientChangeListener;
    }

    public void setMessageFilters(List<INettyMsgFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }
}
