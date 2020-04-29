package com.jd.platform.hotkey.dashboard.controller;

import com.alibaba.fastjson.JSON;
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
	public String view(ModelMap modelMap){
		modelMap.put("title","热点记录");
		return prefix + "/list";
	}


	@PostMapping("/list")
	@ResponseBody
	public Page<KeyRecord> list(PageParam page, SearchDto searchDto){
		System.out.println("searchText--> "+ JSON.toJSONString(searchDto));
		PageInfo<KeyRecord> info = keyService.pageKeyRecord(page, param2(searchDto));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}

	@GetMapping("/viewTimely")
	public String viewTimely(ModelMap modelMap){
		modelMap.put("title","实时热点");
		return prefix + "/listtimely";
	}


	@PostMapping("/listTimely")
	@ResponseBody
	public Page<KeyTimely> listTimely(PageParam page,SearchDto searchDto){
		System.out.println("searchText--> "+ JSON.toJSONString(searchDto));
		PageInfo<KeyTimely> info = keyService.pageKeyTimely(page, param2(searchDto));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


	@GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(KeyTimely key){
		key.setType(0);
		key.setSource(userName());
		int b = keyService.insertKeyByUser(key);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(String key){
		int b = keyService.delKeyByUser(new KeyTimely(key,userName()));
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap modelMap){
		modelMap.put("key", keyService.selectByPk(id));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(KeyTimely keyTimely) {
		return Result.success(keyService.updateKeyByUser(keyTimely));
    }


}

