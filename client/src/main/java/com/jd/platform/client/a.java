package com.jd.platform.client;

import com.jd.platform.client.core.ClientBuilder;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class a {
    public static void main(String[] args) throws InterruptedException {
        ClientBuilder clientBuilder = new ClientBuilder("wuwf");
        clientBuilder.start();
    }
}
