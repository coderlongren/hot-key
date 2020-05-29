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

    public static String rule(String key) {
        if (StrUtil.isEmpty(key)) {
            return null;
        }
        String[] appKey = key.split("/");
        String appName = appKey[0];
        String realKey = appKey[1];
        return findByKey(appName, realKey).getKey();
    }

    private static KeyRule findByKey(String appName, String key) {
        synchronized (RULE_MAP) {
            KeyRule prefix = null;
            KeyRule common = null;
            //遍历该app的所有rule，找到与key匹配的rule。优先全匹配->prefix匹配-> * 通配
            //这一段虽然看起来比较奇怪，但是没毛病，不要乱改
            for (KeyRule keyRule : RULE_MAP.get(appName)) {
                if (key.equals(keyRule.getKey())) {
                    return keyRule;
                }
                if ((keyRule.isPrefix() && key.startsWith(keyRule.getKey()))) {
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
