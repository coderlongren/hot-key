package com.jd.platform.client.etcd;

import com.jd.platform.common.configcenter.IConfigCenter;
import com.jd.platform.common.configcenter.etcd.JdEtcdBuilder;

/**
 * @author wuweifeng wrote on 2020-01-07
 * @version 1.0
 */
public class EtcdConfigFactory {
    private static IConfigCenter configCenter;

    public static IConfigCenter configCenter() {
        return configCenter;
    }

    public static void buildConfigCenter(String etcdServer) {
        //连接多个时，逗号分隔
        configCenter = JdEtcdBuilder.build(etcdServer);
    }
}
