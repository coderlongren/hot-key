package com.jd.platform.hotkey.dashboard.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.service.RuleService;
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
	public String view(ModelMap modelMap){
		modelMap.put("title", Constant.RULE_CONFIG_VIEW);
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
		rule.setUpdateUser(userName());
		int b = ruleService.insertRule(rule);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(String key){
		int b = ruleService.delRule(key);
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{key}")
    public String edit(@PathVariable("key") String key, ModelMap modelMap){
		modelMap.put("rule", ruleService.selectByKey(key));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(KeyRule rule) {
		return Result.success(ruleService.updateRule(rule));
    }

}

