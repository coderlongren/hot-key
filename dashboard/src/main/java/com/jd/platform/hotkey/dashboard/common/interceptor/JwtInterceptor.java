package com.jd.platform.hotkey.dashboard.common.interceptor;


import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.common.ex.BizException;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


public class JwtInterceptor extends HandlerInterceptorAdapter{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        //判断是否为ajax请求，默认不是
        boolean isAjaxRequest = false;
        if(!StringUtils.isEmpty(request.getHeader("x-requested-with"))
                && request.getHeader("x-requested-with").equals("XMLHttpRequest")){
            isAjaxRequest = true;
        }

        if(isAjaxRequest){
            // 获取请求头信息authorization信息
            final String authHeader = request.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
            System.out.println("authHeader-->  "+authHeader);
            System.out.println("isEmpty-->  "+StringUtils.isEmpty(authHeader) );

            if (StringUtils.isEmpty(authHeader)
                    || !authHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
              //  response.sendRedirect("login");
                throw new BizException(ResultEnum.NO_LOGIN);
             }
            // 获取token
            final String token = authHeader.substring(2);
            // 验证token是否有效--无效已做异常抛出，由全局异常处理后返回对应信息
            JwtTokenUtil.parseJWT(token);
        }
        return true;
    }

}
