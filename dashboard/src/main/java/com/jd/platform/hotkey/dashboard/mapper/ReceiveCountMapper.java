package com.jd.platform.hotkey.dashboard.mapper;


import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.model.ReceiveCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author liyunfeng31
 */
@Mapper
public interface ReceiveCountMapper {

    /**
     * 插入最新记录
     * @param record  record
     * @return row
     */
    int insert(ReceiveCount record);

    /**
     * 查询统计
     * @param chartReq req
     * @return list
     */
    List<ReceiveCount> list(ChartReq chartReq);

}
