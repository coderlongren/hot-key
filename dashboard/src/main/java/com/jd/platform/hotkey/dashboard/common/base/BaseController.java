package com.jd.platform.hotkey.dashboard.common.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.common.ex.BizException;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import org.springframework.util.StringUtils;
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
        final String authHeader = request.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
        final String token = authHeader.substring(2);
        return JwtTokenUtil.getUsername(token);
    }


    public SearchDto param(String text){
        String authHeader = request.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
        SearchDto dto = JSON.parseObject(text, SearchDto.class);
        if(dto == null){ dto = new SearchDto(); }
        dto.setAppName(JwtTokenUtil.getAppName(authHeader.substring(2)));
        return dto;
    }
   
}
