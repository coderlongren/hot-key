package com.jd.platform.client.callback;

import com.jd.platform.common.model.HotKeyModel;

/**
 * @author wuweifeng wrote on 2020-02-21
 * @version 1.0
 */
public class ReceiveNewKeyEvent {
    private HotKeyModel model;

    public ReceiveNewKeyEvent(HotKeyModel model) {
        this.model = model;
    }

    public HotKeyModel getModel() {
        return model;
    }

    public void setModel(HotKeyModel model) {
        this.model = model;
    }
}
