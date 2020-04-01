package com.jd.platform.hotkey.client.core.worker;

import com.google.common.eventbus.Subscribe;
import com.jd.platform.hotkey.client.Context;
import com.jd.platform.hotkey.client.netty.event.ChannelInactiveEvent;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * eventbus监听worker信息变动
 *
 * @author wuweifeng wrote on 2020-01-13
 * @version 1.0
 */
public class WorkerChangeSubscriber {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 监听worker信息变动
     */
    @Subscribe
    public void connectAll(WorkerInfoChangeEvent event) {
        logger.info("new infos is :" + event.getAddresses());
        List<String> addresses = event.getAddresses();
        if (addresses == null) {
            return;
        }

        WorkerInfoHolder.mergeAndConnectNew(addresses);
    }

    /**
     * 当client与worker的连接断开后，需要做如下处理
     * 等待10秒，根据etcd里是否还存在该worker的信息，如果还在，就进行重连（10秒后，如果是worker掉了，etcd会收到信息的）
     */
    @Subscribe
    public void channelInactive(ChannelInactiveEvent inactiveEvent) {
        //获取断线的channel
        Channel channel = inactiveEvent.getChannel();
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String address = socketAddress.getHostName() + ":" + socketAddress.getPort();
        logger.warn("this channel is inactive : " + socketAddress + " trying to NEED_RECONNECT 10 seconds later");

        CompletableFuture.runAsync(
                () -> {
                    try {
                        while (true) {
                            //如果不需要重连
                            if (!Context.NEED_RECONNECT) {
                                return;
                            }
                            Thread.sleep(10000);
                            boolean success = WorkerInfoHolder.dealChannelInactive(address);
                            if (success) {
                                return;
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }


}
