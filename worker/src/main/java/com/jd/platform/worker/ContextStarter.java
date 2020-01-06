package com.jd.platform.worker;

import com.jd.platform.worker.config.starters.EtcdStarter;
import com.jd.platform.worker.config.starters.NodesServerStarter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动时，也启动各种服务，如netty、etcd的监听器
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
@Component
public class ContextStarter {
    @Resource
    private EtcdStarter etcdStarter;
    @Resource
    private NodesServerStarter nodesServerStarter;


    @EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {
        etcdStarter.init();
        nodesServerStarter.start();
    }
}
