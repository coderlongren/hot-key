package com.jd.platform.common.model.typeenum;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public enum MessageType {
    APP_NAME((byte) 1), REQUEST_NEW_KEY((byte) 2), PING((byte) 3), PONG((byte) 4), EMPTY((byte) 5),
    RESPONSE_NEW_KEY((byte) 11),;

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
        throw new RuntimeException("unsupported type: " + type);
    }
}