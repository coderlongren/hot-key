package com.jd.platform.hotkey.client.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class JdLogger {

    public static void debug(Class className, String info) {
        getLogger(className).debug(info);
    }

    public static void info(Class className, String info) {
        getLogger(className).info(info);
    }

    public static void warn(Class className, String info) {
        getLogger(className).warn(info);
    }

    public static void error(Class className, String info) {
        getLogger(className).error(info);
    }

    private static Logger getLogger(String className) {
        return LoggerFactory.getLogger(className);
    }

    private static Logger getLogger(Class cla) {
        return LoggerFactory.getLogger(cla.getName());
    }
}
