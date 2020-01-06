package com.jd.platform.worker.eventlisten.keyevent;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Joiner;
import com.jd.platform.common.configcenter.ConfigConstant;
import com.jd.platform.common.configcenter.IConfigCenter;
import com.jd.platform.common.model.HotKeyModel;
import com.jd.platform.common.rule.KeyRateRule;
import com.jd.platform.worker.model.KeyRuleHolder;
import com.jd.platform.worker.netty.holder.ClientInfoHolder;
import com.jd.platform.worker.tool.SlidingWindow;
import com.jd.platform.worker.tool.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * key的新增、删除处理
 *
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
@Component
public class KeyListener implements IKeyListener {
    @Resource(name = "allKeyCache")
    private Cache<String, Object> cache;
    @Resource(name = "hotKeyCache")
    private Cache<String, Object> hotCache;
    @Resource
    private IConfigCenter iConfigCenter;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void newKey(HotKeyModel hotKeyModel, KeyEventOriginal original) {
        //cache里的key
        String key = key(hotKeyModel);
        //判断是不是刚热不久
        Object o = hotCache.getIfPresent(key);
        if (o != null) {
            return;
        }

        SlidingWindow slidingWindow = checkWindow(hotKeyModel, key);
        //看看hot没
        boolean hot = slidingWindow.addCount(hotKeyModel.getCount());
        if (!hot) {
            //如果没hot，重新put，cache会自动刷新过期时间
            cache.put(key, slidingWindow);
        } else {
            hotCache.put(key, 1);

            //开启推送
            hotKeyModel.setCreateTime(SystemClock.now());
            logger.info("new key created event push : " + hotKeyModel.toString());
            ClientInfoHolder.pushToApp(hotKeyModel.getAppName(), hotKeyModel);

            //推送到etcd
            iConfigCenter.putAndGrant(keyPath(hotKeyModel, key), "1",
                    KeyRuleHolder.getRuleByAppAndKey(hotKeyModel).getContinued());
        }

    }

    @Override
    public void removeKey(HotKeyModel hotKeyModel, KeyEventOriginal original) {
        //cache里的key
        String key = key(hotKeyModel);

        hotCache.invalidate(key);
        cache.invalidate(key);

        //推送所有client删除
        hotKeyModel.setCreateTime(SystemClock.now());
        logger.info("key delete event push : " + hotKeyModel.toString());
        ClientInfoHolder.pushToApp(hotKeyModel.getAppName(), hotKeyModel);

        //推送etcd删除
        iConfigCenter.delete(keyPath(hotKeyModel, key));
    }

    /**
     * 生成或返回该key的滑窗
     */
    private SlidingWindow checkWindow(HotKeyModel hotKeyModel, String key) {
        //取该key的滑窗
        SlidingWindow slidingWindow = (SlidingWindow) cache.getIfPresent(key);
        if (slidingWindow == null) {
            //是个新key，获取它的规则
            KeyRateRule keyRateRule = KeyRuleHolder.getRuleByAppAndKey(hotKeyModel);
            slidingWindow = new SlidingWindow(keyRateRule.getDuration(), keyRateRule.getCount());
        }
        return slidingWindow;
    }

    private String key(HotKeyModel hotKeyModel) {
        return Joiner.on("-").join(hotKeyModel.getAppName(), hotKeyModel.getKeyType(), hotKeyModel.getKey());
    }

    private String keyPath(HotKeyModel hotKeyModel, String key) {
        return ConfigConstant.hotKeyPath + hotKeyModel.getAppName() + "/" + key;
    }
}
