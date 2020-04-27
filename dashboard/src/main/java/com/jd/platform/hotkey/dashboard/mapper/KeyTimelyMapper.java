package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyTimelyMapper {

    int clear();

    int deleteByKey(String key);

    int insertSelective(KeyTimely key);

    KeyTimely selectByPrimaryKey(Long id);

    KeyTimely selectByKey(String key);

    int updateByKey(KeyTimely key);

    List<KeyTimely> listKeyTimely(SearchDto param);

    void batchInsert(List<KeyTimely> list);
}