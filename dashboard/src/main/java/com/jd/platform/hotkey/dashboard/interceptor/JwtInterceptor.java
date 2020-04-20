package com.jd.platform.hotkey.dashboard.interceptor;


import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.common.ex.BizException;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
            if (StringUtils.isEmpty(authHeader)
                    || !authHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
              //  response.sendRedirect("login");
                throw new BizException(ResultEnum.NO_LOGIN);
             }
            // 获取token
            final String token = authHeader.substring(2);
            // 验证token是否有效--无效已做异常抛出，由全局异常处理后返回对应信息
            JwtTokenUtil.parseJWT(token);

            String role = JwtTokenUtil.getRole(token);
            String url = request.getRequestURI();
            if(role.equals("ADMIN") || role.equals("APPADMIN")){
                return true;
            }
            // appUser只读
            if(url.contains("view")||url.contains("list")){
                return true;
            }
            throw new BizException(ResultEnum.NO_PERMISSION);
        }
        return true;
    }

}
