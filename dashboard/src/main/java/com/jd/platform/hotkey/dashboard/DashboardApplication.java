package com.jd.platform.hotkey.dashboard;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.mapper.KeyRuleMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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
    private KeyRuleMapper keyRuleMapper;


    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        int row = timelyMapper.clear();
        logger.info("clear db timely hotKey, effect row : {}", row);
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.hotKeyPath);
        List<KeyTimely> keyList = new ArrayList<>();
        Date date = new Date();
        for (KeyValue kv : keyValues) {
            String key = kv.getKey().toStringUtf8();
            String val = kv.getValue().toStringUtf8();
            if(StringUtil.isEmpty(val)){ continue; }
            long version = kv.getModRevision();
            String appKey = key.replace(ConfigConstant.hotKeyPath,"");
            String app = CommonUtil.appName(key);
            String uuid = appKey + Constant.JOIN + version;
            long ttl = configCenter.timeToLive(kv.getLease());
            String newKey = appKey.replace(app + "/", "");
            keyList.add(new KeyTimely(newKey, val, app, ttl, uuid, date));
         };
        if (keyList.size() == 0) {
            return;
        }
        try {
            timelyMapper.batchInsert(keyList);
        }catch (DuplicateKeyException e){

        }
    }


    /**
     * 每隔60秒同步一下rule到本地db
     */
    @Scheduled(fixedRate = 60000)
    public void syncRuleToDb() {
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.rulePath);
        logger.info("get rule from ETCD,  rules: {}", keyValues.size());
        Date date = new Date();
        for (KeyValue kv : keyValues) {
            String val = kv.getValue().toStringUtf8();
            if(StringUtil.isEmpty(val)) continue;
            String key = kv.getKey().toStringUtf8();
            List<KeyRule> rules = JSON.parseArray(val,KeyRule.class);
            for (KeyRule rule : rules) {
                rule.setAppName(CommonUtil.appName(key));
                rule.setUpdateTime(date);
                rule.setUpdateUser(Constant.SYSTEM);
                rule.setState(1);
            }
            keyRuleMapper.batchInsert(rules);
        }
    }

}
