package com.jd.platform.client.core.push;

import com.jd.platform.common.model.HotKeyModel;

import java.util.List;

/**
 * 将msg推送到netty的pusher
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class NettyPusher implements IPushHK {

    @Override
    public void send(String appName, List<HotKeyModel> list) {


        //积攒了半秒的key集合，按照hash分发到不同的worker
        long now = System.currentTimeMillis();
        for(HotKeyModel model : list) {
            model.setCreateTime(now);

        }



    }


}
