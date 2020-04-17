package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(KeyRecord record);

    KeyRecord selectByPrimaryKey(Long id);

    int updateByPk(KeyRecord record);

    List<KeyRecord> listKey(SearchDto param);
}