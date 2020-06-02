package com.jd.platform.hotkey.dashboard.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.RulesMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.Rule;
import com.jd.platform.hotkey.dashboard.model.Rules;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private RulesMapper rulesMapper;

    @Resource
    private ChangeLogMapper logMapper;



    @Override
    public Rules selectRules(String app) {
        KeyValue kv = configCenter.getKv(ConfigConstant.rulePath + app);
        if(kv == null || kv.getValue() == null){
            return new Rules();
        }
        String k = kv.getKey().toStringUtf8();
        String v = kv.getValue().toStringUtf8();
        List<Rule> rule = JSON.parseArray(v,Rule.class);
        return new Rules(app,v);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateRule(Rules rules) {
        String app = rules.getApp();
        Rules oldRules = rulesMapper.select(app);
        String from = JSON.toJSONString(oldRules);
        configCenter.put(ConfigConstant.rulePath+app,rules.getRules());
        String to = JSON.toJSONString(rules);
        logMapper.insertSelective(new ChangeLog(app, 1, from, to,
                rules.getUpdateUser(), app, UUID.fastUUID().toString(true)));
        return rulesMapper.update(rules);
    }

    @Override
    public Integer add(Rules rules) {
        String app = rules.getApp();
        configCenter.put(ConfigConstant.rulePath+app,rules.getRules());
        String to = JSON.toJSONString(rules);
        logMapper.insertSelective(new ChangeLog(app, 1, "", to,
                rules.getUpdateUser(), app, UUID.fastUUID().toString(true)));
        return rulesMapper.insert(rules);
    }


    @Override
    public int delRule(String app, String updater) {
        Rules oldRules = rulesMapper.select(app);
        String from = JSON.toJSONString(oldRules);
        configCenter.delete(ConfigConstant.rulePath + app);
        logMapper.insertSelective(new ChangeLog(app, 1, from, "",updater, app, UUID.fastUUID().toString(true)));
        return rulesMapper.delete(app);
    }


}
