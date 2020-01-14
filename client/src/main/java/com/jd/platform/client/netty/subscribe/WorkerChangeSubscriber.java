package com.jd.platform.client.netty.subscribe;

import com.google.common.eventbus.Subscribe;
import com.jd.platform.client.etcd.WorkerInfoChangeEvent;
import com.jd.platform.client.model.WorkerInfoHolder;
import com.jd.platform.client.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
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
        logger.info("worker info is changed , new infos is :" + event.getAddresses());
        List<String> addresses = event.getAddresses();
        if (addresses == null) {
            return;
        }
        WorkerInfoHolder.removeNoneUsed(addresses);

        List<String> needConnectWorkers = WorkerInfoHolder.newWorkers(addresses);
        NettyClient.getInstance().connect(needConnectWorkers);
    }

}
