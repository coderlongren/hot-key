package com.jd.platform.sample.controller;

import com.jd.platform.sample.Cache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuweifeng wrote on 2020-02-21
 * @version 1.0
 */
@RestController
@RequestMapping
public class TestController {

    @Resource
    private Cache cache;

    /**
     * 往redis里添加20个key
     */
    @RequestMapping("addKey")
    public Object add(Integer count) {
        if (count == null) {
            count = 20;
        }
        for (int i = 0; i < count; i++) {
            cache.set("key" + i, "我是一个用来做测试的value：" + i);
        }
        return "success";
    }

    /**
     * 从redis查询key
     */
    @RequestMapping("find")
    public Object findNormal(Integer count) {
        if (count == null) {
            count = 20;
        }
        List<String> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            values.add(cache.getN("key" + i));
        }
        return values;
    }

    /**
     * 使用热key查询，从redis查询key
     */
    @RequestMapping("findHot")
    public Object findWithHotKey(Integer count) {
        if (count == null) {
            count = 20;
        }
        List<String> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            values.add(cache.get("key" + i));
        }
        return values;
    }

}
