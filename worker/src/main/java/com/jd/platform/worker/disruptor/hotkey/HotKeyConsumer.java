package com.jd.platform.worker.disruptor.hotkey;

import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.worker.disruptor.AbsConsumer;
import com.jd.platform.worker.eventlisten.keyevent.IKeyListener;
import com.jd.platform.worker.eventlisten.keyevent.KeyEventOriginal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 热key的消费者
 * @author wuweifeng wrote on 2019-12-12.
 */
public class HotKeyConsumer extends AbsConsumer<HotKeyEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private IKeyListener iKeyListener;

    public HotKeyConsumer(int hashIndex) {
        super(hashIndex);
    }

    @Override
    protected void onEvent(HotKeyEvent hotKeyEvent) {
        HotKeyModel model = hotKeyEvent.getModel();
        if (iKeyListener == null) {
            logger.warn("new key is coming, but no consumer deal this key!");
            return;
        }

        if (model.isRemove()) {
            iKeyListener.removeKey(model, KeyEventOriginal.CLIENT);
        } else {
            iKeyListener.newKey(model, KeyEventOriginal.CLIENT);
        }
    }

    public void setKeyListener(IKeyListener iKeyListener) {
        this.iKeyListener = iKeyListener;
    }
}