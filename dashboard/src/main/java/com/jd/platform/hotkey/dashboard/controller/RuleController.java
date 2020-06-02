package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.Rules;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Controller
@RequestMapping("/rule")
public class RuleController extends BaseController {
	

	@Resource
	private RuleService ruleService;



	@GetMapping("/view")
	public String view(ModelMap modelMap){
		modelMap.put("title", Constant.RULE_CONFIG_VIEW);
		return "admin/rule/view";
	}

	@GetMapping("/viewDetail")
	public String view3(ModelMap modelMap){
		modelMap.put("title", Constant.RULE_CONFIG_VIEW);
		return "admin/rule/jn";
	}

	@PostMapping("/getRule")
	@ResponseBody
	public Rules getRule(String app){
		return ruleService.selectRules(app);
	}


	@PostMapping("/add")
	@ResponseBody
	public Result add(Rules rule){
		rule.setUpdateUser(userName());
		int b = ruleService.add(rule);
		return b == 0 ? Result.fail():Result.success();
	}


	@PostMapping("/update")
	@ResponseBody
	public Result update(Rules rules){
		rules.setUpdateUser(userName());
		int b = ruleService.updateRule(rules);
		return b == 0 ? Result.fail():Result.success();
	}


	@PostMapping("/remove")
	@ResponseBody
	public Result remove(String app){
		int b = ruleService.delRule(app, userName());
		return b == 0 ? Result.fail():Result.success();
	}


}

