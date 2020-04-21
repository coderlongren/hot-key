package com.jd.platform.hotkey.client.netty;

import com.jd.platform.hotkey.client.core.worker.WorkerInfoHolder;
import com.jd.platform.hotkey.client.log.JdLogger;
import com.jd.platform.hotkey.common.coder.Codec;
import com.jd.platform.hotkey.common.coder.NettyCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;

/**
 * netty连接器
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NettyClient {
    private static final NettyClient nettyClient = new NettyClient();

    private Bootstrap bootstrap;

    private Codec codec = new NettyCodec();


    public static NettyClient getInstance() {
        return nettyClient;
    }

    private NettyClient() {
        if (bootstrap == null) {
            bootstrap = initBootstrap();
        }
    }

    private Bootstrap initBootstrap() {
        //少线程
        EventLoopGroup group = new NioEventLoopGroup(2);

        Bootstrap bootstrap = new Bootstrap();
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(codec.newEncoder())
                                .addLast(codec.newDecoder())
                                //10秒没消息时，就发心跳包过去
                                .addLast(new IdleStateHandler(0, 0, 10))
                                .addLast(nettyClientHandler);
                    }
                });
        return bootstrap;
    }

    public boolean connect(List<String> addresses) {
        boolean allSuccess = true;
        for (String address : addresses) {
            String[] ss = address.split(":");
            try {
                ChannelFuture channelFuture = bootstrap.connect(ss[0], Integer.parseInt(ss[1])).sync();
                Channel channel = channelFuture.channel();
                WorkerInfoHolder.put(address, channel);
            } catch (Exception e) {
                JdLogger.error(getClass(), "----该worker连不上----" + address);
                WorkerInfoHolder.put(address, null);
                allSuccess = false;
            }
        }

        return allSuccess;

        //这一步就阻塞了
//            channelFuture.channel().closeFuture().sync();
        //当server断开后才会走下面的
//            System.out.println("server is down");
    }

}
