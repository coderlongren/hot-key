package com.jd.platform.hotkey.worker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-04
 */
@Component
public class Test {
    @Resource(name = "hotKeyCache")
    private Cache<String, Object> hotCache;

//    @PostConstruct
//    public void aa() throws InterruptedException {
//        long i = 0;
//        while (true) {
////            CaffeineCacheHolder.getCache("a").put("i" + i, i);
//            hotCache.put("i" + i, i);
//            i++;
//            if (i % 10000 == 0) {
//                System.out.println(i);
//                System.out.println(new Date());
//            }
//            for (int j = 0; j < 10000; j++) {
//                int h = j;
//            }
//        }
//    }
}
