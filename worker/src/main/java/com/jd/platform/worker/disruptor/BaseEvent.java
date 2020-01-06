package com.jd.platform.worker.disruptor;

import com.jd.platform.common.model.BaseModel;

/**
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class BaseEvent<T extends BaseModel> {
    private T model;

    public BaseEvent(T model) {
        this.model = model;
    }

    public BaseEvent() {
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
