package com.jd.platform.hotkey.client.core.key;

import com.jd.platform.hotkey.common.model.HotKeyModel;

import java.util.List;

/**
 * 对hotkey进行聚合
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public interface IKeyCollector {
    List<HotKeyModel> lockAndGetResult();

    void collect(HotKeyModel hotKeyModel);

    void finishOnce();
}
