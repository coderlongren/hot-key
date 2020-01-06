package com.jd.platform.client.netty;

import com.jd.platform.client.netty.encoder.DelimiterBasedFrameEncoder;
import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.model.typeenum.KeyType;
import com.jd.platform.common.tool.FastJsonUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * netty连接器
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NettyClient {

    public void connect(String host, int port, ChannelFutureListener futureListener) throws InterruptedException {
        //单线程
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            NettyClientHandler nettyClientHandler = new NettyClientHandler();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf buf = Unpooled.copiedBuffer("$".getBytes());
                            ch.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(1024, buf))
                                    .addLast(new StringDecoder())
                                    .addLast(new DelimiterBasedFrameEncoder())
                                    //10秒没消息时，就发心跳包过去
                                    .addLast(new IdleStateHandler(0, 0, 10), nettyClientHandler)
                            ;
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect().sync();
            //确保已经连上，active了
            while (true) {
                Thread.sleep(10);
                if (nettyClientHandler.isActive()) {
                    futureListener.operationComplete(channelFuture);
                    break;
                }
            }
            //这一步就阻塞了
            channelFuture.channel().closeFuture().sync();
            //当server断开后才会走下面的
            System.out.println("server is down");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger i = new AtomicInteger();
        new NettyClient().connect("127.0.0.1", 11111, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println(channelFuture.isSuccess());
                while (true) {
                    Thread.sleep(2);
                    HotKeyModel hotKeyModel = new HotKeyModel();
                    hotKeyModel.setAppName("a");
                    hotKeyModel.setKeyType(KeyType.REDIS_KEY);
                    hotKeyModel.setKey("" + i);
                    channelFuture.channel().writeAndFlush(FastJsonUtils.convertObjectToJSON(hotKeyModel));
                }
            }
        });
    }

}
