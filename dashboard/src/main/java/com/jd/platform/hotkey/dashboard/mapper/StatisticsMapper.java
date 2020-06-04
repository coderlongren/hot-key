package com.jd.platform.hotkey.dashboard.mapper;

import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liyunfeng31
 */
@Mapper
public interface StatisticsMapper {


    /**
     * 查看
     * @return list
     */
    List<Statistics> listStatistics(ChartReq chartReq);

    /**
     * records
     * @param records records
     * @return row
     */
    int batchInsert(List<Statistics> records);

    /**
     * 多个时间聚合统计列表
     * @param chartReq req
     * @return list
     */
    List<Statistics> sumStatistics(ChartReq chartReq);
}