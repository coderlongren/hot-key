//package com.jd.platform.hotkey.worker.keydispatcher;
//
//import com.jd.platform.hotkey.common.model.HotKeyModel;
//import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
//import com.jd.platform.hotkey.worker.keylistener.KeyEventOriginal;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.atomic.LongAdder;
//
///**
// * @author wuweifeng
// * @version 1.0
// * @date 2020-06-09
// */
//public class KeyConsumer {
//    /**
//     * 累计处理完毕数量
//     */
//    public static final LongAdder totalDealCount = new LongAdder();
//
//    public static final LongAdder totalOfferCount = new LongAdder();
//
//    /**
//     * 队列
//     */
//    private BlockingQueue<HotKeyModel> queue = new LinkedBlockingQueue<>(10000);
//
//    private IKeyListener iKeyListener;
//
//    public void setKeyListener(IKeyListener iKeyListener) {
//        this.iKeyListener = iKeyListener;
//    }
//
//    public void offer(HotKeyModel hotKeyModel) {
//        try {
//            queue.put(hotKeyModel);
//            totalOfferCount.increment();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void beginConsume() {
//        while (true) {
//            try {
//                HotKeyModel model = queue.take();
//                if (model.isRemove()) {
//                    iKeyListener.removeKey(model, KeyEventOriginal.CLIENT);
//                } else {
//                    iKeyListener.newKey(model, KeyEventOriginal.CLIENT);
//                }
//
//                //处理完毕，将数量加1
//                totalDealCount.increment();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//}
