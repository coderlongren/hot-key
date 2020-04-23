package com.jd.platform.hotkey.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.WorkerMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private IConfigCenter configCenter;

    @Resource
    private WorkerMapper workerMapper;

    @Resource
    private ChangeLogMapper changeLogMapper;


    @Override
    public PageInfo<Worker> pageWorker(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<Worker> workers = workerMapper.listWorker(param);
        return new PageInfo<>(workers);
    }


    @Override
    public int insertWorkerByUser(Worker worker) {
        configCenter.put(worker.getName(),worker.getIp()+worker.getPort());
        return this.insertWorkerBySys(worker);
    }

    @Override
    public int insertWorkerBySys(Worker worker) {
        int workerId = workerMapper.insertSelective(worker);
        String to = JSON.toJSONString(worker);
        return changeLogMapper.insertSelective(new ChangeLog(workerId,2,"",to,worker.getUpdateUser()));
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return workerMapper.logicDeleteByKey(id,"");
    }

    @Override
    public Worker selectByPrimaryKey(int id) {
        return workerMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateWorkerByUser(Worker worker) {
        configCenter.put(worker.getName(),worker.getIp()+worker.getPort());
        return this.updateWorker(worker);
    }

    @Override
    public int delWorkerByUser(Worker worker) {
        configCenter.delete(worker.getName());
        return this.updateWorker(worker);
    }

    @Override
    public int updateWorker(Worker worker) {
        Worker oldWorker = workerMapper.selectByKey(worker.getName());
        String from = JSON.toJSONString(oldWorker);
        String to = JSON.toJSONString(worker);
        changeLogMapper.insertSelective(new ChangeLog(worker.getId(),2,from,to,worker.getUpdateUser()));
        return workerMapper.updateByKey(worker);
    }

    @Override
    public Worker selectByKey(String key) {
        return workerMapper.selectByKey(key);
    }
}
