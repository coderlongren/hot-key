package com.jd.platform.hotkey.worker.disruptor.hotkey;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.disruptor.BaseEvent;

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
