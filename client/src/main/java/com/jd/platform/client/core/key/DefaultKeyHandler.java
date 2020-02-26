package com.jd.platform.client.core.key;

/**
 * @author wuweifeng wrote on 2020-02-25
 * @version 1.0
 */
public class DefaultKeyHandler implements IKeyHandler {
    @Override
    public IKeyPusher keyPusher() {
        return new NettyKeyPusher();
    }

    @Override
    public IKeyCollector keyCollector() {
        return new TurnKeyCollector();
    }
}
