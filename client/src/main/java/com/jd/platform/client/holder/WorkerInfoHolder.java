package com.jd.platform.client.holder;

import cn.hutool.core.util.StrUtil;
import com.jd.platform.client.netty.NettyClient;
import io.netty.channel.Channel;

import java.util.*;

/**
 * @author wuweifeng wrote on 2020-01-13
 * @version 1.0
 */
public class WorkerInfoHolder {
    /**
     * 保存worker的ip地址和Channel的映射关系，这是有序的。每次client发送消息时，都会根据该map的size进行hash
     * 如key-1就发送到workerHolder的第1个Channel去，key-2就发到第2个Channel去
     */
    private static final List<Server> workerHolder = new ArrayList<>();


    private WorkerInfoHolder() {
    }

    public static Channel chooseChannel(String key) {
        synchronized (workerHolder) {
            if (StrUtil.isEmpty(key)) {
                return null;
            }
            int index = Math.abs(key.hashCode() % workerHolder.size());

            return workerHolder.get(index).channel;
        }
    }

    public static void main(String[] args) {
        List<String> allAddresses = new ArrayList<>();
        allAddresses.add("a:80");
        allAddresses.add("b:80");
        allAddresses.add("aC:80");
        mergeAndConnectNew(allAddresses);

        List<String> a1 = new ArrayList<>();
        a1.add("b:80");
        a1.add("aC:80");
        mergeAndConnectNew(a1);

        List<String> a2 = new ArrayList<>();
        mergeAndConnectNew(a2);
    }

    /**
     * etcd监听到worker信息变化后
     * 将新的worker信息和当前的进行合并，并且连接新的address
     * address例子：10.12.139.152:11111
     */
    public static void mergeAndConnectNew(List<String> allAddresses) {
        synchronized (workerHolder) {
            removeNoneUsed(allAddresses);

            //去连接那些在etcd里有，但是list里没有的
            List<String> needConnectWorkers = WorkerInfoHolder.newWorkers(allAddresses);

            //再连接，连上后，value就有值了
            NettyClient.getInstance().connect(needConnectWorkers);

            Collections.sort(workerHolder);
        }
    }

    /**
     * 处理某个worker的channel断线事件
     * 如果etcd里已经没有了，就从holder里remove掉，如果etcd里还有，就去重连
     */
    public static boolean dealChannelInactive(String address) {
        synchronized (workerHolder) {
            Iterator<Server> it = workerHolder.iterator();
            boolean exist = false;
            while (it.hasNext()) {
                Server server = it.next();
                if (address.equals(server.address)) {
                    exist = true;
                    break;
                }
            }
            //如果holder里已经没有该worker信息里，就不用处理了
            if (!exist) {
                return true;
            }
            //如果在holder里还有，说明worker和etcd的连接还没断，就需要重连了
            return NettyClient.getInstance().connect(Arrays.asList(address));
        }
    }

    /**
     * 增加一个新的worker
     */
    public static void put(String address, Channel channel) {
        Iterator<Server> it = workerHolder.iterator();
        boolean exist = false;
        while (it.hasNext()) {
            Server server = it.next();
            if (address.equals(server.address)) {
                server.channel = channel;
                exist = true;
                break;
            }
        }
        if (!exist) {
            Server server = new Server();
            server.address = address;
            server.channel = channel;
            workerHolder.add(server);
        }

    }

    /**
     * 根据传过来的所有的worker地址，返回当前尚未连接的新的worker地址集合，用以创建新连接
     */
    private static List<String> newWorkers(List<String> allAddresses) {
        List<String> list = new ArrayList<>();

        Iterator<Server> it = workerHolder.iterator();
        for (String s : allAddresses) {
            boolean exist = false;
            while (it.hasNext()) {
                String nowAddress = it.next().address;
                if (s.equals(nowAddress)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                list.add(s);
            }

        }
        return list;
    }

    /**
     * 移除那些在最新的worker地址集里没有的那些
     */
    private static void removeNoneUsed(List<String> allAddresses) {
        Iterator<Server> it = workerHolder.iterator();
        while (it.hasNext()) {
            boolean exist = false;
            //判断现在的worker里是否存在，如果当前的不存在，则删掉
            String nowAddress = it.next().address;
            for (String address : allAddresses) {
                if (address.equals(nowAddress)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                it.remove();
            }
        }
    }


    private static class Server implements Comparable<Server> {
        private String address;
        private Channel channel;


        @Override
        public int compareTo(Server o) {
            //按address排序
            return this.address.compareTo(o.address);
        }
    }
}
