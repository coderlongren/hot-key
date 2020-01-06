package com.jd.platform.worker.config;

import com.jd.platform.common.configcenter.IConfigCenter;
import com.jd.platform.common.configcenter.etcd.JdEtcdBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author wuweifeng wrote on 2019-12-06
 * @version 1.0
 */
@Configuration
public class EtcdConfig {
    @Value("${etcd.server}")
    private String etcdServer;

    @Bean
    public IConfigCenter client() {
        //连接多个时，逗号分隔
        return JdEtcdBuilder.build(etcdServer);
    }

}
