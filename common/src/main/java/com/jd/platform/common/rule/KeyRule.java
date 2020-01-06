package com.jd.platform.common.rule;

import java.util.List;

/**
 * 给key定义的规则。来决定是否热key
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
public class KeyRule {
    /**
     * 要忽略的keys
     */
    private List<String> ignoreKeys;

    private List<KeyRateRule> keyRateRules;

    @Override
    public String toString() {
        return "KeyRule{" +
                ", ignoreKeys=" + ignoreKeys +
                ", keyRateRules=" + keyRateRules +
                '}';
    }


    public List<String> getIgnoreKeys() {
        return ignoreKeys;
    }

    public void setIgnoreKeys(List<String> ignoreKeys) {
        this.ignoreKeys = ignoreKeys;
    }

    public List<KeyRateRule> getKeyRateRules() {
        return keyRateRules;
    }

    public void setKeyRateRules(List<KeyRateRule> keyRateRules) {
        this.keyRateRules = keyRateRules;
    }
}
