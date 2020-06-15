package com.jd.platform.hotkey.dashboard.erp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErpConfiguration {

    @Autowired
    ErpProperties erpProperties;

    @Bean
    public ErpUimInterceptor erpUimInterceptor() {
        ErpUimInterceptor springSSOInterceptor = new ErpUimInterceptor();
        springSSOInterceptor.setSsoAppKey(erpProperties.getSsoAppKey());
        springSSOInterceptor.setSsoAppToken(erpProperties.getSsoAppToken());
        springSSOInterceptor.setLoginUrl(erpProperties.getLoginUrl());
        springSSOInterceptor.setExcludePath(erpProperties.getExcludePath());
        springSSOInterceptor.setSsoAppUrl(erpProperties.getSsoAppUrl());
        return springSSOInterceptor;
    }
}
