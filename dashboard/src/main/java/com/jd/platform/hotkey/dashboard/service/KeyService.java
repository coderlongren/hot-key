package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.KeyVo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;

import java.util.concurrent.ExecutionException;

/**
 * @ProjectName: hotkey
 * @ClassName: KeyService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 16:28
 */
public interface KeyService {
    PageInfo<KeyRecord> pageKeyRecord(PageParam page, SearchDto param);

    int insertKeyRecord(KeyRecord record);

    int deleteByPrimaryKey(int id);

    KeyRecord selectByPrimaryKey(int id);

    int updateKeyRecord(KeyRecord record);

    KeyVo listKeyRecord(SearchDto param) ;
}
