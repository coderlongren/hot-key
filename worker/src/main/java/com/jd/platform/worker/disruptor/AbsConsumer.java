package com.jd.platform.worker.disruptor;

import com.jd.platform.common.model.BaseModel;
import com.jd.platform.common.tool.Constant;
import com.jd.platform.worker.tool.SystemClock;
import com.lmax.disruptor.EventHandler;

/**
 * @author wuweifeng wrote on 2019-08-21.
 */
public abstract class AbsConsumer<T extends BaseEvent> implements EventHandler<T> {

    private int hashIndex;

    public AbsConsumer(int hashIndex) {
        this.hashIndex = hashIndex;
    }

    @Override
    public void onEvent(T t, long l, boolean b) {
        //每个消费者，只处理特定的key。保证相同的key，一定被同一个线程处理
        BaseModel model = t.getModel();
        if (model.getKey().hashCode() % Constant.Default_Threads == hashIndex
                || model.getKey().hashCode() % Constant.Default_Threads == -hashIndex) {
            //5秒前的过时消息就不处理了
            if (SystemClock.now() - model.getCreateTime() > 5000) {
                return;
            }
            onEvent(t);
        }
    }

    protected abstract void onEvent(T t);
}