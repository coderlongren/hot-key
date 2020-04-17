package com.jd.platform.hotkey.dashboard.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.UserService;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
	private static Logger log = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private UserService userService;

	private String prefix = "admin";
	
	@GetMapping("/index")
	public String index() {
		return prefix+"/index";
	}

	@GetMapping("/main")
	public String main() {
		return prefix+"/main";
	}

	@GetMapping("/login")
    public String login(ModelMap modelMap) {
        return "login";
    }



	@PostMapping("/login")
	@ResponseBody
	public Result login(User param,HttpServletResponse response) {
		User user = userService.findByNameAndPwd(param);
		if(user == null){
			return Result.error(ResultEnum.PWD_ERROR);
		}
		String token = JwtTokenUtil.createJWT(user.getId(), user.getUserName(), user.getRole(), user.getAppName());
		Cookie cookie = new Cookie("token", JwtTokenUtil.TOKEN_PREFIX + token);
		cookie.setMaxAge(3600);
		cookie.setDomain("localhost");
		cookie.setPath("/");
		response.addCookie(cookie);
		//response.setHeader(JwtTokenUtil.AUTH_HEADER_KEY, JwtTokenUtil.TOKEN_PREFIX + token);
		return  Result.success(token);
	}
	

	@GetMapping("/Loginout")
	public String LoginOut(HttpServletRequest request, HttpServletResponse response){
		/*//在这里执行退出系统前需要清空的数据
		Subject subject = SecurityUtils.getSubject();
		 //注销
        subject.logout();*/
        return "redirect:/"+prefix+"/login";
	}
	


	@GetMapping("Out404")
	public String Out404(HttpServletRequest request, HttpServletResponse response){
		
        return "redirect:/error/404";
	}
	
	@GetMapping("Out403")
	public String Out403(HttpServletRequest request, HttpServletResponse response){
		
        return "redirect:/error/403";
	}
	@GetMapping("Out500")
	public String Out500(HttpServletRequest request, HttpServletResponse response){
		
        return "redirect:/error/500";
	}

	@GetMapping("Outqx")
	public String Outqx(HttpServletRequest request, HttpServletResponse response){
        return "redirect:/error/500";
	}



}
