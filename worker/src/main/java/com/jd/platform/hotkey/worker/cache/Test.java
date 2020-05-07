package com.jd.platform.hotkey.worker.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-04
 */
@Component
public class Test {
    @Resource(name = "hotKeyCache")
    private Cache<String, Object> hotCache;

    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("a", "b");
        System.out.println(map);
    }

//    @PostConstruct
//    public void aa() throws InterruptedException {
//        Executor executor = Executors.newCachedThreadPool();
//        Cache<String, Object> cache = Caffeine.newBuilder()
//                .executor(executor)
//                .initialCapacity(1024)//初始大小
//                .maximumSize(5000000)//最大数量
//                .expireAfterWrite(5, TimeUnit.MINUTES)//过期时间
//                .softValues()
//                .build();
//        long i = 0;
//        while (true) {
//            cache.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
////            hotCache.put("i" + i, i);
//            i++;
//        }
//    }
}
