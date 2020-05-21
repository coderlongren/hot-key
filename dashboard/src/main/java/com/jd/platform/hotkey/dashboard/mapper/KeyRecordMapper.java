package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.dto.KeyCountDto;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liyunfeng31
 */
@Mapper
public interface KeyRecordMapper {

    int insertSelective(KeyRecord record);

    KeyRecord selectByPrimaryKey(Long id);

    List<KeyRecord> listKeyRecord(SearchReq param);

    int batchInsert(List<KeyRecord> list);

    List<KeyCountDto> maxHotKey(ChartReq chartParam);
}