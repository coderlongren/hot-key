package com.jd.platform.common.rule;

/**
 * 对key的字符串是否命中的规则，以及该key判断是否热key的频率，如2秒500次
 * @author wuweifeng wrote on 2019-12-12
 * @version 1.0
 */
public class KeyRateRule {
    /**
     * key的前缀，也可以完全和key相同
     */
    private String key;
    /**
     * 是否是前缀，true是前缀
     */
    private boolean prefix;
    /**
     * 间隔时间
     */
    private int duration = 2;
    /**
     * 累计数量
     */
    private int count = 500;
    /**
     * 变热key后，本地、etcd缓存它多久。默认10分钟
     */
    private int continued = 600;

    public KeyRateRule(String key) {
        this.key = key;
    }

    public KeyRateRule(String key, int duration, int count) {
        this.key = key;
        this.duration = duration;
        this.count = count;
    }

    @Override
    public String toString() {
        return "KeyRateRule{" +
                "key='" + key + '\'' +
                ", prefix=" + prefix +
                ", duration=" + duration +
                ", count=" + count +
                ", continued=" + continued +
                '}';
    }

    public int getContinued() {
        return continued;
    }

    public void setContinued(int continued) {
        this.continued = continued;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if (duration < 1) {
            duration = 1;
        }
        this.duration = duration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
