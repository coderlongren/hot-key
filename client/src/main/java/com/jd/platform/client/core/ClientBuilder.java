package com.jd.platform.client.core;

import com.jd.platform.client.core.eventbus.EventBusCenter;
import com.jd.platform.client.etcd.EtcdConfigFactory;
import com.jd.platform.client.etcd.EtcdStarter;
import com.jd.platform.client.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端启动器
 *
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class ClientBuilder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ClientBuilder(String appName) {
        if (appName == null) {
            throw new NullPointerException("appName cannot be null!");
        }
        Context.appName = appName;
    }

    public static void main(String[] args) throws InterruptedException {
        new ClientBuilder("").startPipeline("https://127.0.0.1:2379");
    }

    /**
     * 启动监听etcd
     */
    private void startPipeline(String etcdServer) throws InterruptedException {
        //设置etcd地址
        EtcdConfigFactory.buildConfigCenter(etcdServer);

        registEventBus();

        EtcdStarter starter = new EtcdStarter();
        //worker的地址集合
        starter.fetchWorkerInfo();

        //启动etcd监听
//        new Thread(starter::startWatch).start();
        starter.startWatch();
    }

    private void registEventBus() {
        //netty连接器会关注WorkerInfoChangeEvent事件
        EventBusCenter.register(new NettyClient());
    }

    private void fetchWorkerAddress() {

    }

}
