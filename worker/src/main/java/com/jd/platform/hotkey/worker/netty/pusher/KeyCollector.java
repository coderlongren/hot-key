package com.jd.platform.hotkey.worker.netty.pusher;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.jd.platform.hotkey.common.tool.KeyRecordKeyTool.key;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-05-26
 */
@Component
@Deprecated
public class KeyCollector {
    private Set<String> keySet0 = new ConcurrentHashSet<>();
    private Set<String> keySet1 = new ConcurrentHashSet<>();
    private AtomicLong atomicLong = new AtomicLong(0);

    public Set<String> lockAndGetResult() {
        //自增后，对应的map就会停止被写入，等待被读取
        atomicLong.addAndGet(1);
        Set<String> set;
        if (atomicLong.get() % 2 == 0) {
            set = new HashSet<>(keySet1);
            keySet1.clear();
        } else {
            set = new HashSet<>(keySet0);
            keySet0.clear();
        }
        return set;
    }

    public void collect(HotKeyModel hotKeyModel) {
        String key = hotKeyModel.getKey();
        if (StrUtil.isEmpty(key)) {
            return;
        }
        if (atomicLong.get() % 2 == 0) {
            keySet0.add(key(hotKeyModel));
        } else {
            keySet1.add(key(hotKeyModel));
        }

    }

}
