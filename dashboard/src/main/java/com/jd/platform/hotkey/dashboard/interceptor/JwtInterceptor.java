package com.jd.platform.hotkey.dashboard.interceptor;


import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.common.ex.BizException;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JwtInterceptor extends HandlerInterceptorAdapter{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
        String url = request.getRequestURI();
        if(isAjaxRequest){
            final String authHeader = request.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
            if (StringUtils.isEmpty(authHeader)
                    || !authHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
              //  response.sendRedirect("login");
                throw new BizException(ResultEnum.NO_LOGIN);
             //   response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              //  return false;
              //  throw new RuntimeException("NO_LOGIN");
             }
            final String token = authHeader.substring(2);
            Claims claims = JwtTokenUtil.parseJWT(token);
            String role = claims.get("role", String.class);
            if(role.equals("ADMIN") || role.equals("APPADMIN")){
                return true;
            }
            // appUser只读
            if(url.contains("view")||url.contains("list")){
                return true;
            }
            throw new BizException(ResultEnum.NO_PERMISSION);
           // throw new RuntimeException("NO_PERMISSION");
           // response.setStatus(HttpServletResponse.SC_FORBIDDEN);
           // return  false;
        }
        return true;
    }

}
