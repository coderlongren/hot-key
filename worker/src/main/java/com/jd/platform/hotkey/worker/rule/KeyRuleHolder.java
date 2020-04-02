package com.jd.platform.hotkey.worker.rule;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.rule.DefaultKeyRule;
import com.jd.platform.hotkey.common.rule.IKeyRule;
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

        IKeyRule prefix = null;
        IKeyRule common = null;

        //遍历该app的所有rule，找到与key匹配的rule。优先全匹配->prefix匹配-> * 通配
        for (IKeyRule keyRule : keyRules) {
            if (hotKeyModel.getKey().equals(keyRule.getKeyRule().getKey())) {
                return keyRule;
            }
            if (keyRule.getKeyRule().isPrefix() && hotKeyModel.getKey().startsWith(keyRule.getKeyRule().getKey())) {
                prefix = keyRule;
            }
            if ("*".equals(keyRule.getKeyRule().getKey())) {
                common = keyRule;
            }
        }
        if (prefix != null) {
            return prefix;
        }
        if (common != null) {
            return common;
        }

        return new DefaultKeyRule();
    }

    public static void put(String appName, List<IKeyRule> keyRules) {
        RULE_MAP.put(appName, keyRules);
    }

}
