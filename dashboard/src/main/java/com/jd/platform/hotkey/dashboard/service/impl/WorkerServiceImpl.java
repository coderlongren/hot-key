package com.jd.platform.hotkey.dashboard.service.impl;

import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.mapper.WorkerMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.KeyRule;
import com.jd.platform.hotkey.dashboard.model.Worker;
import com.jd.platform.hotkey.dashboard.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    public static void main(String[] args) {
        String a="/jd/workers/catr/WORKER";
        int index = a.lastIndexOf("/");
        String aa = a.substring(index+1);
        System.out.println(JSON.toJSONString(aa));
        System.out.println(JSON.toJSONString(index).hashCode());

    }

    @Override
    public PageInfo<Worker> pageWorker(PageParam page, SearchDto param) {
        List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.workersPath);
        List<Worker> workers = new ArrayList<>();

        List<KeyValue> rules = configCenter.getPrefix(ConfigConstant.rulePath);
        Set<String> apps = new HashSet<>();
        for (KeyValue kv : rules) {
            String key = kv.getKey().toStringUtf8();
            String app = key.replace(ConfigConstant.rulePath,"");
            apps.add(app);
        }

        for (KeyValue kv : keyValues) {
            String k = kv.getKey().toStringUtf8();
            String v = kv.getValue().toStringUtf8();
            String[] arr = v.split(Constant.SPIT);
            int cliCount = 0;
            if(v.contains(Constant.SPIT)){
                // 多个app的连接count聚合
                for (String app : apps) {
                    KeyValue countKv = configCenter.getKv(ConfigConstant.clientCountPath + app + "/" + arr[0]);
                    if(countKv != null){
                        cliCount = cliCount + Integer.parseInt(countKv.getValue().toStringUtf8());
                    }
                }
                workers.add(new Worker(k,v,cliCount));
            }
        }
        return new PageInfo<>(workers);
    }


    @Override
    public int insertWorkerByUser(Worker worker) {
        configCenter.put(worker.getName(),worker.getIp()+Constant.SPIT+worker.getPort());
        return this.insertWorkerBySys(worker);
    }

    @Override
    public int insertWorkerBySys(Worker worker) {
        worker.setUpdateTime(new Date());
        try {
            workerMapper.insertSelective(worker);
            String to = JSON.toJSONString(worker);
            return changeLogMapper.insertSelective(new ChangeLog(worker.getName(),Constant.WORKER_CHANGE,"",
                    to,worker.getUpdateUser(), SystemClock.now()+""));
        }catch (DuplicateKeyException e){

        }
        return 0;
    }


    @Override
    public Worker selectByPrimaryKey(int id) {
        return workerMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateWorkerByUser(Worker worker) {
        configCenter.put(worker.getName(),worker.getIp() + Constant.SPIT + worker.getPort());
        return this.updateWorker(worker);
    }

    @Override
    public int delWorkerByUser(Worker worker) {
        configCenter.delete(worker.getName());
        return this.updateWorker(worker);
    }

    @Override
    public int updateWorker(Worker worker) {
        try {
            Worker oldWorker = workerMapper.selectByKey(worker.getName());
            String from = JSON.toJSONString(oldWorker);
            String to = JSON.toJSONString(worker);
            changeLogMapper.insertSelective(new ChangeLog(worker.getName(),Constant.WORKER_CHANGE,from,to,worker.getUpdateUser(),SystemClock.now()+""));
            return workerMapper.updateByKey(worker);
        }catch (DuplicateKeyException e){

        }
        return 0;
    }

    @Override
    public Worker selectByKey(String key) {
        String val = configCenter.get(key);
        return new Worker(key,val,0);
    }
}
