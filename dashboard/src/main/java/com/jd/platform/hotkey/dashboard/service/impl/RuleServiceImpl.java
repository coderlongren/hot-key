package com.jd.platform.hotkey.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
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
import java.util.ArrayList;
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
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.rulePath);
        List<KeyRule> rules = new ArrayList<>();
        for (KeyValue kv : keyValues) {
            String key = kv.getKey().toStringUtf8();
            String app = key.replace(ConfigConstant.rulePath, "");
            String v = kv.getValue().toStringUtf8();
            List<KeyRule> rule = FastJsonUtils.toList(v, KeyRule.class);
            for (KeyRule keyRule : rule) {
                keyRule.setAppName(app);
            }
            rules.addAll(rule);
        }
        return new PageInfo<>(rules);
    }


    @Override
    public KeyRule selectByKey(String key) {
        key = key.replace("_","/");
        KeyValue keyValue = configCenter.getKv(ConfigConstant.rulePath + key);
        String val = keyValue.getValue().toStringUtf8();
        KeyRule rule = JSON.parseObject(val, KeyRule.class);
        rule.setVersion((int)keyValue.getModRevision());
        return rule;
    }


    /**
     * 系统的更新值记录to
     * @param rule
     * @return
     */
    @Override
    public int updateRule(KeyRule rule) {
        String to = JSON.toJSONString(rule);
        String key = rule.getKey();
        return changeLogMapper.insertSelective(new ChangeLog(key,1,"",
                to,"SYSTEM",rule.getAppName(),key+"_"+rule.getVersion()));
    }

    /**
     * 手动更新记录 from - to
     * @param rule
     * @return
     */
    @Override
    public int updateRuleByUser(KeyRule rule) {
        String from = rule.getOldRule();
        rule.setOldRule(null);
        String to = JSON.toJSONString(rule);
        String etcdKey = ConfigConstant.rulePath + rule.getAppName() + "/" + rule.getKey();
        String uuid = rule.getKey()+"_"+rule.getVersion();
        changeLogMapper.insertSelective(new ChangeLog(rule.getKey(),1,
                from,to,rule.getUpdateUser(),rule.getAppName(),uuid));
        return (int)configCenter.putAndGrant(etcdKey,JSON.toJSONString(rule),rule.getDuration());
    }


    @Override
    public int insertRuleByUser(KeyRule rule) {
        configCenter.put(rule.getKey(),JSON.toJSONString(rule));
        return this.insertRuleBySys(rule);
    }

    @Transactional
    @Override
    public int insertRuleBySys(KeyRule rule) {
        String uuid = rule.getKey()+"_"+rule.getVersion();
        return changeLogMapper.insertSelective(new ChangeLog(rule.getKey(),1,"",
                JSON.toJSONString(rule),rule.getUpdateUser(),rule.getAppName(),uuid));
    }


    @Override
    public int delRuleByUser(KeyRule rule) {
        configCenter.delete(rule.getKey());
        return this.updateRule(rule);
    }


}
