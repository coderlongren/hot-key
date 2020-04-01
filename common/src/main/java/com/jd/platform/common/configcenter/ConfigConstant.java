package com.jd.platform.common.configcenter;

/**
 * @author wuweifeng wrote on 2019-12-06
 * @version 1.0
 */
public interface ConfigConstant {
    /**
     * 所有的app名字，存这里
     */
    String appsPath = "/jd/apps/";
    /**
     * 所有的workers，存这里
     */
    String workersPath = "/jd/workers/";
    /**
     * 该app所有的workers地址的path。需要手工分配，默认每个app都用所有的worker
     */
    String appWorkerPath = null;
    /**
     * 所有的客户端规则（譬如哪个app的哪些前缀的才参与计算）
     */
    String rulePath = "/jd/rules/";
    /**
     * 每个app的热key放这里。格式如：jd/hotkeys/app1/userA
     */
    String hotKeyPath = "/jd/hotkeys/";
}
