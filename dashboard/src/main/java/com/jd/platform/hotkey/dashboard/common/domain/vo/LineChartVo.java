package com.jd.platform.hotkey.dashboard.common.domain.vo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author liyunfeng31
 */
public class LineChartVo {

    private Set<String> xAxis;

    private Map<String,List<Integer>> series;

    public LineChartVo(Set<String> xAxis, Map<String, List<Integer>> series) {
        this.xAxis = xAxis;
        this.series = series;
    }

    public Set<String> getxAxis() {
        return xAxis;
    }

    public Map<String, List<Integer>> getSeries() {
        return series;
    }
}
