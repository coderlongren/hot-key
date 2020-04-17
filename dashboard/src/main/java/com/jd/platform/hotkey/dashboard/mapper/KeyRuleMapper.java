package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyRuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyRule record);

    int insertSelective(KeyRule record);

    KeyRule selectByPrimaryKey(Integer id);

    int updateByPk(KeyRule record);

    List<KeyRule> listRule(SearchDto param);
}