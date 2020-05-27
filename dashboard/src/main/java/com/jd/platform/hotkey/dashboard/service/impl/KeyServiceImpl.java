package com.jd.platform.hotkey.dashboard.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ibm.etcd.api.Event;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.dto.KeyCountDto;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;


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
    public PageInfo<KeyTimely> pageKeyTimely(PageReq page, SearchReq param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyTimely> listKey = keyTimelyMapper.listKeyTimely(param);
        for (KeyTimely timely : listKey) {
            timely.setKey(CommonUtil.keyName(timely.getKey()));
        }
        return new PageInfo<>(listKey);
    }

    @Override
    public HotKeyLineChartVo getLineChart(ChartReq chartReq) {
        LocalDateTime now = LocalDateTime.now();
       // LocalDateTime now = LocalDateTime.now().minusDays(3).minusHours(3);
        Map<String, int[]> map = new HashMap<>(10);
        List<String> list = new ArrayList<>();
        for (int i = 5; i > 0 ; i--) {
            LocalDateTime pre = now.minusHours(i);
            List<KeyCountDto> records = recordMapper.maxHotKey(new ChartReq(pre, now, 10));
            int finalI = i;
            records.forEach(dto ->{
                String k = dto.getK();
                Integer v = dto.getCount();
                if(map.get(k) == null){
                   int [] data = new int[5];
                   data[finalI-1] = v;
                   map.put(k, data);
               }else{
                   int[] data = map.get(k);
                   data[finalI-1] = v;
                   map.put(k, data);
               }
            });
            list.add("近"+i+"小时");
        }
        return new HotKeyLineChartVo(list,map);
    }

    @Override
    public List<KeyCountDto> listExportKey(SearchReq req) {
        ChartReq chartReq = new ChartReq(req.getStartTime(), req.getEndTime(), req.getAppName(), req.getKey());
        System.out.println(JSON.toJSONString(chartReq));
        return recordMapper.maxHotKey(chartReq);
    }


    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageReq page, SearchReq param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKeyRecord(param);
        return new PageInfo<>(listKey);
    }


    @Override
    public int insertKeyByUser(KeyTimely key) {
        configCenter.putAndGrant(ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey(),
                System.currentTimeMillis() + "", key.getDuration());
        return 1;
    }

    @Override
    public int updateKeyByUser(KeyTimely key) {
        String ectdKey = ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey();
        configCenter.putAndGrant(ectdKey, "UPDATE", key.getDuration());
        return 1;
    }

    @Override
    public int delKeyByUser(KeyTimely keyTimely) {
        //app + "_" + key
        String[] arr = keyTimely.getKey().split("_");
        //删除client监听目录的key
        String ectdKey = ConfigConstant.hotKeyPath + arr[0] + "/" + arr[1];
        configCenter.delete(ectdKey);
        //也删除Record目录下的该key，因为不确定要删的key到底在哪
        String recordKey = ConfigConstant.hotKeyRecordPath + arr[0] + "/" + arr[1];
        configCenter.delete(recordKey);

        KeyRecord keyRecord = new KeyRecord(arr[1], "", arr[0], 0L, Constant.HAND,
                Event.EventType.DELETE_VALUE, UUID.randomUUID().toString(), new Date());

        recordMapper.insertSelective(keyRecord);

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


