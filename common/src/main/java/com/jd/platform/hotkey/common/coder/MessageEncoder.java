package com.jd.platform.hotkey.common.coder;

import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import com.jd.platform.hotkey.common.tool.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class MessageEncoder extends MessageToByteEncoder<HotKeyMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HotKeyMsg message, ByteBuf out) {
        MessageType messageType = message.getMessageType();
        //非法类型
        if (MessageType.get(messageType.getType()) == null) {
            return;
        }
        // 这里会判断消息类型是不是EMPTY类型，如果是EMPTY类型，则表示当前消息不需要写入到管道中
        if (messageType == MessageType.EMPTY) {
            return;
        }
        //4个byte
        out.writeInt(Constant.MAGIC_NUMBER);    // 写入当前的魔数
        //1个byte
        out.writeByte(message.getMessageType().getType());    // 写入当前消息的类型

        if (null == message.getBody()) {
            out.writeInt(0);    // 如果消息体为空，则写入0，表示消息体长度为0
        } else {
            System.out.println(message.getBody().length());
            out.writeInt(message.getBody().length());
            out.writeCharSequence(message.getBody(), Charset.defaultCharset());
        }
        out.writeBytes(Constant.DELIMITER.getBytes());
    }

    public static void main(String[] args) {
        System.out.println(Constant.MAGIC_NUMBER + "");
    }
}
