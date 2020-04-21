package com.jd.platform.hotkey.common.model.typeenum;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public enum MessageType {
    APP_NAME((byte) 1),
    REQUEST_NEW_KEY((byte) 2),
    RESPONSE_NEW_KEY((byte) 3),
    PING((byte) 4), PONG((byte) 5),
    EMPTY((byte) 6);

    private byte type;

    MessageType(byte type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static MessageType get(byte type) {
        for (MessageType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }
}