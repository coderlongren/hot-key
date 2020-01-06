package com.jd.platform.worker.netty.encoder;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-13
 * @version 1.0
 */
@ChannelHandler.Sharable
public class DelimiterBasedFrameEncoder extends MessageToMessageEncoder<CharSequence> {

    private final Charset charset;

    public DelimiterBasedFrameEncoder() {
        this(Charset.defaultCharset());
    }

    public DelimiterBasedFrameEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if (msg.length() != 0) {
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg + "$"), this.charset));
        }
    }
}
