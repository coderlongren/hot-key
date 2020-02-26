package com.jd.platform.client;

import com.jd.platform.client.cache.CacheFactory;
import com.jd.platform.client.cache.LocalCache;
import com.jd.platform.client.callback.ReceiveNewKeySubscribe;
import com.jd.platform.client.core.eventbus.EventBusCenter;
import com.jd.platform.client.core.key.PushSchedulerStarter;
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

    private LocalCache localCache;
    /**
     * 推送key的间隔(毫秒)
     */
    private Long pushPeriod;

    public ClientStarter(String appName) {
        if (appName == null) {
            throw new NullPointerException("APP_NAME cannot be null!");
        }
        Context.APP_NAME = appName;
    }

    public static class Builder {
        private String appName;
        private String etcdServer;
        private LocalCache localCache;
        private Long pushPeriod;

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

        public Builder setLocalCache(LocalCache localCache) {
            this.localCache = localCache;
            return this;
        }

        public Builder setPushPeriod(Long pushPeriod) {
            this.pushPeriod = pushPeriod;
            return this;
        }

        public ClientStarter build() {
            ClientStarter clientStarter = new ClientStarter(appName);
            clientStarter.etcdServer = etcdServer;
            clientStarter.localCache = localCache;
            clientStarter.pushPeriod = pushPeriod;

            return clientStarter;
        }

    }

    /**
     * 启动监听etcd
     */
    public void startPipeline() {
        //设置etcd地址
        EtcdConfigFactory.buildConfigCenter(etcdServer);
        //设置本地缓存器
        CacheFactory.setCache(localCache);
        //开始定时推送
        PushSchedulerStarter.startPusher(pushPeriod);

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
        //热key探测回调关注热key事件
        EventBusCenter.register(new ReceiveNewKeySubscribe());
    }


}
