package com.jd.platform.hotkey.worker.disruptor.hotkey;

import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.worker.disruptor.AbsWorkConsumer;
import com.jd.platform.hotkey.worker.keylistener.IKeyListener;
import com.jd.platform.hotkey.worker.keylistener.KeyEventOriginal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 热key的消费者
 * @author wuweifeng wrote on 2019-12-12.
 */
public class HotKeyEventConsumer extends AbsWorkConsumer<HotKeyEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private IKeyListener iKeyListener;

    public HotKeyEventConsumer(int hashIndex) {
        super(hashIndex);
    }

    @Override
    protected void onNewEvent(HotKeyEvent hotKeyEvent) {
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