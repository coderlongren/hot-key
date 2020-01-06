package com.jd.platform.client.core;

import com.jd.platform.client.model.WorkerInfo;
import com.jd.platform.client.netty.NettyClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class ClientBuilder {

    public ClientBuilder(String appName) {
        if (appName == null) {
            throw new  NullPointerException("appName cannot be null!");
        }
        Context.appName = appName;
    }

    //add worker应该是个顺序事件，强制从etcd里连接所有，有任何一个worker自己连不上，就不要对外发key了
    /**
     * 启动，
     */
    public void start() throws InterruptedException {
        WorkerInfo workerInfo = new WorkerInfo("127.0.0.1", 11111);
        Context.workerInfoList.add(workerInfo);
        for (WorkerInfo info : Context.workerInfoList) {
            new NettyClient().connect(info.getIp(), info.getPort(), new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    info.setChannel(channelFuture.channel());
                }
            });
        }
//        CompletableFuture.supplyAsync()
    }

}
