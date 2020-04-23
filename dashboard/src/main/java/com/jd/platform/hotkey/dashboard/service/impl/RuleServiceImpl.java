package com.jd.platform.hotkey.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyRuleMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: RuleServiceImpl
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 18:18
 */
@Service
public class RuleServiceImpl implements RuleService {


    @Resource
    private IConfigCenter configCenter;

    @Resource
    private KeyRuleMapper ruleMapper;

    @Resource
    private ChangeLogMapper changeLogMapper;

    @Override
    public PageInfo<KeyRule> pageKeyRule(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<KeyRule> rules = ruleMapper.listRule(param);
        return new PageInfo<>(rules);
    }

    @Override
    public int insertRuleByUser(KeyRule rule) {
        configCenter.put(rule.getKey(),JSON.toJSONString(rule));
        return this.insertRuleBySys(rule);
    }

    @Transactional
    @Override
    public int insertRuleBySys(KeyRule rule) {
        int ruleId = ruleMapper.insertSelective(rule);
        return changeLogMapper.insertSelective(new ChangeLog(ruleId,1,"",JSON.toJSONString(rule),rule.getUpdateUser(),rule.getAppName()));
    }

    @Override
    public KeyRule selectByKey(String key) {
        return ruleMapper.selectByKey(key);
    }


    @Override
    public int updateRuleByUser(KeyRule rule) {
        configCenter.put(rule.getKey(),JSON.toJSONString(rule));
        return this.updateRule(rule);
    }

    @Override
    public int delRuleByUser(KeyRule rule) {
        configCenter.delete(rule.getKey());
        return this.updateRule(rule);
    }

    @Transactional
    @Override
    public int updateRule(KeyRule rule) {
        KeyRule oldRule = ruleMapper.selectByKey(rule.getKey());
        String from = JSON.toJSONString(oldRule);
        String to = JSON.toJSONString(rule);
        changeLogMapper.insertSelective(new ChangeLog(rule.getId(),1,from,to,rule.getUpdateUser(),rule.getAppName()));
        return ruleMapper.updateByKey(rule);
    }
}
