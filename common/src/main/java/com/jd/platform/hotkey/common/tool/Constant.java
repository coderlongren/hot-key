package com.jd.platform.hotkey.common.tool;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public interface Constant {
    String PING = "ping";
    String PONG = "pong";

    int Default_Threads = Runtime.getRuntime().availableProcessors() * 2;

    int MAGIC_NUMBER = 0x12fcf76;
    String DELIMITER = "$(* *)$";
}
