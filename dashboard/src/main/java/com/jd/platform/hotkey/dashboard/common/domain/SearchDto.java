package com.jd.platform.hotkey.dashboard.common.domain;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;

/**
 * @ProjectName: hotkey
 * @ClassName: SearchParam
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/16 21:03
 */
public class SearchDto implements Serializable {

    private Date startTime;

    private Date endTime;

    private Integer status;

    private Integer bizId;

    private String appName;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBizId() {
        return bizId;
    }

    public void setBizId(Integer bizId) {
        this.bizId = bizId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static SearchDto param(String text, HttpServletRequest request){
       /* String token = request.getHeader("token");
        SearchDto dto = JSON.parseObject(text, SearchDto.class);
        dto.setAppName(JwtTokenUtil.getAppName(token));*/
        SearchDto dto = JSON.parseObject(text, SearchDto.class);
        if(dto == null){
            dto = new SearchDto();
        }
        dto.setAppName("test");
        return dto;
    }
}
