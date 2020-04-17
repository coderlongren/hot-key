package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;

/**
 * @ProjectName: hotkey
 * @ClassName: ChangeLogService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 16:29
 */
public interface ChangeLogService {
    PageInfo<ChangeLog> pageChangeLog(PageParam page, SearchDto param);

    int insertChangeLog(ChangeLog log);

    int deleteByPrimaryKey(int id);

    ChangeLog selectByPrimaryKey(int id);

    int updateChangeLog(ChangeLog log);

}
