package com.jd.platform.worker.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class ClientInfo {
    /**
     * 应用名
     */
    private String appName;
    /**
     * 连接的id
     */
    private String channelId;

    private ChannelHandlerContext channelHandlerContext;

    public ClientInfo() {
    }

    public ClientInfo(String appName, String channelId, ChannelHandlerContext channelHandlerContext) {
        this.appName = appName;
        this.channelId = channelId;
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "appName='" + appName + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelHandlerContext=" + channelHandlerContext +
                '}';
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
