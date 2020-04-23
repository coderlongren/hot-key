package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ProjectName: hotkey
 * @ClassName: RuleService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 16:29
 */
public interface RuleService {
    PageInfo<KeyRule> pageKeyRule(PageParam page, SearchDto param);

    int insertRuleByUser(KeyRule rule);

    int insertRuleBySys(KeyRule rule);

    KeyRule selectByKey(String key);

    int updateRuleByUser(KeyRule rule);

    int delRuleByUser(KeyRule rule);

    int updateRule(KeyRule rule);
}
