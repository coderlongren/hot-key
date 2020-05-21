package com.jd.platform.hotkey.dashboard.common.domain.req;

import com.jd.platform.hotkey.dashboard.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author liyunfeng31
 */
public class ChartReq implements Serializable {


    private Date st;

    private Date et;

    private String appName;

    private Integer limit;

    private Integer threshold;

    public ChartReq() {
    }

    public ChartReq(LocalDateTime st, LocalDateTime et) {
        this.st = DateUtil.localDateTimeToDate(st);
        this.et = DateUtil.localDateTimeToDate(et);
        this.limit = 5;
    }

    public Date getSt() {
        return st;
    }

    public void setSt(Date st) {
        this.st = st;
    }

    public Date getEt() {
        return et;
    }

    public void setEt(Date et) {
        this.et = et;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
