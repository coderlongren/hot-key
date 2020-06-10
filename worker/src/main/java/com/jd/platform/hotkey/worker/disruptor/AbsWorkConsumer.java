package com.jd.platform.hotkey.worker.disruptor;

import cn.hutool.core.date.SystemClock;
import com.jd.platform.hotkey.common.model.BaseModel;
import com.jd.platform.hotkey.worker.tool.InitConstant;
import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.atomic.LongAdder;

/**
 * 各个消费者不重复消费
 *
 * @author wuweifeng wrote on 2019-08-21.
 */
public abstract class AbsWorkConsumer<T extends BaseEvent> implements WorkHandler<T> {

    private int hashIndex;

    public static final LongAdder totalDealCount = new LongAdder();
    //过期的
    public static final LongAdder expireTotalCount = new LongAdder();

    public AbsWorkConsumer(int hashIndex) {
        this.hashIndex = hashIndex;
    }

    @Override
    public void onEvent(T t) {
        BaseModel model = t.getModel();
        if (model == null || model.getKey() == null) {
            return;
        }
        //5秒前的过时消息就不处理了
        if (SystemClock.now() - model.getCreateTime() > InitConstant.timeOut) {
            expireTotalCount.increment();
            if (InitConstant.openTimeOut) {
                return;
            }
        }
        onNewEvent(t);

        //处理完毕，将数量加1
        totalDealCount.increment();
    }

    protected abstract void onNewEvent(T t);

}