package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.model.KeyRecord;

public interface KeyRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(KeyRecord record);

    int insertSelective(KeyRecord record);

    KeyRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(KeyRecord record);

    int updateByPrimaryKey(KeyRecord record);
}