package com.jd.platform.hotkey.dashboard.controller;

import java.util.List;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import com.jd.platform.hotkey.dashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Controller
@RequestMapping("/rule")
public class RuleController extends BaseController {
	
	private String prefix = "admin/rule";

	@Resource
	private RuleService ruleService;

	@GetMapping("/view")
    public String view()
    {	
        return prefix + "/list";
    }


	@PostMapping("/list")
	@ResponseBody
	public Page<KeyRule> list(PageParam page, String searchText){
		PageInfo<KeyRule> info = ruleService.pageKeyRule(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


    @GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(KeyRule rule){
		int b=ruleService.insertRule(rule);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(int id){
		int b=ruleService.deleteByPrimaryKey(id);
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap modelMap){
		modelMap.put("user", ruleService.selectByPrimaryKey(id));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(KeyRule rule) {
		return Result.success(ruleService.updateRule(rule));
    }

}

