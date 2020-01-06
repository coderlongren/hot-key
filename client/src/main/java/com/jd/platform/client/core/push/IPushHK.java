package com.jd.platform.client.core.push;

import com.jd.platform.common.model.HotKeyModel;

import java.util.List;

/**
 * 客户端上传热key到worker接口
 * @author wuweifeng wrote on 2020-01-06
 * @version 1.0
 */
public interface IPushHK {
    void send(String appName, List<HotKeyModel> list);
}
