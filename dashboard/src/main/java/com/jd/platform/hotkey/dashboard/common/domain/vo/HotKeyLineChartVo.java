package com.jd.platform.hotkey.dashboard.common.domain.vo;

import java.util.List;
import java.util.Map;

/**
 * @author liyunfeng31
 */
public class HotKeyLineChartVo {

    private List<String> xAxis;

    private Map<String,int[]> series;

    public HotKeyLineChartVo(List<String> xAxis, Map<String, int[]> series) {
        this.xAxis = xAxis;
        this.series = series;
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public Map<String, int[]> getSeries() {
        return series;
    }
}
