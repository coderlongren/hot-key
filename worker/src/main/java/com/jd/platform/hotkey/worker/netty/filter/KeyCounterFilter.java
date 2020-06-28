package com.jd.platform.hotkey.worker.netty.filter;

import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.KeyCountModel;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.worker.keydispatcher.KeyProducer;
import com.jd.platform.hotkey.worker.mq.IMqMessageReceiver;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对热key访问次数和总访问次数进行累计
 * @author wuweifeng wrote on 2020-6-24
 * @version 1.0
 */
@Component
@Order(4)
public class KeyCounterFilter implements INettyMsgFilter, IMqMessageReceiver {
    @Resource
    private KeyProducer keyProducer;


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean chain(HotKeyMsg message, ChannelHandlerContext ctx) {
        if (MessageType.REQUEST_HIT_COUNT == message.getMessageType()) {

            publishMsg(message.getAppName(), message.getBody(), ctx);

            return false;
        }

        return true;
    }

    @Override
    public void receive(String msg) {
        publishMsg("", msg, null);
    }

    private void publishMsg(String appName, String message, ChannelHandlerContext ctx) {
        //老版的用的单个HotKeyModel，新版用的数组
        List<KeyCountModel> models = FastJsonUtils.toList(message, KeyCountModel.class);
//        for (HotKeyModel model : models) {
//            //白名单key不处理
//            if (WhiteListHolder.contains(model.getKey())) {
//                continue;
//            }
//            long timeOut = SystemClock.now() - model.getCreateTime();
//            if (timeOut > 1000) {
//                logger.info("key timeout " + timeOut + ", from ip : " + NettyIpUtil.clientIp(ctx));
//            }
//            keyProducer.push(model);
//        }

    }

}