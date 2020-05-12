package com.jd.platform.hotkey.worker.disruptor;

import cn.hutool.core.date.SystemClock;
import com.jd.platform.hotkey.common.model.BaseModel;
import com.jd.platform.hotkey.common.tool.Constant;
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
        if (model == null || model.getKey() == null) {
            return;
        }
        if (model.getKey().hashCode() % Constant.Default_Threads == Math.abs(hashIndex)) {
            //5秒前的过时消息就不处理了
            if (SystemClock.now() - model.getCreateTime() > 5000) {
                return;
            }
            onEvent(t);
        }
    }

    protected abstract void onEvent(T t);
}