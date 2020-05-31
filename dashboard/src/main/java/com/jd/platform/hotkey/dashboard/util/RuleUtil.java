package com.jd.platform.hotkey.dashboard.util;

import cn.hutool.core.util.StrUtil;
import com.jd.platform.hotkey.common.rule.KeyRule;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-29
 */
public class RuleUtil {
    private static final ConcurrentHashMap<String, List<KeyRule>> RULE_MAP = new ConcurrentHashMap<>();

    public static void init() {
        synchronized (RULE_MAP) {
            RULE_MAP.clear();
        }
    }

    public static void put(String appName, List<KeyRule> list) {
        synchronized (RULE_MAP) {
            RULE_MAP.put(appName, list);
        }
    }

    /**
     * 根据APP的key，获取该key对应的rule
     */
    public static String rule(String key) {
        KeyRule keyRule = findByKey(key);
        if (keyRule != null) {
            String[] appKey = key.split("/");
            String appName = appKey[0];
            return appName + "-" + keyRule.getKey();
        }
        return null;
    }

    /**
     * 根据APP的key，获取该key对应的rule的desc
     */
    public static String ruleDesc(String key) {
        KeyRule keyRule = findByKey(key);
        if (keyRule != null) {
            return keyRule.getDesc();
        }
        return null;
    }

    private static KeyRule findByKey(String appNameKey) {
        synchronized (RULE_MAP) {
            if (StrUtil.isEmpty(appNameKey)) {
                return null;
            }
            String[] appKey = appNameKey.split("/");
            String appName = appKey[0];
            String realKey = appKey[1];
            KeyRule prefix = null;
            KeyRule common = null;
            //遍历该app的所有rule，找到与key匹配的rule。优先全匹配->prefix匹配-> * 通配
            //这一段虽然看起来比较奇怪，但是没毛病，不要乱改
            for (KeyRule keyRule : RULE_MAP.get(appName)) {
                if (realKey.equals(keyRule.getKey())) {
                    return keyRule;
                }
                if ((keyRule.isPrefix() && realKey.startsWith(keyRule.getKey()))) {
                    prefix = keyRule;
                }
                if ("*".equals(keyRule.getKey())) {
                    common = keyRule;
                }
            }

            if (prefix != null) {
                return prefix;
            }
            return common;
        }

    }
}
