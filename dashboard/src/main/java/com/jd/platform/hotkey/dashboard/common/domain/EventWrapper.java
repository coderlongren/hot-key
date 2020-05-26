package com.jd.platform.hotkey.dashboard.common.domain;

import com.ibm.etcd.api.Event;

import java.io.Serializable;
import java.util.Date;

/**
 * 包装类，用于保存事件最准确的时间
 */
public class EventWrapper implements Serializable {

    private String key;

    private String value;

    private Event.EventType eventType;

    private Date date;

    private Long ttl;

    private long version;

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Event.EventType getEventType() {
        return eventType;
    }

    public void setEventType(Event.EventType eventType) {
        this.eventType = eventType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public Date getDate() {
        return date;
    }

    public Long getTtl() {
        return ttl;
    }
}
