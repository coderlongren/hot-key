package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: RuleService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 16:29
 */
public interface RuleService {
    PageInfo<KeyRule> pageKeyRule(PageParam page, SearchDto param);

    int insertRule(KeyRule rule);

    KeyRule selectByKey(String key);

    int updateRule(KeyRule rule);

   // int delRule(KeyRule rule);

}
