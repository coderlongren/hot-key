package com.jd.platform.worker.config.starters;

import com.jd.platform.worker.netty.client.IClientChangeListener;
import com.jd.platform.worker.netty.filter.INettyMsgFilter;
import com.jd.platform.worker.netty.server.NodesServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
public class NodesServerStarter {
    @Value("${netty.port}")
    private int port;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IClientChangeListener iClientChangeListener;
    @Resource
    private List<INettyMsgFilter> messageFilters;

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws Exception {
        logger.info("netty server is starting");

        NodesServer nodesServer = new NodesServer();
        nodesServer.setClientChangeListener(iClientChangeListener);
        nodesServer.setMessageFilters(messageFilters);
        nodesServer.startNettyServer(port);
    }
}
