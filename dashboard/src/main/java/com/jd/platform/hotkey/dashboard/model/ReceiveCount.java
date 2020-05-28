package com.jd.platform.hotkey.dashboard.model;

import com.jd.platform.hotkey.dashboard.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author liyunfeng31
 */
public class ReceiveCount implements Serializable {

    private Long id;

    private String workerName;

    private Long receiveCount;

    private String uuid;

    private Date updateTime;

    private Integer hour;

    private Long minutes;


    public ReceiveCount() {
    }

    public ReceiveCount(String workerName, Long receiveCount,String uuid) {
        this.workerName = workerName;
        this.receiveCount = receiveCount;
        this.uuid = uuid;
        this.updateTime = new Date();
        LocalDateTime now = LocalDateTime.now();
        this.hour = DateUtil.nowHour(now);
        this.minutes = DateUtil.nowMinus(now);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Long getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(Long receiveCount) {
        this.receiveCount = receiveCount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Long getMinutes() {
        return minutes;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }
}
