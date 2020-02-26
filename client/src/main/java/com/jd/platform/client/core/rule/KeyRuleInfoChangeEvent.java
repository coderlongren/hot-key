package com.jd.platform.client.core.rule;

import com.jd.platform.common.rule.IKeyRule;

import java.util.List;

/**
 * @author wuweifeng wrote on 2020-02-26
 * @version 1.0
 */
public class KeyRuleInfoChangeEvent {
    private List<IKeyRule> keyRules;

    public KeyRuleInfoChangeEvent(List<IKeyRule> keyRules) {
        this.keyRules = keyRules;
    }

    public List<IKeyRule> getKeyRules() {
        return keyRules;
    }

    public void setKeyRules(List<IKeyRule> keyRules) {
        this.keyRules = keyRules;
    }
}
