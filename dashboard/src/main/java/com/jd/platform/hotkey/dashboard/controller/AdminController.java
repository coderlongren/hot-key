package com.jd.platform.hotkey.dashboard.controller;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.AjaxResult;
import com.jd.platform.hotkey.dashboard.model.BootstrapTree;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public String index(HttpServletRequest request) {
		return prefix+"/index";
	}

	@GetMapping("/main")
	public String main(ModelMap map) {
	//	setTitle(map, new TitleVo("首页", "首页", true,"欢迎进入", true, false));
		return prefix+"/main";
	}

	@GetMapping("/login")
    public String login(ModelMap modelMap) {
        try {
			System.out.println("==============login==============");
        	// 已经登陆
            if (1==1) {
				return "login";
			//	return "redirect:/"+prefix+"/index";
			//	return "admin/index";
            } else {
            	System.out.println("--进行登录验证..验证开始");
                return "login";
            }
        } catch (Exception e) {
        		e.printStackTrace();
        }
        return "login";
    }



	@PostMapping("/login")
	@ResponseBody
	public AjaxResult login(String userStr) {
		User user = userService.findByNameAndPwd(JSON.parseObject(userStr, User.class));
		return  AjaxResult.success();
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
