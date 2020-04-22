package com.jd.platform.hotkey.common.coder;

import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.model.typeenum.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        byteBuf.markReaderIndex();
        HotKeyMsg message = new HotKeyMsg();
        message.setMagicNumber(byteBuf.readInt());  // 读取魔数

        MessageType messageType = MessageType.get(byteBuf.readByte());
        if (messageType == null) {
            logger.error("messageType is null , byteBuf readByte = " + byteBuf.readByte());
            return;
        }
        message.setMessageType(messageType);	// 读取当前的消息类型

        int bodyLength = byteBuf.readInt();	// 读取消息体长度和数据
        CharSequence body = byteBuf.readCharSequence(bodyLength, Charset.defaultCharset());
        message.setBody(body.toString());
        out.add(message);
    }
}
