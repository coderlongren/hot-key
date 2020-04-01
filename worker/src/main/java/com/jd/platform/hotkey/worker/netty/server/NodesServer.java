package com.jd.platform.hotkey.worker.netty.server;

import com.jd.platform.hotkey.common.coder.Codec;
import com.jd.platform.hotkey.worker.netty.client.IClientChangeListener;
import com.jd.platform.hotkey.worker.netty.filter.INettyMsgFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
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
    private Codec codec;

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
        } catch (Exception e) {
            //do nothing
            System.out.println("netty stop");
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
        protected void initChannel(Channel ch) {
            NodesServerHandler serverHandler = new NodesServerHandler();
            serverHandler.setClientEventListener(clientChangeListener);
            serverHandler.addMessageFilters(messageFilters);
            //maxFrameLength：最大消息长度，超过会抛出异常TooLongFrameException
            //lengthFieldOffset：长度域的起始偏移
            //lengthFieldLength：长度域占用字节数
            //lengthAdjustment：帧长度的调整字段，用来根据具体情况调整实际读取的帧字节数
            //initialBytesToStrip：如果业务中用不到消息长度，指定需要跳过的字节数可以直接截掉
            ch.pipeline()
                    .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4))
                    .addLast(new LengthFieldPrepender(4))
                    .addLast(codec.newEncoder())
                    .addLast(codec.newDecoder())
                    .addLast(serverHandler);
        }
    }

    public void setClientChangeListener(IClientChangeListener clientChangeListener) {
        this.clientChangeListener = clientChangeListener;
    }

    public void setMessageFilters(List<INettyMsgFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }
}
