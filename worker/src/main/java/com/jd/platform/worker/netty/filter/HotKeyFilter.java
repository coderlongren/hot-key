package com.jd.platform.worker.netty.filter;

import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.MessageType;
import com.jd.platform.common.tool.FastJsonUtils;
import com.jd.platform.worker.disruptor.MessageProducer;
import com.jd.platform.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.worker.mq.IMqMessageReceiver;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 热key消息，包括从netty来的和mq来的。收到消息，都发到disruptor去
 *
 * @author wuweifeng wrote on 2019-12-11
 * @version 1.0
 */
@Component
@Order(3)
public class HotKeyFilter implements INettyMsgFilter, IMqMessageReceiver {
    @Resource
    private MessageProducer<HotKeyEvent> messageProducer;

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.REQUEST_NEW_KEY == message.getMessageType()) {
            publishMsg(message.getBody());

            return false;
        }

        return true;
    }

    @Override
    public void receive(String msg) {
        publishMsg(msg);
    }

    private void publishMsg(String message) {
        HotKeyModel model = FastJsonUtils.toBean(message, HotKeyModel.class);
        messageProducer.publish(new HotKeyEvent(model));
    }
}
