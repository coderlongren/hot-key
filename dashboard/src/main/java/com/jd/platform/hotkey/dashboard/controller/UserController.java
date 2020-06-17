package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.UserService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import com.jd.platform.hotkey.dashboard.util.JwtTokenUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Resource
	private UserService userService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}


	@PostMapping("/login")
	@ResponseBody
	public Result login(User param, HttpServletResponse response) {
		User user = userService.findByNameAndPwd(param);
		if(user == null) return Result.error(ResultEnum.PWD_ERROR);
		String token = JwtTokenUtil.createJWT(user.getId(), user.getUserName(), user.getRole(), user.getAppName());
		Cookie cookie = new Cookie("token", JwtTokenUtil.TOKEN_PREFIX + token);
		cookie.setMaxAge(3600*24*7);
		cookie.setDomain("localhost");
		cookie.setPath("/");
		response.addCookie(cookie);
		Map<String, String> map = new HashMap<>(2);
		map.put("info",CommonUtil.encoder(user.getNickName()+"_"+user.getRole()));
		map.put("token",JwtTokenUtil.TOKEN_PREFIX + token);
		return  Result.success(map);
	}


	@GetMapping("/index")
	public String index(String text,ModelMap modelMap) {
		if(StringUtil.isNotEmpty(text)){
			String info = CommonUtil.decoder(text);
			String[] arr = info.split("_");
			modelMap.put("name",arr[0]);
			modelMap.put("role",arr[1]);
		}
		return "admin/index";
	}

	@GetMapping("/main")
	public String main() {
		return "admin/main";
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


	@GetMapping("/LoginOut")
	public String LoginOut(){
		return "redirect:/admin/user/login";
	}


	@GetMapping("/view")
    public String view(ModelMap modelMap){
		modelMap.put("title", Constant.USER_MANAGE_VIEW);
        return "admin/user/list";
    }


	@PostMapping("/list")
	@ResponseBody
	public Page<User> list(PageReq page, String searchText){
		PageInfo<User> info = userService.pageUser(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


    @GetMapping("/add")
    public String add(){
        return "admin/user/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(User user){
		int b=userService.insertUser(user);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(Integer key){
		int b = userService.deleteByPrimaryKey(key);
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap modelMap){
		modelMap.put("user", userService.selectByPrimaryKey(id));
        return "admin/user/edit";
    }


    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(User user) {
        return Result.success(userService.updateUser(user));
    }



	@GetMapping("/editPwd/{id}")
    public String editPwd(@PathVariable("id") Integer id, ModelMap modelMap){
		modelMap.put("user", userService.selectByPrimaryKey(id));
        return "admin/user/editPwd";
    }

    @PostMapping("/editPwd")
    @ResponseBody
    public Result editPwdSave(User user){
        return Result.success(userService.updateUser(user));
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

