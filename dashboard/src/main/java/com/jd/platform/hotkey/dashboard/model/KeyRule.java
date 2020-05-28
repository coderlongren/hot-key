package com.jd.platform.hotkey.dashboard.model;

import java.util.Date;

public class KeyRule {
    private Integer id;

    /**
     * key的前缀，也可以完全和key相同。为"*"时代表通配符
     */
    private String key;
    /**
     * 是否是前缀，true是前缀
     */
    private Boolean prefix;
    /**
     * 间隔时间（秒）
     */
    private Integer interval;
    /**
     * 累计数量
     */
    private Integer threshold;
    /**
     * 变热key后，本地、etcd缓存它多久。单位（秒），默认60
     */
    private Integer duration;

    private String appName;

    private Integer state;

    private String updateUser;

    private Date updateTime;

    private Integer version;

    public KeyRule() {
    }

    public KeyRule(String key, Integer state,String updateUser) {
        this.key = key;
        this.state = state;
        this.updateUser = updateUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getPrefix() {
        return prefix;
    }

    public void setPrefix(Boolean prefix) {
        this.prefix = prefix;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}