package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.Worker;

/**
 * @ProjectName: hotkey
 * @ClassName: WorkerService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 16:28
 */
public interface WorkerService {
    PageInfo<Worker> pageWorker(PageParam page, SearchDto param);

    int insertWorkerByUser(Worker worker);

    int insertWorkerBySys(Worker worker);

    int deleteByPrimaryKey(int id);

    Worker selectByPrimaryKey(int id);

    int updateWorkerByUser(Worker worker);

    int delWorkerByUser(Worker worker);

    int updateWorker(Worker worker);

    Worker selectByKey(String key);
}
