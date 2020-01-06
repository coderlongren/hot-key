package com.jd.platform.client.core.push;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class HKConfigFactory {
    private static IPushHK pushHK;
    private static ICollectHK collectHK;

    public HKConfigFactory(IPushHK push, ICollectHK collect) {
        pushHK = push;
        collectHK = collect;
    }

    /**
     * 热key发送器，发到netty
     */
    public static IPushHK getPusher() {
        if (pushHK == null) {
            pushHK = new NettyPusher();
        }
        return pushHK;
    }

    /**
     * 热key搜集器，暂存key
     */
    public static ICollectHK getCollector() {
        if (collectHK == null) {
            collectHK = new TurnCollectHK();
        }
        return collectHK;
    }

}
