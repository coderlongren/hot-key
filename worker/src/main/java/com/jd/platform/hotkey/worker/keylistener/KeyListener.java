package com.jd.platform.hotkey.worker.keylistener;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Joiner;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.rule.KeyRule;
import com.jd.platform.hotkey.worker.cache.CaffeineCacheHolder;
import com.jd.platform.hotkey.worker.netty.pusher.IPusher;
import com.jd.platform.hotkey.worker.rule.KeyRuleHolder;
import com.jd.platform.hotkey.worker.tool.SlidingWindow;
import com.jd.platform.hotkey.worker.tool.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * key的新增、删除处理
 *
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
@Component
public class KeyListener implements IKeyListener {
    @Resource(name = "hotKeyCache")
    private Cache<String, Object> hotCache;
    @Resource
    private List<IPusher> iPushers;

    private static final String SPLITER = "-";

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void newKey(HotKeyModel hotKeyModel, KeyEventOriginal original) {
        //cache里的key
        String key = buildKey(hotKeyModel);
        //判断是不是刚热不久
        Object o = hotCache.getIfPresent(key);
        if (o != null) {
            return;
        }

        SlidingWindow slidingWindow = checkWindow(hotKeyModel, key);
        //看看hot没
        boolean hot = slidingWindow.addCount(hotKeyModel.getCount());

        //删掉该key
        CaffeineCacheHolder.getCache(hotKeyModel.getAppName()).invalidate(key);
        if (!hot) {
            //如果没hot，重新put，cache会自动刷新过期时间
            CaffeineCacheHolder.getCache(hotKeyModel.getAppName()).put(key, slidingWindow);
        } else {
            hotCache.put(key, 1);

            //开启推送
            hotKeyModel.setCreateTime(SystemClock.now());
            logger.info("new key created event key : " + hotKeyModel.toString());

            for (IPusher pusher : iPushers) {
                pusher.push(hotKeyModel);
            }

        }

    }

    @Override
    public void removeKey(HotKeyModel hotKeyModel, KeyEventOriginal original) {
        //cache里的key
        String key = buildKey(hotKeyModel);

        hotCache.invalidate(key);
        CaffeineCacheHolder.getCache(hotKeyModel.getAppName()).invalidate(key);

        //推送所有client删除
        hotKeyModel.setCreateTime(SystemClock.now());
        logger.info("key delete event key : " + hotKeyModel.toString());

        for (IPusher pusher : iPushers) {
            pusher.remove(hotKeyModel);
        }

    }

    /**
     * 生成或返回该key的滑窗
     */
    private SlidingWindow checkWindow(HotKeyModel hotKeyModel, String key) {
        //取该key的滑窗
        SlidingWindow slidingWindow = (SlidingWindow) CaffeineCacheHolder.getCache(hotKeyModel.getAppName()).getIfPresent(key);
        //考虑在某个APP的rule变化后，清空该APP所有key
        if (slidingWindow == null) {
            //是个新key，获取它的规则
            KeyRule keyRule = KeyRuleHolder.getRuleByAppAndKey(hotKeyModel);
            slidingWindow = new SlidingWindow(keyRule.getInterval(), keyRule.getThreshold());
        }
        return slidingWindow;
    }

    private String buildKey(HotKeyModel hotKeyModel) {
        return Joiner.on(SPLITER).join(hotKeyModel.getAppName(), hotKeyModel.getKeyType(), hotKeyModel.getKey());
    }

}
