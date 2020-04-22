package com.jd.platform.hotkey.dashboard.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.kv.KvClient;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.configcenter.etcd.JdEtcdClient;
import com.jd.platform.hotkey.dashboard.common.domain.KeyVo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.KeyRecordMapper;
import com.jd.platform.hotkey.dashboard.mapper.KeyTimelyMapper;
import com.jd.platform.hotkey.dashboard.model.KeyRecord;
import com.jd.platform.hotkey.dashboard.service.KeyService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;


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
    @Resource
    private KeyTimelyMapper keyTimelyMapper;
    @Resource
    private IConfigCenter iConfigCenter;

    public KeyVo listKeyRecord(SearchDto param) {
         // ALL_KEYS
        List<KeyValue> keyValues = iConfigCenter.getPrefix("");
        if (CollectionUtil.isEmpty(keyValues)) { return null; }
        List<KeyVo> keyVos = convert(keyValues, param.getAppName());
        KeyVo kv = new KeyVo();
        kv.setKey("/jd/");
        System.out.println("records---->   "+JSON.toJSONString(keyVos));
        buildTree(kv,keyVos);
        System.out.println("kv-->  "+JSON.toJSONString(kv));
        return kv;
    }



    private static KeyVo buildTree(KeyVo pNode,List<KeyVo> recordList){
        List<KeyVo> childMenus = new ArrayList<>();
        for(KeyVo record : recordList) {
            if(record.getParentKey().equals(pNode.getKey())) {
                System.out.println("node  key--> "+ record.getKey()+"    父级Key--> "+pNode.getKey());
                childMenus.add(buildTree(record,recordList));
            }
        }
        pNode.setNodes(childMenus);
        return pNode;
    }


    @Override
    public PageInfo<KeyRecord> pageKeyRecord(PageParam page, SearchDto param) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize());
        List<KeyRecord> listKey = recordMapper.listKeyRecord(param);
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


    private List<KeyVo> convert(List<KeyValue> keyValues,String appName){
        List<KeyVo> records = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (KeyValue keyValue : keyValues) {
            String key = keyValue.getKey().toStringUtf8();

            String rule = ConfigConstant.rulePath+appName;
            String worker = ConfigConstant.workersPath+appName;
            String hotKey = ConfigConstant.hotKeyPath+appName;
            if((key.contains(ConfigConstant.rulePath) && !key.startsWith(rule))
                    ||key.contains(ConfigConstant.workersPath) && !key.startsWith(worker)
                    ||key.contains(ConfigConstant.hotKeyPath) && !key.startsWith(hotKey)){
                continue;
            }

            KeyVo vo = new KeyVo();
            vo.setDir(key.endsWith("/"));
            vo.setKey(key);
            vo.setValue(keyValue.getValue().toStringUtf8());
            long ttl = (iConfigCenter).timeToLive(keyValue.getLease());
            Calendar expiration = ((Calendar) now.clone());
            expiration.add(Calendar.SECOND, (int)ttl);
            vo.setTtl(ttl);
            vo.setExpiration(expiration.getTime());
            vo.setParentKey(getParentK(key));
            records.add(vo);
        }
        return records;
    }


    public static class Kv{
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

    public static List<Kv> getList(){
        List<Kv> kvList = new ArrayList<>();
        Kv kv1 = new Kv("/jd/");
        Kv kv2 = new Kv("/jd/hotkeys/");
        Kv kv3 = new Kv("/jd/hotkeys/0420-k111","0420-v111");
        Kv kv4 = new Kv("/jd/hotkeys/app1/");
        Kv kv5 = new Kv("/jd/hotkeys/app1/hk1","hk1val",7587845841961209272L);
        Kv kv6 = new Kv("/jd/rules/");
        Kv kv7 = new Kv("/jd/rules/app1/");
        Kv kv8 = new Kv("/jd/rules/app1/rule111","rule222",7587845841961209320L);
        Kv kv9 = new Kv("/jd/rules/app1/rule222","rule222val",7587845841961209326L);
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
            kv.setPk(getParentK(k));
        }
        System.out.println(JSON.toJSONString(kvList));
        return kvList;
    }

    private static String getParentK(String key){
        if(key.endsWith("/")){  key = key.substring(0,key.length()-1); }
        int shortKey = key.lastIndexOf("/");
        return key.substring(0,shortKey+1);
    }
}

