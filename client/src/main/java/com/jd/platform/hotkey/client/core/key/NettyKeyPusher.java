package com.jd.platform.hotkey.client.core.key;

import com.jd.platform.hotkey.client.core.worker.WorkerInfoHolder;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.MsgBuilder;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
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
            channel.writeAndFlush(MsgBuilder.buildMsg(new HotKeyMsg(MessageType.REQUEST_NEW_KEY, FastJsonUtils.convertObjectToJSON(model))));
        }

    }


}
