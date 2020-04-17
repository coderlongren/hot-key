package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import com.jd.platform.hotkey.dashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/key")
public class KeyController extends BaseController {
	
	private String prefix = "admin/key";

	@Autowired
	private KeyService keyService;

	@GetMapping("/view")
    public String view()
    {	
        return prefix + "/list";
    }


	@PostMapping("/list")
	@ResponseBody
	public Page<KeyRecord> list(PageParam page, String searchText){
		PageInfo<KeyRecord> info = keyService.pageKeyRecord(page, SearchDto.param(searchText, request));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


    @GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(KeyRecord rule){
		int b=keyService.insertKeyRecord(rule);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(int id){
		int b=keyService.deleteByPrimaryKey(id);
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap modelMap){
		modelMap.put("user", keyService.selectByPrimaryKey(id));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(KeyRecord record) {
        return Result.success(keyService.updateKeyRecord(record));
    }


}

