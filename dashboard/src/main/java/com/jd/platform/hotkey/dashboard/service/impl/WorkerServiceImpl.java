package com.jd.platform.hotkey.dashboard.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.WorkerMapper;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: WorkerService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 18:19
 */
@Service
public class WorkerServiceImpl implements WorkerService {

    @Autowired
    private WorkerMapper workerMapper;

    @Override
    public PageInfo<Worker> pageWorker(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<Worker> workers = workerMapper.listWorker(param);
        return new PageInfo<>(workers);
    }

    @Override
    public int insertWorker(Worker worker) {
        return workerMapper.insertSelective(worker);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return workerMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Worker selectByPrimaryKey(int id) {
        return workerMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateWorker(Worker worker) {
        return workerMapper.updateByPk(worker);
    }
}
