package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface KeyRuleMapper {

    @Options(useGeneratedKeys = true)
    int insertSelective(KeyRule record);

    KeyRule selectByKey(String key);

    int updateByKey(KeyRule record);

    List<KeyRule> listRule(SearchDto param);

    void batchInsert(List<KeyRule> rules);
}