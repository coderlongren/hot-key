package com.jd.platform.client.model;

import io.netty.channel.Channel;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class WorkerInfo {
    private String ip;
    private int port;

    private Channel channel;

    public WorkerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
