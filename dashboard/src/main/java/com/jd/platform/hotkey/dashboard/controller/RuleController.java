package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.model.Rules;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Controller
@RequestMapping("/rule")
public class RuleController extends BaseController {
	

	@Resource
	private RuleService ruleService;



	@GetMapping("/view2")
	public String view2(ModelMap modelMap){
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

	@PostMapping("/save")
	@ResponseBody
	public Result save(Rules rules){
		rules.setUpdateUser(userName());
		int b = ruleService.save(rules);
		return b == 0 ? Result.fail():Result.success();
	}


	@PostMapping("/remove")
	@ResponseBody
	public Result remove(String key){
		int b = ruleService.delRule(key, userName());
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/view")
	public String view(ModelMap modelMap){
		modelMap.put("title", Constant.RULE_CONFIG_VIEW);
		return "admin/rule/list";
	}

	@PostMapping("/list")
	@ResponseBody
	public Page<Rules> list2(PageReq page, String searchText){
		page.setPageSize(30);
		PageInfo<Rules> info = ruleService.pageKeyRule(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}

	@GetMapping("/edit/{app}")
	public String edit(ModelMap modelMap,@PathVariable("app") String app){
		modelMap.put("title", Constant.RULE_CONFIG_VIEW);
		modelMap.put("rules", ruleService.selectRules(app));
		return "admin/rule/view";
	}


	@GetMapping("/add")
	public String add(ModelMap modelMap){
		modelMap.put("title", Constant.RULE_CONFIG_VIEW);
		return "admin/rule/view";
	}


	@PostMapping("/listRules")
	@ResponseBody
	public List<String> rules(){
		List<String> info = ruleService.listRules(null);
		return info;
	}

}

