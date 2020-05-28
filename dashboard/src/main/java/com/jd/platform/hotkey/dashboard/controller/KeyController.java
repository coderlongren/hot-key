package com.jd.platform.hotkey.dashboard.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.jd.platform.hotkey.dashboard.common.base.BaseController;
import com.jd.platform.hotkey.dashboard.common.domain.*;
import com.jd.platform.hotkey.dashboard.common.domain.dto.KeyCountDto;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import com.jd.platform.hotkey.dashboard.util.ExcelUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



/**
 * @author liyunfeng31
 */
@Controller
@RequestMapping("/key")
public class KeyController extends BaseController {
	
	private String prefix = "admin/key";

	@Resource
	private KeyService keyService;


	@PostMapping("/lineChart")
	@ResponseBody
	public HotKeyLineChartVo lineChart(ChartReq chartReq){
		System.out.println("===========");
		return keyService.getLineChart(chartReq);
	}

	//@PostMapping("/qps")
	@GetMapping("/qps")
	@ResponseBody
	public HotKeyLineChartVo qpsLineChart(ChartReq ChartReq) {
		return keyService.getQpsLineChart(ChartReq);
	}



	@GetMapping("/view")
	public String view(ModelMap modelMap){
		modelMap.put("title", Constant.KEY_RECORD_VIEW);
		return prefix + "/list";
	}


	@PostMapping("/list")
	@ResponseBody
	public Page<KeyRecord> list(PageReq page, SearchReq searchReq){
		PageInfo<KeyRecord> info = keyService.pageKeyRecord(page, param2(searchReq));
		return new Page<>(info.getPageNum(),(int)info.getTotal(),info.getList());
	}

	@GetMapping("/viewTimely")
	public String viewTimely(ModelMap modelMap){
		modelMap.put("title","实时热点");
		return prefix + "/listtimely";
	}


	@PostMapping("/listTimely")
	@ResponseBody
	public Page<KeyTimely> listTimely(PageReq page, SearchReq searchReq){
		PageInfo<KeyTimely> info = keyService.pageKeyTimely(page, param2(searchReq));
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



	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public void export(HttpServletResponse response,
						  String startTime,String endTime,String appName,String key){
		SearchReq req = new SearchReq();
		if(StringUtil.isNotEmpty(startTime)){
			req.setStartTime(DateUtil.strToDate(startTime));
		}
		if(StringUtil.isNotEmpty(endTime)){
			req.setEndTime(DateUtil.strToDate(endTime));
		}
		req.setAppName(appName);
		req.setKey(key);
		List<Statistics> records = keyService.listExportKey(req);
		List<List<String> > rows = new ArrayList<>();
		for (Statistics record : records) {
			List<String> list = new ArrayList<>();
			list.add(record.getKeyName());
			list.add(record.getCount().toString());
			list.add(record.getApp());
			rows.add(list);
		}
		String[] s = {"热点key","次数","所属APP"};
		ExcelData data = new ExcelData("hotKey.xlsx", Arrays.asList(s),rows);
		ExcelUtil.exportExcel(response,data);
	}
}

