package com.jd.platform.hotkey.worker.netty.pusher;

import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.UUID;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-26
 */
@Component
@Deprecated
public class BatchToEtcdScheduler {
    @Resource
    private KeyCollector keyCollector;
    @Resource
    private IConfigCenter iConfigCenter;

    /**
     * 每隔0.5秒上传一下已探测出的热key发往etcd供入库
     */
//    @Scheduled(fixedRate = 500)
    public void uploadClientCount() {
        Set<String> set = keyCollector.lockAndGetResult();
        if (set.size() == 0) {
            return;
        }

        //worker将热key推送到该地址，供dashboard监听入库做记录
        String hotKeyRecordPath = ConfigConstant.hotKeyRecordPath + UUID.randomUUID().toString();
        try {
            //推送到etcd，供dashboard监听入库
            iConfigCenter.putAndGrant(hotKeyRecordPath, FastJsonUtils.convertObjectToJSON(set), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
