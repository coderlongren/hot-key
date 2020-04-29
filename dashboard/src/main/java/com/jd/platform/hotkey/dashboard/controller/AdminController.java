package com.jd.platform.hotkey.dashboard.controller;

import java.util.Collections;
import java.util.List;
import com.github.pagehelper.util.StringUtil;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.UserService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
	private static Logger log = LoggerFactory.getLogger(AdminController.class);

	@Resource
	private UserService userService;

	private String prefix = "admin";
	
	@GetMapping("/index")
	public String index(String text,ModelMap modelMap) {
		try {
			String info = CommonUtil.decoder(text);
			String[] arr = info.split("_");
			modelMap.put("name",arr[0]);
			modelMap.put("role",arr[1]);
		}catch (Exception e){
		}
		return prefix+"/index";
	}

	@GetMapping("/main")
	public String main() {
		return prefix+"/main";
	}

	@GetMapping("/login")
    public String login() {
        return "login";
    }


	@PostMapping("/login")
	@ResponseBody
	public Result login(User param,HttpServletResponse response) {
		User user = userService.findByNameAndPwd(param);
		if(user == null) return Result.error(ResultEnum.PWD_ERROR);
		String token = JwtTokenUtil.createJWT(user.getId(), user.getUserName(), user.getRole(), user.getAppName());
		Cookie cookie = new Cookie("token", JwtTokenUtil.TOKEN_PREFIX + token);
		cookie.setMaxAge(3600);
		cookie.setDomain("localhost");
		cookie.setPath("/");
		response.addCookie(cookie);
		return  Result.success(CommonUtil.encoder(user.getNickName()+"_"+user.getRole()));
	}


	@ResponseBody
	@PostMapping("/info")
	public User info(HttpServletRequest request){
		String authHeader = request.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
		User userPower = JwtTokenUtil.userPower(authHeader.substring(2));
		String appName = userPower.getAppName();
		String role = userPower.getRole();
		if(role.equals("ADMIN")){
		    List<String> apps =	userService.listApp();
			return new User(role,apps);
		}
		return new User(role, Collections.singletonList(appName));
	}


	@GetMapping("/Loginout")
	public String LoginOut(HttpServletRequest request, HttpServletResponse response){
        return "redirect:/"+prefix+"/login";
	}
	


	@GetMapping("Out404")
	public String Out404(){
        return "redirect:/error/404";
	}
	
	@GetMapping("Out403")
	public String Out403(){
        return "redirect:/error/403";
	}
	@GetMapping("Out500")
	public String Out500(){
        return "redirect:/error/500";
	}

	@GetMapping("Outqx")
	public String Outqx(){
        return "redirect:/error/500";
	}



}
