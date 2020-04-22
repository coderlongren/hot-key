package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.*;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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


	@GetMapping("/realtimelist")
	@ResponseBody
	public KeyVo realTimeList(String searchText){
		try {
			System.out.println("searchText---->  "+searchText);
			return keyService.listKeyRecord(param(searchText));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@PostMapping("/list")
	@ResponseBody
	public Page<KeyRecord> list(PageParam page, String searchText){
		PageInfo<KeyRecord> info = keyService.pageKeyRecord(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


    @GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(KeyRecord rule){
		rule.setType(1);
		rule.setSource(loginUser().getUserName());
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
		modelMap.put("key", keyService.selectByPrimaryKey(id));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(KeyRecord record) {
		return Result.success(keyService.updateKeyRecord(record));
    }


}

