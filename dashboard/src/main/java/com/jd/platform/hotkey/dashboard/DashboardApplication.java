package com.jd.platform.hotkey.dashboard;

import cn.hutool.core.date.SystemClock;
import com.github.pagehelper.util.StringUtil;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DashboardApplication implements CommandLineRunner {

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private KeyTimelyMapper timelyMapper;

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        int row = timelyMapper.clear();
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.hotKeyPath);
        List<KeyTimely> keyList = new ArrayList<>();
        for (KeyValue kv : keyValues) {
            KeyTimely timely = new KeyTimely();
            String key = kv.getKey().toStringUtf8();
            String val = kv.getValue().toStringUtf8();
            if(StringUtil.isEmpty(val)){ continue; }
            timely.setKey(key);
            timely.setType(0);
            timely.setVal(val);
            timely.setAppName(CommonUtil.appName(key));
            timely.setSource("SYSTEM");
            timely.setDuration(configCenter.timeToLive(kv.getLease()));
            timely.setCreateTime(SystemClock.now());
            keyList.add(timely);
        }
        if (keyList.size() == 0) {
            return;
        }
        timelyMapper.batchInsert(keyList);
    }
}
