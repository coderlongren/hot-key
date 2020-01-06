package com.jd.platform.client.netty.encoder;

import com.jd.platform.common.model.HotKeyMsg;
import com.jd.platform.common.model.typeenum.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        HotKeyMsg message = new HotKeyMsg();
        message.setMagicNumber(byteBuf.readInt());  // 读取魔数
        message.setMessageType(MessageType.get(byteBuf.readByte()));	// 读取当前的消息类型

        int bodyLength = byteBuf.readInt();	// 读取消息体长度和数据
        CharSequence body = byteBuf.readCharSequence(bodyLength, Charset.defaultCharset());
        message.setBody(body.toString());
        out.add(message);
    }
}
