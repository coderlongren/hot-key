package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mapper
public interface KeyRecordMapper {

    int insertSelective(KeyRecord record);

    KeyRecord selectByPrimaryKey(Long id);

    List<KeyRecord> listKeyRecord(SearchDto param);

    int batchInsert(List<KeyRecord> list);
}