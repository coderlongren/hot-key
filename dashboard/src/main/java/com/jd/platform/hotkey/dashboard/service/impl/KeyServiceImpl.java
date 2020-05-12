package com.jd.platform.hotkey.dashboard.service.impl;


import cn.hutool.core.date.SystemClock;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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
    private IConfigCenter configCenter;
    @Resource
    private KeyRecordMapper recordMapper;
    @Resource
    private KeyTimelyMapper keyTimelyMapper;


    @Override
    public PageInfo<KeyTimely> pageKeyTimely(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyTimely> listKey = keyTimelyMapper.listKeyTimely(param);
        for (KeyTimely timely : listKey) {
            timely.setKey(CommonUtil.keyName(timely.getKey()));
        }
        return new PageInfo<>(listKey);
    }


    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKeyRecord(param);
        return new PageInfo<>(listKey);
    }


    @Override
    public int insertKeyByUser(KeyTimely key) {
        key.setVal("ADD");
        key.setCreateTime(new Date());
        key.setKey(ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey());
        configCenter.putAndGrant(key.getKey(), SystemClock.now() + "", key.getDuration());
        return keyTimelyMapper.insertSelective(key);
    }

    @Override
    public int updateKeyByUser(KeyTimely key) {
        String ectdKey = ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey();
        configCenter.putAndGrant(ectdKey, "UPDATE", key.getDuration());
        return 1;
    }

    @Override
    public int delKeyByUser(KeyTimely keyTimely) {
        String[] arr = keyTimely.getKey().split("_");
        String ectdKey = ConfigConstant.hotKeyPath + arr[0] + "/" + arr[1];
        configCenter.delete(ectdKey);
        return 1;
    }

    @Override
    public KeyTimely selectByKey(String key) {
        return keyTimelyMapper.selectByKey(key);
    }

    @Override
    public KeyTimely selectByPk(Long id) {
        return keyTimelyMapper.selectByPrimaryKey(id);
    }


}


