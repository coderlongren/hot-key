package com.jd.platform.hotkey.dashboard.model;

import cn.hutool.core.date.SystemClock;

import java.io.Serializable;

public class KeyTimely implements Serializable {


    private Long id;

    private String key;

    private String appName;

    private String val;

    /**
     * 缓存时间
     */
    private Long duration;

    /**
     * 来源： SYSTEM 系统探测；USERNAME创建人
     */
    private String source;

    /**
     * 事件类型： 0 PUT； 1 删除
     */
    private Integer type;

    private Long createTime;

    private String parentKey;


    public KeyTimely() {
    }

    public KeyTimely(String key, String source) {
        this.key = key;
        this.source = source;
    }

    public KeyTimely(String key, Long duration) {
        this.key = key;
        this.duration = duration;
    }


    public KeyTimely(String key, String val, String appName, Long duration) {
        this.key = key;
        this.val = val;
        this.appName = appName;
        this.duration = duration;
        this.createTime = SystemClock.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }
}