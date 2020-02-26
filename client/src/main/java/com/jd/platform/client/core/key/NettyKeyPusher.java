package com.jd.platform.client.core.key;

import com.jd.platform.client.core.worker.WorkerInfoHolder;
import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.MessageType;
import com.jd.platform.common.tool.FastJsonUtils;
import io.netty.channel.Channel;

import java.util.List;

/**
 * 将msg推送到netty的pusher
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class NettyKeyPusher implements IKeyPusher {

    @Override
    public void send(String appName, List<HotKeyModel> list) {
        //积攒了半秒的key集合，按照hash分发到不同的worker
        long now = System.currentTimeMillis();
        for(HotKeyModel model : list) {
            model.setCreateTime(now);
            Channel channel = WorkerInfoHolder.chooseChannel(model.getKey());
            if (channel == null) {
                continue;
            }
            channel.writeAndFlush(new HotKeyMsg(MessageType.REQUEST_NEW_KEY, FastJsonUtils.convertObjectToJSON(model)));
        }

    }


}
