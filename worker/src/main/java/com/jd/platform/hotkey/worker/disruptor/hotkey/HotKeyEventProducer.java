package com.jd.platform.hotkey.worker.disruptor.hotkey;

import com.jd.platform.hotkey.worker.disruptor.MessageProducer;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * 所有节点发来的消息，都进入这里，然后publish出去，供消费者消费
 *
 * @author wuweifeng wrote on 2019/11/6.
 */
public class HotKeyEventProducer implements MessageProducer<HotKeyEvent> {

    private Disruptor<HotKeyEvent> disruptor;

    public HotKeyEventProducer(Disruptor<HotKeyEvent> disruptor) {
        this.disruptor = disruptor;
    }

    @Override
    public void publish(HotKeyEvent hotKeyEvent) {
        RingBuffer<HotKeyEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            HotKeyEvent event = ringBuffer.get(sequence);
            event.setModel(hotKeyEvent.getModel());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    //nioEventLoopGroup-3-63" #206 prio=10 os_prio=0 tid=0x00007ff498077000 nid=0x6a6 runnable [0x00007ff422f44000]
    //   java.lang.Thread.State: TIMED_WAITING (parking)
    //        at sun.misc.Unsafe.park(Native Method)
    //        at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:338)
    //        at com.lmax.disruptor.SingleProducerSequencer.next(SingleProducerSequencer.java:137)
    //        at com.lmax.disruptor.SingleProducerSequencer.next(SingleProducerSequencer.java:110)
    //        at com.lmax.disruptor.RingBuffer.next(RingBuffer.java:263)
    //        at com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventProducer.publish(HotKeyEventProducer.java:23)
    //        at com.jd.platform.hotkey.worker.disruptor.hotkey.HotKeyEventProducer.publish(HotKeyEventProducer.java:12)
    //        at com.jd.platform.hotkey.worker.netty.filter.HotKeyFilter.publishMsg(HotKeyFilter.java:46)
    //        at com.jd.platform.hotkey.worker.netty.filter.HotKeyFilter.chain(HotKeyFilter.java:31)
    //        at com.jd.platform.hotkey.worker.netty.server.NodesServerHandler.channelRead0(NodesServerHandler.java:38)
    //        at com.jd.platform.hotkey.worker.netty.server.NodesServerHandler.channelRead0(NodesServerHandler.java:21)
    //        at io.netty.channel.SimpleChannelInboundHandler.channelRead(SimpleChannelInboundHandler.java:99)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:374)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:360)
    //        at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:352)
    //        at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:102)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:374)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:360)
    //        at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:352)
    //        at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:326)
    //        at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:300)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:374)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:360)
    //        at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:352)
    //        at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1422)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:374)
    //        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:360)
    //        at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:931)
    //        at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:163)
    //        at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:700)
    //        at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:635)
    //        at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:552)
    //        at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:514)
    //        at io.netty.util.concurrent.SingleThreadEventExecutor$6.run(SingleThreadEventExecutor.java:1050)
    //        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
    //        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
    //        at java.lang.Thread.run(Thread.java:745)
}
