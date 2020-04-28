package com.jd.platform.hotkey.client.core.worker;

import cn.hutool.core.util.StrUtil;
import com.jd.platform.hotkey.client.log.JdLogger;
import com.jd.platform.hotkey.client.netty.NettyClient;
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
    private static final List<Server> WORKER_HOLDER = new ArrayList<>();


    private WorkerInfoHolder() {
    }

    public static List<Server> getWorkers() {
        return WORKER_HOLDER;
    }

    /**
     * 获取worker是存在，但自己没连上的address集合，供重连
     */
    public static List<String> getNonConnectedWorkers() {
        List<String> list = new ArrayList<>();
        for (Server server : WORKER_HOLDER) {
            if (server.channel == null) {
                list.add(server.address);
            }
        }
        return list;
    }

    public static Channel chooseChannel(String key) {
        synchronized (WORKER_HOLDER) {
            if (StrUtil.isEmpty(key) || WORKER_HOLDER.size() == 0) {
                return null;
            }
            int index = Math.abs(key.hashCode() % WORKER_HOLDER.size());

            return WORKER_HOLDER.get(index).channel;
        }
    }

    /**
     * etcd监听到worker信息变化后
     * 将新的worker信息和当前的进行合并，并且连接新的address
     * address例子：10.12.139.152:11111
     */
    public synchronized static void mergeAndConnectNew(List<String> allAddresses) {
        synchronized (WORKER_HOLDER) {
            removeNoneUsed(allAddresses);

            //去连接那些在etcd里有，但是list里没有的
            List<String> needConnectWorkers = newWorkers(allAddresses);
            if (needConnectWorkers.size() == 0) {
                return;
            }

            JdLogger.info(WorkerInfoHolder.class, "new workers : " + needConnectWorkers);

            //再连接，连上后，value就有值了
            NettyClient.getInstance().connect(needConnectWorkers);

            Collections.sort(WORKER_HOLDER);
        }
    }

    /**
     * 处理某个worker的channel断线事件
     * 如果etcd里已经没有了，就从holder里remove掉，如果etcd里还有，就去重连
     */
//    public synchronized static boolean dealChannelInactive(String address) {
//        synchronized (WORKER_HOLDER) {
//            Iterator<Server> it = WORKER_HOLDER.iterator();
//            boolean exist = false;
//            while (it.hasNext()) {
//                Server server = it.next();
//                if (address.equals(server.address)) {
//                    exist = true;
//                    break;
//                }
//            }
//            //如果holder里已经没有该worker信息里，就不用处理了
//            if (!exist) {
//                return true;
//            }
//            //如果在holder里还有，说明worker和etcd的连接还没断，就需要重连了
//            return NettyClient.getInstance().connect(Arrays.asList(address));
//        }
//    }

    /**
     * 增加一个新的worker
     */
    public synchronized static void put(String address, Channel channel) {
        Iterator<Server> it = WORKER_HOLDER.iterator();
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
            WORKER_HOLDER.add(server);
        }

    }

    /**
     * 根据传过来的所有的worker地址，返回当前尚未连接的新的worker地址集合，用以创建新连接
     */
    private static List<String> newWorkers(List<String> allAddresses) {
        Set<String> set = new HashSet<>(WORKER_HOLDER.size());
        for (Server server : WORKER_HOLDER) {
            set.add(server.address);
        }

        List<String> list = new ArrayList<>();
        for (String s : allAddresses) {
            if (!set.contains(s)) {
                list.add(s);
            }
        }

        return list;
    }

    /**
     * 移除那些在最新的worker地址集里没有的那些
     */
    private static void removeNoneUsed(List<String> allAddresses) {
        Iterator<Server> it = WORKER_HOLDER.iterator();
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
                JdLogger.info(WorkerInfoHolder.class, "worker remove : " + nowAddress);
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

        @Override
        public String toString() {
            return "Server{" +
                    "address='" + address + '\'' +
                    ", channel=" + channel +
                    '}';
        }
    }
}
