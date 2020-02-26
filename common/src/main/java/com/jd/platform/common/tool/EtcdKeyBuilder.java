package com.jd.platform.common.tool;

import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.model.HotKeyModel;

/**
 * @author wuweifeng wrote on 2020-02-24
 * @version 1.0
 */
public class EtcdKeyBuilder {
    public static String keyPath(HotKeyModel hotKeyModel) {
        return ConfigConstant.hotKeyPath + hotKeyModel.getAppName() + "/" + hotKeyModel.getKey();
    }
}
