package com.jd.platform.hotkey.dashboard;


import com.github.pagehelper.util.StringUtil;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.RulesMapper;
import com.jd.platform.hotkey.dashboard.model.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.List;


@EnableAsync
@EnableScheduling
@SpringBootApplication
public class DashboardApplication implements CommandLineRunner {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IConfigCenter configCenter;
    @Resource
    private KeyTimelyMapper timelyMapper;
    @Resource
    private RulesMapper rulesMapper;


    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        int row = timelyMapper.clear();
        logger.info("clear db timely hotKey, effect row : {}");
    }


    /**
     * 每隔60秒同步一下rule到本地db
     */
    @Scheduled(fixedRate = 60000)
    public void syncRuleToDb() {
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.rulePath);
        logger.info("get rule from ETCD,  rules: {}", keyValues.size());
        for (KeyValue kv : keyValues) {
            String val = kv.getValue().toStringUtf8();
            if(StringUtil.isEmpty(val)) continue;
            String key = kv.getKey().toStringUtf8();
            rulesMapper.update(new Rules(key, val));
        }
    }

}
