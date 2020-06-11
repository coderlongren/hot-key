package com.jd.platform.sample;

import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wuweifeng wrote on 2020-02-21
 * @version 1.0
 */
@Component
public class Cache {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public String getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //最佳实践：
    //
    //1 判断用户是否是刷子
    //
    //        if (JdHotKeyStore.isHotKey(“pin__” + thePin)) {
    //            //限流他，do your job
    //        }
    //2 判断商品id是否是热点
    //
    //
    //
    //     Object skuInfo = JdHotKeyStore.getValue("skuId__" + skuId);
    //           if(skuInfo == null) {
    //
    //         JdHotKeyStore.smartSet("skuId__" + skuId, theSkuInfo);
    //           } else {
    //
    //                  //使用缓存好的value即可
    //
    //            }
    //
    //   或者这样：
    //
    //
    //
    //        if (JdHotKeyStore.isHotKey(key)) {
    //                           //注意是get，不是getValue。getValue会获取并上报，get是纯粹的本地获取
    //
    //            Object skuInfo = JdHotKeyStore.get("skuId__" + skuId);
    //                          if(skuInfo == null) {
    //
    //                JdHotKeyStore.smartSet("skuId__" + skuId, theSkuInfo);
    //                          } else {
    //
    //                                  //使用缓存好的value即可
    //
    //                          }
    //
    //        }

    public String get(String key) {
        //如果已经缓存过了
        if (JdHotKeyStore.getValue(key) != null) {
            System.out.println("1");
            return JdHotKeyStore.getValue(key).toString();
        } else {
            String value = getFromRedis(key);
            JdHotKeyStore.smartSet(key, value);
            return value;
        }
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void remove(String key) {
        JdHotKeyStore.remove(key);
        //do your job
    }

}
