package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.KeyVo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;

import java.util.List;
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

    int insertKeyTimely(KeyTimely keyTimely);

    int insertKeyByUser(KeyTimely keyTimely);

    int updateKeyByUser(KeyTimely keyTimely);

    int delKeyByUser(KeyTimely keyTimely);

    KeyTimely selectByKey(String key);

    int updateKeyTimely(KeyTimely keyTimely);

    PageInfo<KeyTimely> pageKeyTimely(PageParam page, SearchDto param);

}
