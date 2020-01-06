package com.jd.platform.client.etcd;

import com.ibm.etcd.api.Event;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.configcenter.IConfigCenter;
import com.jd.platform.common.tool.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class EtcdStarter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private IConfigCenter configCenter;


    //Grant：分配一个租约。
    //Revoke：释放一个租约。
    //TimeToLive：获取剩余TTL时间。
    //Leases：列举所有etcd中的租约。
    //KeepAlive：自动定时的续约某个租约。
    //KeepAliveOnce：为某个租约续约一次。
    //Close：貌似是关闭当前客户端建立的所有租约。

    //启动后，上传自己的配置信息，如果check配置那里已经有了，就不要上传了。避免worker频繁监听
    //监听key删除，key新增。同一个path
    //监听rule变化，可能是etcd被控制台手工修改
    /**
     * 启动回调监听器
     */
    public void init() throws Exception {
        //上传自己的ip信息到配置中心
        uploadNodeInfo();

//        KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.rulePath);
        KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath + "a/");
        while (watchIterator.hasNext()) {
            WatchUpdate watchUpdate = watchIterator.next();
            List<Event> eventList = watchUpdate.getEvents();

            System.out.println(eventList.size());
            System.err.println(eventList.get(0).getKv());
            //包含put、delete
            Event.EventType eventType = eventList.get(0).getType();
        }

    }


    /**
     * 启动后，上传自己的信息到etcd
     */
    public void uploadNodeInfo() {
        try {
            String ip = IpUtils.getIp();
            String hostName = IpUtils.getHostName();
            //每4秒续约一次。将自己的ip注册到etcd的固定目录下
            configCenter.keepAlive(ConfigConstant.workersPath + hostName, ip, 4, 5);
        } catch (Exception e) {
            logger.error("keep alive with etcd server error");
            e.printStackTrace();
        }
    }

}