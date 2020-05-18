package com.jd.platform.hotkey.worker.model;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class AppInfo {
    /**
     * 应用名
     */
    private String appName;
    /**
     * 客户端ip 和 channel的映射关系
     */
    private Map<String, ChannelHandlerContext> map = new ConcurrentHashMap<>();

    private Long id;

    /**
     * 应用负责人
     */
    private String principalName;

    /**
     * 负责人联系电话
     */
    private String principalPhone;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用创建/接入时间
     */
    private Date createTime;

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", map=" + map +
                '}';
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, ChannelHandlerContext> getMap() {
        return map;
    }

    public void setMap(Map<String, ChannelHandlerContext> map) {
        this.map = map;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalPhone() {
        return principalPhone;
    }

    public void setPrincipalPhone(String principalPhone) {
        this.principalPhone = principalPhone;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
