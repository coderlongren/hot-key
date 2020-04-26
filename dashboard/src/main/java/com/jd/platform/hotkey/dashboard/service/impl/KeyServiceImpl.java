package com.jd.platform.hotkey.dashboard.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.dashboard.common.domain.KeyVo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.model.KeyTimely;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import com.jd.platform.hotkey.dashboard.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    public List<KeyVo> listKeyTimely(SearchDto param) {
        // ALL_KEYS
        List<KeyTimely> keyTimely = keyTimelyMapper.listKeyTimely(param);
        if (CollectionUtil.isEmpty(keyTimely)) {
            return null;
        }
        KeyTimely kv = new KeyTimely();
        kv.setKey("/jd/hotkeys/");
        kv.setId(4L);
        keyTimely.add(kv);
      //  buildTree(kv, keyVos);
        return convert(keyTimely);
    }



    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKeyRecord(param);
        return new PageInfo<>(listKey);
    }

    @Transactional
    @Override
    public int insertKeyTimely(KeyTimely key) {
        recordMapper.insertSelective(buildRecord(key));
        return keyTimelyMapper.insertSelective(key);
    }


    @Override
    public int insertKeyByUser(KeyTimely key) {
        key.setVal(SystemClock.now() + "");
        key.setCreateTime(SystemClock.now());
        key.setKey(ConfigConstant.hotKeyPath + key.getAppName() + "/" + key.getKey());
        configCenter.putAndGrant(key.getKey(),key.getVal(),key.getDuration());
        return this.insertKeyTimely(key);
    }

    @Override
    public int updateKeyByUser(KeyTimely key) {
        configCenter.put(key.getKey(),key.getVal(),key.getDuration());
        return this.updateKeyTimely(key);
    }

    @Override
    public int delKeyByUser(KeyTimely key) {
        configCenter.delete(key.getKey());
        return keyTimelyMapper.deleteByKey(key.getKey());
    }

    @Override
    public KeyTimely selectByKey(String key) {
        return keyTimelyMapper.selectByKey(key);
    }

    @Override
    public int updateKeyTimely(KeyTimely key) {
        return keyTimelyMapper.updateByPk(key);
    }


    private List<KeyVo> convert(List<KeyTimely> keyValues) {
        List<KeyVo> records = new ArrayList<>();
        for (KeyTimely keyTimely : keyValues) {
            String key = keyTimely.getKey();
            KeyVo vo = new KeyVo();
            vo.setId(keyTimely.getId().intValue());
            vo.setDir(key.endsWith("/"));
            vo.setKey(key);
            vo.setValue(keyTimely.getVal());
            Long createTime = keyTimely.getCreateTime();
            Long ttl = keyTimely.getDuration();
            vo.setTtl(ttl);
            if(createTime!=null && ttl!=null){
                vo.setExpiration(new Date(createTime + ttl));
            }
            vo.setParentKey(keyTimely.getParentKey());
            vo.setParentId(getPid(keyValues,keyTimely.getParentKey()));
            records.add(vo);
        }
        return records;
    }


    private Integer getPid(List<KeyTimely> keyValues,String parentKey){

        for (KeyTimely kv : keyValues) {
            if (kv.getKey().equals(parentKey)) {
                return kv.getId().intValue();
            }
        }
        return null;
    }



    private static KeyVo buildTree(KeyVo pNode, List<KeyVo> recordList) {
        List<KeyVo> childMenus = new ArrayList<>();
        for (KeyVo vo : recordList) {
            if (vo.getParentKey().equals(pNode.getKey())) {
                vo.setParentId(pNode.getId());
                childMenus.add(buildTree(vo, recordList));
            }
        }
        pNode.setNodes(childMenus);
        return pNode;
    }


    private KeyRecord buildRecord(KeyTimely keyTimely) {
        KeyRecord record = new KeyRecord();
        BeanUtil.copyProperties(keyTimely,record);
        return record;
    }


    public static class Kv {
        private String pk;
        private String k;
        private String v;
        private Long lease;
        private boolean isDir;
        private List<Kv> nodes;

        public Kv(String k) {
            this.k = k;
        }

        public Kv(String k, String v) {
            this.k = k;
            this.v = v;
        }

        public Kv(String k, String v, Long lease) {
            this.k = k;
            this.v = v;
            this.lease = lease;
        }

        public String getK() {
            return k;
        }

        public void setK(String k) {
            this.k = k;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public Long getLease() {
            return lease;
        }

        public void setLease(Long lease) {
            this.lease = lease;
        }

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public boolean isDir() {
            return isDir;
        }

        public void setDir(boolean dir) {
            isDir = dir;
        }

        public List<Kv> getNodes() {
            return nodes;
        }

        public void setNodes(List<Kv> nodes) {
            this.nodes = nodes;
        }
    }

    public static List<Kv> getList() {
        List<Kv> kvList = new ArrayList<>();
        Kv kv1 = new Kv("/jd/");
        Kv kv2 = new Kv("/jd/hotkeys/");
        Kv kv3 = new Kv("/jd/hotkeys/0420-k111", "0420-v111");
        Kv kv4 = new Kv("/jd/hotkeys/app1/");
        Kv kv5 = new Kv("/jd/hotkeys/app1/hk1", "hk1val", 7587845841961209272L);
        Kv kv6 = new Kv("/jd/rules/");
        Kv kv7 = new Kv("/jd/rules/app1/");
        Kv kv8 = new Kv("/jd/rules/app1/rule111", "rule222", 7587845841961209320L);
        Kv kv9 = new Kv("/jd/rules/app1/rule222", "rule222val", 7587845841961209326L);
        kvList.add(kv1);
        kvList.add(kv2);
        kvList.add(kv3);
        kvList.add(kv4);
        kvList.add(kv5);
        kvList.add(kv6);
        kvList.add(kv7);
        kvList.add(kv8);
        kvList.add(kv9);
        for (Kv kv : kvList) {
            String k = kv.getK();
            kv.setDir(k.endsWith("/"));
            kv.setPk(CommonUtil.parentK(k));
        }
        System.out.println(JSON.toJSONString(kvList));
        return kvList;
    }



}


