package com.jd.platform.hotkey.dashboard.common.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


public class BaseController {

    @Resource
    protected HttpServletRequest request;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    public String userName(){
        final String authHeader = JwtTokenUtil.getAuthHeader(request);
        final String token = authHeader.substring(2);
        return JwtTokenUtil.getUsername(token);
    }



    public SearchReq param(String text){
        String authHeader = JwtTokenUtil.getAuthHeader(request);
        SearchReq dto = JSON.parseObject(text, SearchReq.class);
        if(dto == null){ dto = new SearchReq(); }
        dto.setAppName(JwtTokenUtil.getAppName(authHeader.substring(2)));
        return dto;
    }

    public SearchReq param2(SearchReq dto){
        String authHeader = JwtTokenUtil.getAuthHeader(request);
        if(dto == null){ dto = new SearchReq(); }
        dto.setAppName(JwtTokenUtil.getAppName(authHeader.substring(2)));
        return dto;
    }

}
