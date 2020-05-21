package com.jd.platform.hotkey.dashboard.common.domain.dto;

import java.io.Serializable;

/**
 * @author liyunfeng31
 */
public class KeyCountDto implements Serializable {

    private String k;

    private Integer count;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
