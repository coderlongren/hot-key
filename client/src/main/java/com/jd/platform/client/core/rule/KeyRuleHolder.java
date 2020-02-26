package com.jd.platform.client.core.rule;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.eventbus.Subscribe;
import com.jd.platform.common.rule.IKeyRule;
import com.jd.platform.common.rule.KeyRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wuweifeng wrote on 2020-02-26
 * @version 1.0
 */
public class KeyRuleHolder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ConcurrentHashMap<KeyRule, Cache<String, Object>> RULE_MAP = new ConcurrentHashMap<>();


    /**
     * 所有的
     */
    public static void putRules(List<IKeyRule> ruleList) {
        List<KeyRule> keyRules = ruleList.stream().map(IKeyRule::getKeyRule).collect(Collectors.toList());

    }


    @Subscribe
    public void ruleChange(KeyRuleInfoChangeEvent event) {
        logger.info("new rules info is :" + event.getKeyRules());
        List<IKeyRule> ruleList = event.getKeyRules();
        if (ruleList == null) {
            return;
        }

        putRules(ruleList);
    }
}
