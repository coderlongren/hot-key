package com.jd.platform.client.model;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author wuweifeng wrote on 2020-01-13
 * @version 1.0
 */
public class WorkerInfoHolder {
    /**
     * 保存worker的ip地址和Channel的映射关系，这是有序的。每次client发送消息时，都会根据该map的size进行hash
     * 如key-1就发送到workerHolder的第1个Channel去，key-2就发到第2个Channel去
     */
    private static ConcurrentSkipListMap<String, Channel> workerHolder = new ConcurrentSkipListMap<>();

    private WorkerInfoHolder() {}

    /**
     * 根据传过来的所有的worker地址，返回当前尚未连接的新的worker地址集合，用以创建新连接
     */
    public static List<String> newWorkers(List<String> allAddresses) {
        List<String> list = new ArrayList<>();
        for (String s : allAddresses) {
            if (!workerHolder.containsKey(s)) {
                list.add(s);
            }
        }
        return list;
    }

    public static void put(String address, Channel channel) {
        if (!workerHolder.containsKey(address)) {
            workerHolder.put(address, channel);
        }
    }

    /**
     * 移除那些在最新的worker地址集里没有的那些
     */
    public static void removeNoneUsed(List<String> allAddresses) {
        for (String key : workerHolder.keySet()) {
            if (!allAddresses.contains(key)) {
                workerHolder.remove(key);
            }
        }
    }
}
