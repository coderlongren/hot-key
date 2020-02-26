package com.jd.platform.client.core.key;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class KeyHandlerFactory {
    private static final IKeyHandler iKeyHandler = new DefaultKeyHandler();

    private static IKeyPusher iKeyPusher = null;

    private static IKeyCollector iKeyCollector = null;

    private KeyHandlerFactory(){}

    public synchronized static IKeyPusher getPusher() {
        if (iKeyPusher == null) {
            iKeyPusher = iKeyHandler.keyPusher();
        }
        return iKeyPusher;
    }

    public static IKeyCollector getCollector() {
        if (iKeyCollector == null) {
            iKeyCollector = iKeyHandler.keyCollector();
        }
        return iKeyCollector;
    }
}
