package com.jd.platform.client.netty;

import com.jd.platform.client.model.WorkerInfoHolder;
import com.jd.platform.client.netty.encoder.MessageDecoder;
import com.jd.platform.client.netty.encoder.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * netty连接器
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NettyClient {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final NettyClient nettyClient = new NettyClient();

    private Bootstrap bootstrap;

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
                                .addLast(new MessageEncoder())
                                .addLast(new MessageDecoder())
                                //10秒没消息时，就发心跳包过去
                                .addLast(new IdleStateHandler(0, 0, 10))
                                .addLast(nettyClientHandler);
                    }
                });
        return bootstrap;
    }

    public boolean connect(List<String> addresses) {
        boolean allSuccess = true;
        for (int i = 0; i < addresses.size(); i++) {
            String address = addresses.get(i);
            String[] ss = address.split(":");
            try {
                ChannelFuture channelFuture = bootstrap.connect(ss[0], Integer.parseInt(ss[1])).sync();
                Channel channel = channelFuture.channel();
                WorkerInfoHolder.put(address, channel);
            } catch (Exception e) {
                logger.error("----该worker连不上----" + address);
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

    public static void main(String[] args) throws Exception {
        //启动定时器，每隔0.5秒上传一次
//        new PushSchedulerStarter().startPusher();
//
//        AtomicInteger i = new AtomicInteger();
//        new NettyClient().connect("127.0.0.1", 11111, new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                System.out.println(channelFuture.isSuccess());
//
//                channelFuture.channel().writeAndFlush(new HotKeyMsg(MessageType.APP_NAME, Context.APP_NAME));
//
//                for (int j = 0; j < 1000; j++) {
//                    HotKeyPusher.push("" + i, KeyType.REDIS_KEY);
//                }
//            }
//        });
    }

}
