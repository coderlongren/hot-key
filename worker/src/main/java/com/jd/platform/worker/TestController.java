package com.jd.platform.worker;

import com.ibm.etcd.api.KeyValue;
import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.configcenter.IConfigCenter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @RequestMapping("workersPath")
    public Object workersPath() {
        List<KeyValue> list = iConfigCenter.getPrefix(ConfigConstant.workersPath);
        Map<String, Object> map = new HashMap<>();
        for (KeyValue keyValue : list) {
            map.put(keyValue.getKey().toStringUtf8(), keyValue.getValue().toStringUtf8());
        }
        return map;
    }

    @RequestMapping("rulePath")
    public Object rulePath() {
        List<KeyValue> list = iConfigCenter.getPrefix(ConfigConstant.rulePath);
        Map<String, Object> map = new HashMap<>();
        for (KeyValue keyValue : list) {
            map.put(keyValue.getKey().toStringUtf8(), keyValue.getValue().toStringUtf8());
        }
        return map;
    }

    @RequestMapping("hotKeyPath")
    public Object hotKeyPath() {
        List<KeyValue> list = iConfigCenter.getPrefix(ConfigConstant.hotKeyPath);
        Map<String, Object> map = new HashMap<>();
        for (KeyValue keyValue : list) {
            map.put(keyValue.getKey().toStringUtf8(), keyValue.getValue().toStringUtf8());
        }
        return map;
    }
}
