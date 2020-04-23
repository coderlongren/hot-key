package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.*;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Controller
@RequestMapping("/key")
public class KeyController extends BaseController {
	
	private String prefix = "admin/key";

	@Resource
	private KeyService keyService;

	@GetMapping("/view")
    public String view()
    {	
        return prefix + "/list";
    }

	@PostMapping("/list")
	@ResponseBody
	public Page<KeyRecord> list(PageParam page, String searchText){
		PageInfo<KeyRecord> info = keyService.pageKeyRecord(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


	@GetMapping("/viewTimely")
	public String timelyview(){
		System.out.println("============viewTimely==========");
		return prefix + "/listtimely";
	}

	@PostMapping("/listTimely")
	@ResponseBody
	public List<KeyVo> realTimeList(String searchText){
		System.out.println("searchText-->  "+searchText);
		return keyService.listKeyTimely(param(searchText));
	}


	@GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(KeyTimely key){
		key.setType(1);
		key.setSource(loginUser().getUserName());
		int b = keyService.insertKeyByUser(key);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(String key){
		int b = keyService.delKeyByUser(new KeyTimely(key,userName()));
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{key}")
    public String edit(@PathVariable("key") String key, ModelMap modelMap){
		modelMap.put("key", keyService.selectByKey(key));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(KeyTimely keyTimely) {
		return Result.success(keyService.updateKeyByUser(keyTimely));
    }


}

