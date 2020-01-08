package com.jd.platform.client.core.push;

import com.jd.platform.common.model.HotKeyModel;

import java.nio.channels.Channel;
import java.util.List;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class NettyPusher implements IPushHK {

    private List<Channel> channels;

    public NettyPusher(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void send(String appName, List<HotKeyModel> list) {

        long now = System.currentTimeMillis();
        for(HotKeyModel model : list) {
            model.setCreateTime(now);

        }



    }


}
