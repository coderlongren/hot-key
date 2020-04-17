package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.model.ChangeLog;

public interface ChangeLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChangeLog record);

    int insertSelective(ChangeLog record);

    ChangeLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChangeLog record);

    int updateByPrimaryKey(ChangeLog record);
}