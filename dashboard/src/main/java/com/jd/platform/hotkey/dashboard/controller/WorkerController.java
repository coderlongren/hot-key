package com.jd.platform.hotkey.dashboard.controller;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.Result;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.UserService;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Controller
@RequestMapping("/worker")
public class WorkerController extends BaseController {
	
	private String prefix = "admin/worker";

	@Resource
	private WorkerService workerService;

	@GetMapping("/view")
    public String view()
    {	
        return prefix + "/list";
    }


	@PostMapping("/list")
	@ResponseBody
		public Page<Worker> list(PageParam page, String searchText){
		PageInfo<Worker> info = workerService.pageWorker(page, param(searchText));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}


    @GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

	@PostMapping("/add")
	@ResponseBody
	public Result add(Worker worker){
		worker.setUpdateUser(userName());
		int b=workerService.insertWorkerByUser(worker);
		return b == 0 ? Result.fail():Result.success();
	}

	@PostMapping("/remove")
	@ResponseBody
	public Result remove(String key){
		int b=workerService.delWorkerByUser(new Worker(key,-1,userName()));
		return b == 0 ? Result.fail():Result.success();
	}


	@GetMapping("/edit/{key}")
    public String edit(@PathVariable("key") String key, ModelMap modelMap){
		modelMap.put("worker", workerService.selectByKey(key));
        return prefix + "/edit";
    }
	

    @PostMapping("/edit")
    @ResponseBody
    public Result editSave(Worker worker) {
		worker.setUpdateUser(userName());
		return Result.success(workerService.updateWorkerByUser(worker));
    }

}

