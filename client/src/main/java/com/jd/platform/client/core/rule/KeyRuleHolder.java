package com.jd.platform.client.core.rule;

import com.google.common.eventbus.Subscribe;
import com.jd.platform.client.cache.CacheFactory;
import com.jd.platform.client.cache.LocalCache;
import com.jd.platform.common.rule.IKeyRule;
import com.jd.platform.common.rule.KeyRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 保存key的规则
 *
 * @author wuweifeng wrote on 2020-02-26
 * @version 1.0
 */
public class KeyRuleHolder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 保存超时时间和caffine的映射，key是超时时间，value是caffeine
     */
    private static final ConcurrentHashMap<Integer, LocalCache> RULE_CACHE_MAP = new ConcurrentHashMap<>();

    private static final List<KeyRule> KEY_RULES = new ArrayList<>();

    /**
     * 所有的规则，如果规则的超时时间变化了，会重建caffine
     */
    public static void putRules(List<IKeyRule> ruleList) {
        List<KeyRule> keyRules = ruleList.stream().map(IKeyRule::getKeyRule).collect(Collectors.toList());
        synchronized (KEY_RULES) {
            KEY_RULES.clear();
            KEY_RULES.addAll(keyRules);

            Set<Integer> durationSet = keyRules.stream().map(KeyRule::getDuration).collect(Collectors.toSet());
            for (Integer duration : RULE_CACHE_MAP.keySet()) {
                //先清除掉那些在RULE_CACHE_MAP里存的，但是rule里已没有的
                if (!durationSet.contains(duration)) {
                    RULE_CACHE_MAP.remove(duration);
                }
            }

            //遍历所有的规则
            for (KeyRule keyRule : keyRules) {
                int duration = keyRule.getDuration();
                if (RULE_CACHE_MAP.get(duration) == null) {
                    LocalCache cache = CacheFactory.build(duration);
                    RULE_CACHE_MAP.put(duration, cache);
                }
            }
        }
    }

    /**
     * 根据key返回对应的LocalCache。
     * 譬如Rules里有多个
     * ｛key1 , 500｝
     * ｛key2 , 600｝
     * ｛*    , 700｝
     * 如果命中了key1，就直接返回。如果key1、key2都没命中，再去判断rule里是否有 * ，如果有 * 代表通配
     */
    public static LocalCache findByKey(String key) {
        synchronized (KEY_RULES) {
            for (KeyRule keyRule : KEY_RULES) {
                //如果
                if (key.equals(keyRule.getKey()) || (keyRule.isPrefix() && key.startsWith(keyRule.getKey()))) {
                    return RULE_CACHE_MAP.get(keyRule.getDuration());
                }
            }

            for (KeyRule keyRule : KEY_RULES) {
                //如果
                if ("*".equals(keyRule.getKey())) {
                    return RULE_CACHE_MAP.get(keyRule.getDuration());
                }
            }

            return null;
        }

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
