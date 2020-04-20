package com.jd.platform.hotkey.dashboard.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: KeyServiceImpl
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/17 17:53
 */
@Service
public class KeyServiceImpl implements KeyService {

    @Resource
    private KeyRecordMapper recordMapper;


    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKey(param);
        return new PageInfo<>(listKey);
    }

    @Override
    public int insertKeyRecord(KeyRecord record) {
        return recordMapper.insertSelective(record);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return recordMapper.deleteByPrimaryKey((long)id);
    }

    @Override
    public KeyRecord selectByPrimaryKey(int id) {
        return recordMapper.selectByPrimaryKey((long)id);
    }

    @Override
    public int updateKeyRecord(KeyRecord record) {
        return recordMapper.updateByPk(record);
    }
}
