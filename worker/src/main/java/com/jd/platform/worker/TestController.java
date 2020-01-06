package com.jd.platform.worker;

import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.configcenter.IConfigCenter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2019-12-09
 * @version 1.0
 */
@RestController
public class TestController {
    @Resource
    private IConfigCenter iConfigCenter;

    @RequestMapping("test")
    public String aa(String key) {
       iConfigCenter.put( ConfigConstant.hotKeyPath + "a/" + key,  "1");
        iConfigCenter.put( ConfigConstant.hotKeyPath + "a/" + key + "1",  "1");
        return "1";
    }

    @RequestMapping("de")
    public String de(String key) {
        iConfigCenter.delete( ConfigConstant.hotKeyPath + "a/" + key);
        return "1";
    }
}
