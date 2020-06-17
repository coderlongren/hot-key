package com.jd.platform.hotkey.dashboard.util;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.common.monitor.DataHandler;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CommonUtil {


	private static Logger log = LoggerFactory.getLogger(CommonUtil.class);


	/**
	 * 获取父级Key
	 * @param key key
	 * @return string
	 */
	public static String parentK(String key) {
		if (key.endsWith("/")) {
			key = key.substring(0, key.length() - 1);
		}
		int index = key.lastIndexOf("/");
		return key.substring(0, index + 1);
	}

	/**
	 * 获取AppName
	 *
	 * @param k k
	 * @return str
	 */
	public static String appName(String k) {
		String[] arr = k.split("/");
		for (int i = 0; i < arr.length; i++) {
			if (i == 3) {
				return arr[i];
			}
		}
		return null;
	}


	public static String keyName(String k) {
		int index = k.lastIndexOf("/");
		return k.substring(index + 1);
	}


	public static String encoder(String text) {
		try {
			return Base64.getEncoder().encodeToString(text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}


	public static String decoder(String text) {
		byte[] bytes = Base64.getDecoder().decode(text);
		try {
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 拼装数据
	 * @param list list-data
	 * @param startTime 开始时间
	 * @param size 格子数
	 * @param type 类型 1分钟 2小时
	 * @return vo
	 */
	public static HotKeyLineChartVo assembleData(List<Statistics> list, LocalDateTime startTime, int size, int type) {
		Set<String> set = new TreeSet<>();
		boolean isHour = type == 1;
		String suffix = isHour ? "60" : "24";
		String pattern = isHour ? DateUtil.PATTERN_MINUS : DateUtil.PATTERN_HOUR;
		Map<String, int[]> map = new HashMap<>(10);
		Map<String, List<Statistics>> listMap = listGroup(list);
		log.info("按照rule分组以后的listMap--> {}", JSON.toJSONString(listMap));
		for (Map.Entry<String, List<Statistics>> m : listMap.entrySet()) {
			int start = DateUtil.reviseTime(startTime, 0, type);
			map.put(m.getKey(), new int[size]);
			int[] data = map.get(m.getKey());
			int tmp = 0;
			for (int i = 0; i < size; i++) {
				if (String.valueOf(start).endsWith(suffix)) {
					LocalDateTime tmpTime = DateUtil.strToLdt((start - 1) + "", pattern);
					start = DateUtil.reviseTime(tmpTime, 1, type);
				}
				log.info("start--> {},  tmp---> {} ", start, tmp);
				set.add(DateUtil.strToLdt(start + "", pattern).toString().replace("T", " "));
				Statistics st = m.getValue().get(tmp);
				int val = isHour ? st.getMinutes() : st.getHours();
				if (start != val) {
					data[i] = 0;
				} else {
					tmp++;
					data[i] = st.getCount();
				}
				start++;
			}
		}
		return new HotKeyLineChartVo(new ArrayList<>(set), map);
	}


	/**
	 * 分组
	 * @param list list
	 * @return map
	 */
	private static Map<String, List<Statistics>> listGroup(List<Statistics> list){
		return  list.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
	}


}
