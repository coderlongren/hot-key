package com.jd.platform.hotkey.dashboard.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.KeyRuleMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    private KeyRuleMapper ruleMapper;

    @Override
    public PageInfo<KeyRule> pageKeyRule(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<KeyRule> rules = ruleMapper.listRule(param);
        return new PageInfo<>(rules);
    }

    @Override
    public int insertRule(KeyRule rule) {
        return ruleMapper.insert(rule);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return ruleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public KeyRule selectByPrimaryKey(int id) {
        return ruleMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateRule(KeyRule rule) {
        return ruleMapper.updateByPk(rule);
    }
}
