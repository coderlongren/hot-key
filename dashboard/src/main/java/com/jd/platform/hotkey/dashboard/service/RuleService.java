package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.model.KeyRule;

/**
 * @ProjectName: hotkey
 * @ClassName: RuleService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 16:29
 */
public interface RuleService {
    PageInfo<KeyRule> pageKeyRule(PageReq page, SearchReq param);

    int insertRule(KeyRule rule);

    KeyRule selectByKey(String key);

    int updateRule(KeyRule rule);

    int delRule(String key);

}
