package com.jd.platform.hotkey.dashboard.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ibm.etcd.api.Event;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.req.ChartReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.PageReq;
import com.jd.platform.hotkey.dashboard.common.domain.req.SearchReq;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.common.eunm.ResultEnum;
import com.jd.platform.hotkey.dashboard.common.ex.BizException;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.mapper.ReceiveCountMapper;
import com.jd.platform.hotkey.dashboard.mapper.StatisticsMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.model.ReceiveCount;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import com.jd.platform.hotkey.dashboard.util.DateUtil;
import com.jd.platform.hotkey.dashboard.util.RuleUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
    @Resource
    private ReceiveCountMapper countMapper;
    @Resource
    private StatisticsMapper statisticsMapper;


    public HotKeyLineChartVo ruleLineChart2(SearchReq req) {
        int type = req.getType();
        if(req.getEndTime() == null){
            req.setEndTime(new Date());
        }
        switch (type){
            case 1:
                req.setStartTime(DateUtil.preMinus(30));
                List<Statistics> list = statisticsList();
                System.out.println("30 min");
                break;
            case 2:
                req.setStartTime(DateUtil.preDays(1));
                System.out.println("24 hours");
                break;
            case 3:
                req.setStartTime(DateUtil.preDays(7));
                System.out.println("7 days");
                break;
            default:
                System.out.println("=============");
        }
        return null;
    }


    @Override
    public HotKeyLineChartVo ruleLineChart(SearchReq req) {
        int type = req.getType();
        if(req.getEndTime() == null){
            req.setEndTime(new Date());
        }
        switch (type){
            case 1:
                req.setStartTime(DateUtil.preMinus(30));
                List<Statistics> list = statisticsList();
                Map<String, int[]> map = new HashMap<>(10);
                Map<String, List<Statistics>> listMap = list.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
                for (Map.Entry<String, List<Statistics>> m : listMap.entrySet()) {
                    int start = 1;
                    map.put(m.getKey(),new int[30]);
                    int[] data = map.get(m.getKey());
                    int tmp = 0;
                    for (int i = 0; i < 30; i++) {
                        Statistics st;
                        try {
                            st = m.getValue().get(tmp);
                            if(String.valueOf(start).endsWith("24")){ start = start + 77; }
                            if(start != st.getHours()){
                                data[i] = 0;
                            }else{
                                tmp ++;
                                data[i] = st.getCount();
                            }
                            start++;
                        }catch (Exception e){
                            data[i] = 0;
                        }
                    }
                }

                System.out.println("30 min");
                break;
            case 2:
                req.setStartTime(DateUtil.preDays(1));
                System.out.println("24 hours");
                break;
            case 3:
                req.setStartTime(DateUtil.preDays(7));
                System.out.println("7 days");
                break;
            default:
                System.out.println("=============");
        }
        return null;
    }


    @Override
    public PageInfo<KeyTimely> pageKeyTimely(PageReq page, SearchReq param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyTimely> listKey = keyTimelyMapper.listKeyTimely(param);
        for (KeyTimely timely : listKey) {
            timely.setKey(CommonUtil.keyName(timely.getKey()));
            timely.setRuleDesc(RuleUtil.ruleDesc(timely.getAppName() + "/" + timely.getKey()));
        }
        return new PageInfo<>(listKey);
    }

    @Override
    public PageInfo<Statistics> pageMaxHot(PageReq page, SearchReq req) {
        checkParam(req);
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Statistics> statistics = statisticsMapper.sumStatistics(req);
        return new PageInfo<>(statistics);
    }

    @Override
    public List<Statistics> listMaxHot(SearchReq req) {
        checkParam(req);
        return statisticsMapper.sumStatistics(req);
    }


    @Override
    public HotKeyLineChartVo getLineChart(ChartReq chartReq) {
        int hours = 6;
        // 默认查询6小时内的数据
        if(chartReq.getStartTime() == null || chartReq.getEndTime() == null){
            chartReq.setStartTime(DateUtil.preTime(hours));
            chartReq.setEndTime(new Date());
        }

        List<Statistics> statistics = statisticsMapper.listStatistics(chartReq);
        // 获取data Y轴
        Map<String, int[]> keyDateMap = keyDateMap(statistics, hours);
        // 获取时间x轴
        List<String> list = new ArrayList<>();
        for (int i = hours; i >0 ; i--) {
            LocalDateTime time = LocalDateTime.now().minusHours(i-1);
            int hour = time.getHour();
            list.add(hour+"时");
        }
        return new HotKeyLineChartVo(list,keyDateMap);
    }


    @Override
    public HotKeyLineChartVo getQpsLineChart(ChartReq chartReq) {
        if(chartReq.getStartTime() == null || chartReq.getEndTime() == null){
         /*   chartReq.setStartTime(DateUtil.preTime());
            chartReq.setEndTime(new Date());*/
        }
        List<ReceiveCount> countList = countMapper.list(chartReq);
        Map<String, int []> map = new HashMap<>(10);
        Set<String> minutes = new HashSet<>();
        List<List<ReceiveCount>> workerList = new ArrayList<>();
        countList.stream().collect(Collectors.groupingBy(ReceiveCount::getWorkerName,Collectors.toList()))
                .forEach((name,data)-> workerList.add(data));
        for (List<ReceiveCount> cts : workerList) {
            int size = cts.size();
            for (int i = 0; i < size; i++) {
                ReceiveCount dto = cts.get(i);
                String k = dto.getWorkerName();
                Long v = dto.getReceiveCount();
                Integer ms = dto.getMinutes();
                minutes.add(ms.toString());
                if(map.get(k) == null){
                    int [] data = new int[size];
                    data[i] = v.intValue();
                    map.put(k, data);
                }else{
                    int [] data = map.get(k);
                    data[i] = v.intValue();
                    map.put(k, data);
                }
            }
        }
        return new HotKeyLineChartVo(new ArrayList<>(minutes),map);
    }


    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageReq page, SearchReq param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKeyRecord(param);
        for (KeyRecord keyRecord : listKey) {
            keyRecord.setRuleDesc(RuleUtil.ruleDesc(keyRecord.getAppName() + "/" + keyRecord.getKey()));
        }

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




    private Map<String, int[]> keyDateMap(List<Statistics> statistics, int hours){
        Map<String, int[]> map = new HashMap<>(10);
        Map<String, List<Statistics>> listMap = statistics.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
        for (Map.Entry<String, List<Statistics>> m : listMap.entrySet()) {
            int start = DateUtil.preHours(LocalDateTime.now(),5);
            map.put(m.getKey(),new int[hours]);
            int[] data = map.get(m.getKey());
            int tmp = 0;
            for (int i = 0; i < hours; i++) {
                Statistics st;
                try {
                    st = m.getValue().get(tmp);
                    if(String.valueOf(start).endsWith("24")){ start = start + 77; }
                    if(start != st.getHours()){
                        data[i] = 0;
                    }else{
                        tmp ++;
                        data[i] = st.getCount();
                    }
                    start++;
                }catch (Exception e){
                    data[i] = 0;
                }
            }
        }
        return map;
    }



    private void checkParam(SearchReq req) {
        if(req.getStartTime() == null || req.getEndTime() == null){
            req.setStartTime(DateUtil.preTime(5));
            req.setEndTime(new Date());
        }
       /* long day = (req.getEndTime().getTime() - req.getStartTime().getTime()) / 86400000;
        if( day > Constant.MAX_DAY_RANGE){
            throw new BizException(ResultEnum.TIME_RANGE_LARGE);
        }*/
    }


    private List<Statistics> statisticsList(){
        Random rd = new Random();
        List<Statistics> list = new ArrayList<>();
        for (int i = 0; i < 30 ; i++) {
            Statistics st = new Statistics();
            st.setApp("rule1");
            st.setKeyName("key1");
            st.setCount(rd.nextInt(100));
            st.setBizType(1);
            st.setMinutes(2006052140+i);
            if(String.valueOf(st.getMinutes()).endsWith("60")){
                st.setMinutes(st.getMinutes()+1);
            }
            list.add(st);
        }
        List<Statistics> list2 = new ArrayList<>();
        for (int i = 0; i < 30 ; i++) {
            Statistics st2 = new Statistics();
            st2.setApp("rule2");
            st2.setKeyName("key2");
            st2.setCount(rd.nextInt(100));
            st2.setBizType(1);
            st2.setMinutes(2006052140+i);
            list2.add(st2);
        }
        list.addAll(list2);
        return list;
    }

    private List<Statistics> statisticsList1(){
        Random rd = new Random();
        List<Statistics> list = new ArrayList<>();
        for (int i = 0; i < 24 ; i++) {
            Statistics st = new Statistics();
            st.setApp("rule1");
            st.setKeyName("key1");
            st.setCount(rd.nextInt(100));
            st.setBizType(1);
            st.setHours(20060500+i);
        }
        return list;
    }


}


