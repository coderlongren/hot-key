package com.jd.platform.hotkey.dashboard.common.domain;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class KeyVo implements Serializable {

    private Integer id;

    private String key;

    private boolean dir;

    private String value;

    private Long ttl;

    private Date expiration;

    private String parentKey;

    private Integer parentId;

    private List<KeyVo> nodes;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<KeyVo> getNodes() {
        return nodes;
    }

    public void setNodes(List<KeyVo> nodes) {
        this.nodes = nodes;
    }
}