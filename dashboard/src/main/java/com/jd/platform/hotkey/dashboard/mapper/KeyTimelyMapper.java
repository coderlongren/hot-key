package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyTimelyMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(KeyTimely record);

    KeyTimely selectByPrimaryKey(Long id);

    int updateByPk(KeyTimely record);

    List<KeyTimely> listKeyTimely(SearchDto param);

}