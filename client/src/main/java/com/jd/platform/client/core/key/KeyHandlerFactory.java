package com.jd.platform.client.core.key;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class KeyHandlerFactory {
    private static final IKeyHandler iKeyHandler = new DefaultKeyHandler();

    private static final IKeyPusher iKeyPusher = iKeyHandler.keyPusher();

    private static final IKeyCollector iKeyCollector = iKeyHandler.keyCollector();


    private KeyHandlerFactory() {
    }

    public synchronized static IKeyPusher getPusher() {
        return iKeyPusher;
    }

    public synchronized static IKeyCollector getCollector() {
        return iKeyCollector;
    }
}
