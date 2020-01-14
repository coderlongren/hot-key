package com.jd.platform.client;

import com.jd.platform.client.core.eventbus.EventBusCenter;
import com.jd.platform.client.etcd.EtcdConfigFactory;
import com.jd.platform.client.etcd.EtcdStarter;
import com.jd.platform.client.netty.subscribe.WorkerChangeSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端启动器
 *
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class ClientStarter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String etcdServer;

    public ClientStarter(String appName) {
        if (appName == null) {
            throw new NullPointerException("appName cannot be null!");
        }
        Context.appName = appName;
    }

    public static void main(String[] args) throws InterruptedException {
        ClientStarter.Builder builder = new Builder();
        ClientStarter starter = builder.setAppName("a").setEtcdServer("https://127.0.0.1:2379").build();
        starter.startPipeline();

    }

    public static class Builder {
        private String appName;
        private String etcdServer;

        public Builder() {
        }

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setEtcdServer(String etcdServer) {
            this.etcdServer = etcdServer;
            return this;
        }

        public ClientStarter build() {
            ClientStarter clientStarter = new ClientStarter(appName);
            clientStarter.etcdServer = etcdServer;
            return clientStarter;
        }

    }

    /**
     * 启动监听etcd
     */
    private void startPipeline() {
        //设置etcd地址
        EtcdConfigFactory.buildConfigCenter(etcdServer);

        registEventBus();

        EtcdStarter starter = new EtcdStarter();
        //worker的地址集合
        starter.fetchWorkerInfo();

        //启动etcd监听
        starter.startWatch();
    }

    private void registEventBus() {
        //netty连接器会关注WorkerInfoChangeEvent事件
        EventBusCenter.register(new WorkerChangeSubscriber());
    }

    private void fetchWorkerAddress() {

    }

}
