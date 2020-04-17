package com.jd.platform.hotkey.worker.netty.pusher;

import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.rule.KeyRuleHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.jd.platform.hotkey.common.tool.EtcdKeyBuilder.keyPath;

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
                KeyRuleHolder.getRuleByAppAndKey(model).getDuration());
    }

    @Override
    public void remove(HotKeyModel model) {
        //推送etcd删除
        iConfigCenter.delete(keyPath(model));
    }


}
