package com.jd.platform.hotkey.dashboard.common.domain;

import com.ibm.etcd.api.Event;

import java.io.Serializable;
import java.util.Date;

/**
 * 包装类，用于保存事件最准确的时间
 */
public class EventWrapper implements Serializable {

    private Event event;

    private Date date;

    private Long ttl;

    public EventWrapper(Event event ,Long ttl) {
        this.event = event;
        this.ttl = ttl;
        this.date = new Date();
    }

    public Event getEvent() {
        return event;
    }

    public Date getDate() {
        return date;
    }

    public Long getTtl() {
        return ttl;
    }
}
