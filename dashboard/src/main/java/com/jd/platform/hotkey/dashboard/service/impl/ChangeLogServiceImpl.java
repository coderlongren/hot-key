package com.jd.platform.hotkey.dashboard.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.ChangeLogMapper;
import com.jd.platform.hotkey.dashboard.model.ChangeLog;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.service.ChangeLogService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: ChangeLogServiceImpl
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 17:53
 */
@Service
public class ChangeLogServiceImpl implements ChangeLogService {

    @Autowired
    private ChangeLogMapper changeLogMapper;


    @Override
    public PageInfo<ChangeLog> pageChangeLog(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<ChangeLog> changeLogs = changeLogMapper.listChangeLog(param);
        return new PageInfo<>(changeLogs);
    }

    @Override
    public int insertChangeLog(ChangeLog log) {
        return changeLogMapper.insertSelective(log);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return changeLogMapper.deleteByPrimaryKey(id);
    }

    @Override
    public ChangeLog selectByPrimaryKey(int id) {
        return changeLogMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateChangeLog(ChangeLog log) {
        return changeLogMapper.updateByPk(log);
    }
}
