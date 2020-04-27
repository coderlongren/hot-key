package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.ChangeLogService;
import com.jd.platform.hotkey.dashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Controller
@RequestMapping("/changeLog")
public class ChangLogController extends BaseController {
	
	private String prefix = "admin/changeLog";

	@Resource
	private ChangeLogService logService;


	@GetMapping("/view")
	public String view(ModelMap modelMap){
		modelMap.put("title","变更记录");
		return prefix + "/list";
	}


	@PostMapping("/list")
	@ResponseBody
	public Page<ChangeLog> list(PageParam page, String searchText){
		PageInfo<ChangeLog> info = logService.pageChangeLog(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Integer id, ModelMap modelMap){
		modelMap.put("changeLog", logService.selectByPrimaryKey(id));
		return prefix + "/edit";
	}

}

