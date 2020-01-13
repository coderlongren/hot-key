package com.jd.platform.client.netty;

import com.google.common.eventbus.Subscribe;
import com.jd.platform.client.Context;
import com.jd.platform.client.core.push.HotKeyPusher;
import com.jd.platform.client.core.push.PushSchedulerStarter;
import com.jd.platform.client.etcd.WorkerInfoChangeEvent;
import com.jd.platform.client.netty.encoder.MessageDecoder;
import com.jd.platform.client.netty.encoder.MessageEncoder;
import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.KeyType;
import com.jd.platform.common.model.typeenum.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.ArrayList;
import java.util.List;
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
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new MessageEncoder())
                                    .addLast(new MessageDecoder())
                                    //10秒没消息时，就发心跳包过去
                                    .addLast(new IdleStateHandler(0, 0, 10))
                                    .addLast(nettyClientHandler);
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

    @Subscribe
    public List<Channel> connectAll(WorkerInfoChangeEvent event) throws Exception {
        List<String> addresses = event.getAddresses();
        List<Channel> channels = new ArrayList<>(addresses.size());
        for(String address : addresses) {
            String[] ss = address.split(":");
            new NettyClient().connect(ss[0], Integer.parseInt(ss[1]), new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    channels.add(channelFuture.channel());
                }
            });
        }
        return channels;
    }

    public static void main(String[] args) throws Exception {
        //启动定时器，每隔0.5秒上传一次
        new PushSchedulerStarter().startPusher();

        AtomicInteger i = new AtomicInteger();
        new NettyClient().connect("127.0.0.1", 11111, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println(channelFuture.isSuccess());

                channelFuture.channel().writeAndFlush(new HotKeyMsg(MessageType.APP_NAME, Context.appName));

                for (int j = 0; j < 1000; j++) {
                    HotKeyPusher.push("" + i, KeyType.REDIS_KEY);
                }
            }
        });
    }

}
