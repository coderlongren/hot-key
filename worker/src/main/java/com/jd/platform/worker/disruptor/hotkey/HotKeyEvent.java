package com.jd.platform.worker.disruptor.hotkey;

import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.worker.disruptor.BaseEvent;

/**
 * @author wuweifeng wrote on 2019-08-21.
 */
public class HotKeyEvent extends BaseEvent<HotKeyModel> {

    public HotKeyEvent(HotKeyModel hotKeyModel) {
        super(hotKeyModel);
    }

    public HotKeyEvent() {

    }
}
