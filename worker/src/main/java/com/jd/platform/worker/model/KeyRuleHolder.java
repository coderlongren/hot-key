package com.jd.platform.worker.model;

import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.rule.KeyRateRule;
import com.jd.platform.common.rule.KeyRule;
import org.springframework.util.CollectionUtils;

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
    public static final Map<String, KeyRule> RULE_MAP = new ConcurrentHashMap<>();

    public static KeyRateRule getRuleByAppAndKey(HotKeyModel hotKeyModel) {
        KeyRule keyRule = RULE_MAP.get(hotKeyModel.getAppName());
        //没有该key相关信息时，返回默认
        if (keyRule == null || CollectionUtils.isEmpty(keyRule.getKeyRateRules())) {
            return new KeyRateRule(hotKeyModel.getKey());
        }
        for (KeyRateRule keyRateRule : keyRule.getKeyRateRules()) {
            if (hotKeyModel.getKey().equals(keyRateRule.getKey())) {
                return keyRateRule;
            }
            if (keyRateRule.isPrefix() && hotKeyModel.getKey().startsWith(keyRateRule.getKey())) {
                return keyRateRule;
            }
        }
        return new KeyRateRule(hotKeyModel.getKey());
    }

    public static void put(String appName, KeyRule keyRule) {
        RULE_MAP.put(appName, keyRule);
    }

}
