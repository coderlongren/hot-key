package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.model.KeyRule;

public interface KeyRuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyRule record);

    int insertSelective(KeyRule record);

    KeyRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(KeyRule record);

    int updateByPrimaryKey(KeyRule record);
}