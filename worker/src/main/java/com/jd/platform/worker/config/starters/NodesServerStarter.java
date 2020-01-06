package com.jd.platform.worker.config.starters;

import com.jd.platform.worker.netty.client.IClientChangeListener;
import com.jd.platform.worker.netty.filter.INettyMsgFilter;
import com.jd.platform.worker.netty.server.NodesServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Resource
    private IClientChangeListener iClientChangeListener;
    @Resource
    private List<INettyMsgFilter> messageFilters;

    @Async
    public void start() throws Exception {
        NodesServer nodesServer = new NodesServer();
        nodesServer.setClientChangeListener(iClientChangeListener);
        nodesServer.setMessageFilters(messageFilters);
        nodesServer.startNettyServer(port);
    }
}
