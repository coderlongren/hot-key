package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.Worker;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkerMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Worker record);

    Worker selectByPrimaryKey(Integer id);

    int updateByPk(Worker record);

    List<Worker> listWorker(SearchDto param);
}