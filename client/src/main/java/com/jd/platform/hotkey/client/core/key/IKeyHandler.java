package com.jd.platform.hotkey.client.core.key;

/**
 * @author wuweifeng wrote on 2020-02-25
 * @version 1.0
 */
public interface IKeyHandler {

    IKeyPusher keyPusher();

    IKeyCollector keyCollector();
}
