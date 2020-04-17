package com.jd.platform.hotkey.dashboard.controller.admin;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	private String prefix = "admin/user";

	@Autowired
	private UserService userService;

	@GetMapping("/view")
    public String view(ModelMap model)
    {	
		String str="用户";
	//	setTitle(model, new TitleVo("列表", str+"管理", true,"欢迎进入"+str+"页面", true, false));
        return prefix + "/list";
    }


	@PostMapping("/list")
	@ResponseBody
	public Page list(PageParam page, String searchText){
		PageInfo<User> info = userService.pageUser(page, SearchDto.param(searchText, request));
		return new Page(info.getPageNum(),(int)info.getTotal(),info.getList());
	}

/*
    @GetMapping("/add")
    public String add(ModelMap modelMap)
    {
    	//添加角色列表
		List<TsysRole> tsysRoleList=sysRoleService.queryList();
		modelMap.put("tsysRoleList",tsysRoleList);
        return prefix + "/add";
    }*/

/*	@PostMapping("/add")
	@ResponseBody
	public AjaxResult add(TsysUser user,Model model,@RequestParam(value="roles", required = false)List<String> roles){
		int b=sysUserService.insertUserRoles(user,roles);
		if(b>0){
			return success();
		}else{
			return error();
		}
	}

	@PostMapping("/remove")
	@ResponseBody
	public AjaxResult remove(String ids){
		int b=sysUserService.deleteByPrimaryKey(ids);
		if(b>0){
			return success();
		}else{
			return error();
		}
	}


	@PostMapping("/checkLoginNameUnique")
	@ResponseBody
	public int checkLoginNameUnique(TsysUser tsysUser){
		int b=sysUserService.checkLoginNameUnique(tsysUser);
		if(b>0){
			return 1;
		}else{
			return 0;
		}
	}


	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
		//查询所有角色
		List<RoleVo> roleVos=sysUserService.getUserIsRole(id);
		mmap.put("roleVos",roleVos);
        mmap.put("TsysUser", sysUserService.selectByPrimaryKey(id));

        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TsysUser tsysUser,@RequestParam(value="roles", required = false)List<String> roles)
    {
        return toAjax(sysUserService.updateUserRoles(tsysUser,roles));
    }*/



	/*@GetMapping("/editPwd/{id}")
    public String editPwd(@PathVariable("id") String id, ModelMap mmap)
    {
        mmap.put("TsysUser", sysUserService.selectByPrimaryKey(id));
        return prefix + "/editPwd";
    }

    @PostMapping("/editPwd")
    @ResponseBody
    public AjaxResult editPwdSave(TsysUser tsysUser)
    {
        return toAjax(sysUserService.updateUserPassword(tsysUser));
    }

	*/
}

