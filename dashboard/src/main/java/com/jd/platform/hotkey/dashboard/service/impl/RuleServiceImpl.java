package com.jd.platform.hotkey.dashboard.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: RuleServiceImpl
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 18:18
 */
@SuppressWarnings("ALL")
@Service
public class RuleServiceImpl implements RuleService {


    @Resource
    private IConfigCenter configCenter;


    @Override
    public PageInfo<KeyRule> pageKeyRule(PageReq page, SearchReq param) {
        String appName = param.getAppName();
        List<KeyValue> keyValues = configCenter.getPrefix(StringUtil.isEmpty(appName)?ConfigConstant.rulePath:ConfigConstant.rulePath+appName);
        List<KeyRule> rules = new ArrayList<>();
        for (KeyValue kv : keyValues) {
            String key = kv.getKey().toStringUtf8();
            String app = key.replace(ConfigConstant.rulePath,"");
            String v = kv.getValue().toStringUtf8();
            if(StringUtil.isEmpty(v)){ continue; }
            List<KeyRule> rule = JSON.parseArray(v,KeyRule.class);
            for (KeyRule keyRule : rule) {
                keyRule.setAppName(app);
            }
            rules.addAll(rule);
        }
        return new PageInfo<>(rules);
    }


    @Override
    public KeyRule selectByKey(String key) {
        String[] arr = key.split("_");
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.rulePath + arr[0]);
        for (KeyValue keyValue : keyValues) {
            String val = keyValue.getValue().toStringUtf8();
            if(StringUtil.isEmpty(val)){ continue; }
            List<KeyRule> rules = JSON.parseArray(val, KeyRule.class);
            for (KeyRule rule : rules) {
                if(rule.getKey().equals(arr[1])){
                    rule.setAppName(arr[0]);
                    return rule;
                }
            }
        }
        return new KeyRule();
    }


    /**
     * 手动更新记录 from - to
     * @param rule
     * @return
     */
    @Override
    public int updateRule(KeyRule rule) {
        if (StringUtil.isEmpty(rule.getAppName())) {
            return -1;
        }
        com.jd.platform.hotkey.common.rule.KeyRule temp = new com.jd.platform.hotkey.common.rule.KeyRule(rule.getKey(),
                rule.getPrefix(), rule.getInterval(), rule.getThreshold(), rule.getDuration());

        String ruleKey = rule.getKey();
        String app = rule.getAppName();
        String etcdKey = ConfigConstant.rulePath + app;
        List<KeyValue> keyValues = configCenter.getPrefix(etcdKey);
        KeyValue keyValue = keyValues.get(0);
        String val = keyValue.getValue().toStringUtf8();
        List<com.jd.platform.hotkey.common.rule.KeyRule> rules = JSON.parseArray(val, com.jd.platform.hotkey.common.rule.KeyRule.class);
        for (com.jd.platform.hotkey.common.rule.KeyRule keyRule : rules) {
            if(keyRule.getKey().equals(ruleKey)){
                keyRule = new com.jd.platform.hotkey.common.rule.KeyRule.Builder().interval(rule.getInterval())
                        .duration(rule.getDuration()).threshold(rule.getThreshold()).prefix(rule.getPrefix()).build();
            }
        }
        configCenter.put(etcdKey,JSON.toJSONString(rules));
        return 1;
    }

    @Override
    public int delRule(String appKey) {
        String [] arr = appKey.split("_");
        String etcdKey = ConfigConstant.rulePath + arr[0];
        List<KeyValue> keyValues = configCenter.getPrefix(etcdKey);
        KeyValue keyValue = keyValues.get(0);
        String val = keyValue.getValue().toStringUtf8();
        List<KeyRule> rules = JSON.parseArray(val, KeyRule.class);
        rules.removeIf(rule -> rule.getKey().equals(arr[1]));
        configCenter.put(etcdKey,JSON.toJSONString(rules));
        return 1;
    }


    @Override
    public int insertRule(KeyRule rule) {
        String app = rule.getAppName();
        if (StringUtil.isEmpty(app)) {
            return -1;
        }
        String etcdKey = ConfigConstant.rulePath + app;
        List<KeyValue> keyValues = configCenter.getPrefix(etcdKey);

        List<com.jd.platform.hotkey.common.rule.KeyRule> rules = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(keyValues)) {
            KeyValue keyValue = keyValues.get(0);
            String val = keyValue.getValue().toStringUtf8();
            rules = JSON.parseArray(val, com.jd.platform.hotkey.common.rule.KeyRule.class);
        }

        com.jd.platform.hotkey.common.rule.KeyRule keyRule = new com.jd.platform.hotkey.common.rule.KeyRule(rule.getKey(),
                rule.getPrefix(), rule.getInterval(), rule.getThreshold(), rule.getDuration());
        rules.add(keyRule);
        configCenter.put(etcdKey,JSON.toJSONString(rules));
        return 1;
    }

}
