package com.jd.platform.client.core;

import com.jd.platform.client.model.WorkerInfo;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuweifeng wrote on 2019-12-05
 * @version 1.0
 */
public class Context {
    public static String appName;

    public static List<WorkerInfo> workerInfoList = new ArrayList<>();

    public static List<WorkerInfo> connectedInfoList = new ArrayList<>();

    public static void removeWorker(Channel channel) {
        for (WorkerInfo workerInfo : workerInfoList) {
            if (workerInfo.getChannel().equals(channel)) {
                //将该位置的channel置为null，将来恢复时，会回到原位置
                workerInfo.setChannel(null);
            }
        }

    }

    public static void addWorker(WorkerInfo workerInfo) {

    }

    public static WorkerInfo judge(String key) {
        return null;
    }
}
