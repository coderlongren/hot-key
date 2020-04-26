package com.jd.platform.hotkey.client.etcd;

import cn.hutool.core.collection.CollectionUtil;
import com.ibm.etcd.api.Event;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.client.kv.KvClient;
import com.ibm.etcd.client.kv.WatchUpdate;
import com.jd.platform.hotkey.client.Context;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.jd.platform.hotkey.client.callback.ReceiveNewKeyEvent;
import com.jd.platform.hotkey.client.core.eventbus.EventBusCenter;
import com.jd.platform.hotkey.client.core.rule.KeyRuleInfoChangeEvent;
import com.jd.platform.hotkey.client.core.worker.WorkerInfoChangeEvent;
import com.jd.platform.hotkey.client.log.JdLogger;
import com.jd.platform.hotkey.common.configcenter.ConfigConstant;
import com.jd.platform.hotkey.common.configcenter.IConfigCenter;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.rule.KeyRule;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import io.grpc.StatusRuntimeException;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * etcd连接管理器
 *
 * @author wuweifeng wrote on 2019-12-10
 * @version 1.0
 */
public class EtcdStarter {

    public void start() {
        fetchWorkerInfo();

        fetchRule();

        startWatchWorker();

        startWatchRule();

        //监听热key事件，worker探测出来后也会推给etcd，到时client会收到来自于worker和来自于etcd的两个热key事件，如果是新增，
        //就只处理worker的就行。如果是删除，可能是etcd的热key过期删除，也可能是手工删除的
        //只监听手工的增删？
        startWatchHotKey();
    }

    /**
     * 拉取worker信息
     */
    private void fetchWorkerInfo() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            JdLogger.info(getClass(), "trying to connect to etcd and fetch worker info");
            boolean success = fetch();
            if (success) {
                scheduledExecutorService.shutdown();
            }

        }, 0, 5, TimeUnit.SECONDS);
    }

    private synchronized boolean fetch() {
        IConfigCenter configCenter = EtcdConfigFactory.configCenter();

        try {
            //获取所有worker的ip
            List<KeyValue> keyValues = configCenter.getPrefix(ConfigConstant.workersPath);
            //worker为空，可能是worker后启动。就先不管了，等待监听变化吧
            if (CollectionUtil.isEmpty(keyValues)) {
                JdLogger.warn(getClass(), "very important warn !!! workers ip info is null!!!");
                notifyWorkerChange(new ArrayList<>());
                return false;
            } else {
                List<String> addresses = new ArrayList<>();
                for (KeyValue keyValue : keyValues) {
                    //value里放的是ip地址
                    String ipPort = keyValue.getValue().toStringUtf8();
                    addresses.add(ipPort);
                }
                JdLogger.info(getClass(), "worker info list is : " + addresses);
                //发布workerinfo变更信息
                notifyWorkerChange(addresses);
                return true;
            }
        } catch (StatusRuntimeException ex) {
            //etcd连不上
            JdLogger.error(getClass(), "etcd connected fail. Check the etcd address!!!");
            return false;
        }

    }

    private void notifyWorkerChange(List<String> addresses) {
        EventBusCenter.getInstance().post(new WorkerInfoChangeEvent(addresses));
    }

    private void notifyRuleChange(List<KeyRule> rules) {
        EventBusCenter.getInstance().post(new KeyRuleInfoChangeEvent(rules));
    }

    /**
     * 异步开始监听热key变化信息
     */
    private void startWatchHotKey() {
        CompletableFuture.runAsync(() -> {
            JdLogger.info(getClass(), "--- begin watch hotKey change ----");
            IConfigCenter configCenter = EtcdConfigFactory.configCenter();
            try {
                KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.hotKeyPath + Context.APP_NAME);
                //如果有新事件，即新key产生或删除
                while (watchIterator.hasNext()) {
                    WatchUpdate watchUpdate = watchIterator.next();

                    List<Event> eventList = watchUpdate.getEvents();
                    KeyValue keyValue = eventList.get(0).getKv();
                    Event.EventType eventType = eventList.get(0).getType();
                    try {
                        String key = keyValue.getKey().toStringUtf8().replace(ConfigConstant.hotKeyPath + Context.APP_NAME + "/", "");

                        //如果是删除key，就立刻删除
                        if (Event.EventType.DELETE == eventType) {
                            HotKeyModel model = new HotKeyModel();
                            model.setRemove(true);
                            model.setKey(key);
                            EventBusCenter.getInstance().post(new ReceiveNewKeyEvent(model));
                        } else {
                            //如果已经是热key了，就不处理
                            if (JdHotKeyStore.isHotKey(key)) {
                                return;
                            }
                            JdLogger.info(getClass(), "receive new key : " + key);
                            //如果不是，那就是手工添加的
                            HotKeyModel model = new HotKeyModel();
                            model.setRemove(false);
                            model.setCreateTime(Long.valueOf(keyValue.getValue().toStringUtf8()));
                            model.setKey(key);
                            EventBusCenter.getInstance().post(new ReceiveNewKeyEvent(model));
                        }
                    } catch (Exception e) {
                        JdLogger.error(getClass(), "new key err ：" + keyValue);
                    }

                }
            } catch (Exception e) {
                JdLogger.error(getClass(), "watch err");
            }
        });

    }

    /**
     * 异步开始监听worker变化信息
     */
    private void startWatchWorker() {
        CompletableFuture.runAsync(() -> {
            JdLogger.info(getClass(), "--- begin watch worker change ----");
            IConfigCenter configCenter = EtcdConfigFactory.configCenter();
            try {
                KvClient.WatchIterator watchIterator = configCenter.watchPrefix(ConfigConstant.workersPath);
                //如果有新事件，即worker的变更，就重新拉取所有的信息
                while (watchIterator.hasNext()) {
                    JdLogger.info(getClass(), "worker info changed. begin to fetch new infos");
                    WatchUpdate watchUpdate = watchIterator.next();
                    List<Event> eventList = watchUpdate.getEvents();
                    System.err.println(eventList.get(0).getKv());

                    //全量拉取worker信息
                    fetch();
                }
            } catch (Exception e) {
                JdLogger.error(getClass(), "watch err");
            }
        });

    }

    private void fetchRule() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //开启拉取etcd的worker信息，如果拉取失败，则定时继续拉取
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            JdLogger.info(getClass(), "trying to connect to etcd and fetch rule info");
            boolean success = fetchRuleFromEtcd();
            if (success) {
                scheduledExecutorService.shutdown();
            }

        }, 0, 5, TimeUnit.SECONDS);
    }

    private boolean fetchRuleFromEtcd() {
        IConfigCenter configCenter = EtcdConfigFactory.configCenter();
        try {
            //从etcd获取自己的rule
            String rules = configCenter.get(ConfigConstant.rulePath + Context.APP_NAME);
            if (StringUtil.isNullOrEmpty(rules)) {
                JdLogger.warn(getClass(), "rule is empty");
                return true;
            }
            List<KeyRule> ruleList = FastJsonUtils.toList(rules, KeyRule.class);

            notifyRuleChange(ruleList);
            return true;
        } catch (StatusRuntimeException ex) {
            //etcd连不上
            JdLogger.error(getClass(), "etcd connected fail. Check the etcd address!!!");
            return false;
        } catch (Exception e) {
            JdLogger.error(getClass(), "fetch rule failure, please check the rule info in etcd");
            return true;
        }

    }

    /**
     * 异步监听rule规则变化
     */
    private void startWatchRule() {
        CompletableFuture.runAsync(() -> {
            JdLogger.info(getClass(), "--- begin watch rule change ----");
            try {
                IConfigCenter configCenter = EtcdConfigFactory.configCenter();
                KvClient.WatchIterator watchIterator = configCenter.watch(ConfigConstant.rulePath + Context.APP_NAME);
                //如果有新事件，即rule的变更，就重新拉取所有的信息
                while (watchIterator.hasNext()) {
                    JdLogger.info(getClass(), "rules info changed. begin to fetch new infos");
                    WatchUpdate watchUpdate = watchIterator.next();
                    List<Event> eventList = watchUpdate.getEvents();
                    System.err.println(eventList.get(0).getKv());

                    //全量拉取rule信息
                    fetchRuleFromEtcd();
                }
            } catch (Exception e) {
                JdLogger.error(getClass(), "watch err");
            }


        });
    }

}