package com.jd.platform.worker.model;

import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.rule.DefaultKeyRule;
import com.jd.platform.common.rule.IKeyRule;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存各个app的rule信息
 *
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
public class KeyRuleHolder {
    /**
     * key就是appName，value是rule
     */
    private static final Map<String, List<IKeyRule>> RULE_MAP = new ConcurrentHashMap<>();

    public static IKeyRule getRuleByAppAndKey(HotKeyModel hotKeyModel) {
        List<IKeyRule> keyRules = RULE_MAP.get(hotKeyModel.getAppName());
        //没有该key相关信息时，返回默认
        if (keyRules == null || CollectionUtils.isEmpty(keyRules)) {
            return new DefaultKeyRule();
        }
        for (IKeyRule keyRule : keyRules) {
            if (hotKeyModel.getKey().equals(keyRule.getKeyRule().getKey())) {
                return keyRule;
            }
            if (keyRule.getKeyRule().isPrefix() && hotKeyModel.getKey().startsWith(keyRule.getKeyRule().getKey())) {
                return keyRule;
            }
        }
        return new DefaultKeyRule();
    }

    public static void put(String appName, List<IKeyRule> keyRules) {
        RULE_MAP.put(appName, keyRules);
    }

}
