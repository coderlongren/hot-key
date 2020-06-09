package com.jd.platform.hotkey.worker.keydispatcher;

import cn.hutool.core.date.SystemClock;
import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.tool.Constant;
import com.jd.platform.hotkey.worker.tool.InitConstant;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.LongAdder;
import java.util.zip.CRC32;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-06-09
 */
@Component
public class KeyProducer {
    @Resource
    private Consumer consumer;


    //过期的
    public static final LongAdder expireTotalCount = new LongAdder();

    public void push(HotKeyModel model) {
        if (model == null || model.getKey() == null) {
            return;
        }
        //5秒前的过时消息就不处理了
        if (SystemClock.now() - model.getCreateTime() > InitConstant.timeOut) {
            expireTotalCount.increment();
            return;
        }

        int index = (int) (Math.abs(hash(model.getKey())) % Constant.Default_Threads);
        consumer.get(index).offer(model);
    }

    private long hash(String key) {
        CRC32 crc = new CRC32();
        crc.update(key.getBytes());
        return crc.getValue();
    }

}
