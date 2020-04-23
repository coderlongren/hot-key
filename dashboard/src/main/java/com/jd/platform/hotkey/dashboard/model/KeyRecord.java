package com.jd.platform.hotkey.dashboard.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class KeyRecord implements Serializable {

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

    private Date createTime;

    public KeyRecord() {
    }

    public KeyRecord(String key,String val, String appName, Long duration, String source, Integer type, Date createTime) {
        this.key = key;
        this.val = val;
        this.appName = appName;
        this.duration = duration;
        this.source = source;
        this.type = type;
        this.createTime = createTime;
    }

  /*private boolean dir;

    private String value;

    private Long ttl;

    private Date expiration;

    private String parentKey;

    private List<KeyRecord> nodes;*/

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}