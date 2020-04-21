package com.jd.platform.hotkey.dashboard.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class KeyRecord implements Serializable {

    private Long id;

    private String key;

    private String appName;

    private Integer count;

    /**
     * 缓存时间
     */
    private Integer duration;

    /**
     * 来源： SYSTEM 系统探测；USERNAME创建人
     */
    private String source;

    /**
     * 事件类型： 1：新增； 2删除
     */
    private Integer type;

    private Date createTime;

    private boolean dir;

    private String value;

    private Long ttl;

    private Date expiration;

    private String parentKey;

    private List<KeyRecord> nodes;

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
        this.key = key == null ? null : key.trim();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public List<KeyRecord> getNodes() {
        return nodes;
    }

    public void setNodes(List<KeyRecord> nodes) {
        this.nodes = nodes;
    }
}