package com.jd.platform.hotkey.worker.netty.filter;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.worker.disruptor.MessageProducer;
import com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEvent;
import com.jd.platform.hotkey.worker.mq.IMqMessageReceiver;
import com.jd.platform.hotkey.worker.netty.holder.WhiteListHolder;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    public static AtomicLong totalReceiveKeyCount = new AtomicLong();

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.REQUEST_NEW_KEY == message.getMessageType()) {
            totalReceiveKeyCount.incrementAndGet();

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
        //老版的用的单个HotKeyModel，新版用的数组
        if (message.startsWith("[")) {
            List<HotKeyModel> models = FastJsonUtils.toList(message, HotKeyModel.class);
            for (HotKeyModel model : models) {
                //白名单key不处理
                if (WhiteListHolder.contains(model.getKey())) {
                    continue;
                }
                messageProducer.publish(new HotKeyEvent(model));
            }
        } else if (message.startsWith("{")) {
            HotKeyModel model = FastJsonUtils.toBean(message, HotKeyModel.class);
            if (WhiteListHolder.contains(model.getKey())) {
                return;
            }
            messageProducer.publish(new HotKeyEvent(model));
        }

    }

}