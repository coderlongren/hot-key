package com.jd.platform.worker.netty.pusher;

import com.jd.platform.common.configcenter.IConfigCenter;
import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.worker.model.KeyRuleHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.jd.platform.common.tool.EtcdKeyBuilder.keyPath;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
@Component
public class EtcdPusher implements IPusher {
    @Resource
    private IConfigCenter iConfigCenter;

    @Override
    public void push(HotKeyModel model) {
        //推送到etcd
        iConfigCenter.putAndGrant(keyPath(model), "1",
                KeyRuleHolder.getRuleByAppAndKey(model).getContinued());
    }

    @Override
    public void remove(HotKeyModel model) {
        //推送etcd删除
        iConfigCenter.delete(keyPath(model));
    }


}
