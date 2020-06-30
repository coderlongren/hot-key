package com.jd.platform.hotkey.dashboard.util;

import com.alibaba.fastjson.JSON;
import com.jd.platform.hotkey.dashboard.common.domain.Constant;
import com.jd.platform.hotkey.dashboard.common.domain.vo.HotKeyLineChartVo;
import com.jd.platform.hotkey.dashboard.model.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
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
//		log.info("按照rule分组以后的listMap--> {}", JSON.toJSONString(listMap));
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
//				log.info("start--> {},  tmp---> {} ", start, tmp);
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
		if(Constant.VERSION != 1){
		   return list.stream().collect(Collectors.groupingBy(Statistics::getRule));
		}
		return  list.stream().collect(Collectors.groupingBy(Statistics::getKeyName));
	}

	/**
	 * 分组
	 * @param list list
	 * @return map
	 */
	private static Map<Integer, List<Statistics>> listGroupByTime(List<Statistics> list, boolean isMinute){
		if(isMinute){
			return  list.stream().collect(Collectors.groupingBy(Statistics::getMinutes));
		}
		return list.stream().collect(Collectors.groupingBy(Statistics::getHours));
	}


	/**
	 * 处理数据
	 * @param st 开始时间
	 * @param et 结束时间
	 * @param list 数据
	 * @param isMinute 类型
	 * @return vo
	 */
	public static HotKeyLineChartVo processData(LocalDateTime st, LocalDateTime et, List<Statistics> list, boolean isMinute){
		Set<String> set = new TreeSet<>();
		Duration duration = Duration.between(st,et);
		long passTime = isMinute ? duration.toMinutes() : duration.toHours();
		Map<Integer, Integer> timeCountMap =  new TreeMap<>();
		String pattern = isMinute ? DateUtil.PATTERN_MINUS : DateUtil.PATTERN_HOUR;
		for (int i = 0; i < passTime; i++) {
			int time = DateUtil.reviseTime(st, i, isMinute ? 1:2);
			set.add(DateUtil.formatTime(time, pattern));
			timeCountMap.put(time,null);
		}
		Map<String, List<Statistics>> ruleStatsMap = listGroup(list);
		Map<String, List<Integer>> ruleDataMap = new HashMap<>(ruleStatsMap.size());
		ruleStatsMap.forEach((rule,statistics)->{
			String app = statistics.get(0).getApp();
			rule = app + "-" + rule;
			Map<Integer, List<Statistics>> timeStatsMap = listGroupByTime(statistics, isMinute);
			timeCountMap.forEach((k,v)->{
				if(timeStatsMap.get(k) == null){
					timeCountMap.put(k,0);
				}else{
					timeCountMap.put(k,timeStatsMap.get(k).get(0).getCount());
				}
			});
			ruleDataMap.put(rule, new ArrayList<>(timeCountMap.values()));

		});
		HotKeyLineChartVo vo = new HotKeyLineChartVo();
		vo.setxAxis2(set);
		vo.setSeries2(ruleDataMap);
		return vo;
	}


	public static void main(String[] args) {
		String str =" [{\"app\":\"app1\",\"bizType\":5,\"count\":9,\"createTime\":1593434160000,\"days\":200629,\"hours\":20062912,\"id\":76996,\"keyName\":\"rule1\",\"minutes\":2006291236,\"rule\":\"rule1\",\"uuid\":\"76996\"},{\"app\":\"app1\",\"bizType\":5,\"count\":184,\"createTime\":1593434220000,\"days\":200629,\"hours\":20062912,\"id\":76997,\"keyName\":\"rule1\",\"minutes\":2006291237,\"rule\":\"rule1\",\"uuid\":\"76997\"},{\"app\":\"app1\",\"bizType\":5,\"count\":238,\"createTime\":1593434280000,\"days\":200629,\"hours\":20062912,\"id\":76998,\"keyName\":\"rule1\",\"minutes\":2006291238,\"rule\":\"rule1\",\"uuid\":\"76998\"},{\"app\":\"app1\",\"bizType\":5,\"count\":139,\"createTime\":1593434340000,\"days\":200629,\"hours\":20062912,\"id\":76999,\"keyName\":\"rule1\",\"minutes\":2006291239,\"rule\":\"rule1\",\"uuid\":\"76999\"},{\"app\":\"app1\",\"bizType\":5,\"count\":15,\"createTime\":1593434400000,\"days\":200629,\"hours\":20062912,\"id\":77000,\"keyName\":\"rule1\",\"minutes\":2006291240,\"rule\":\"rule1\",\"uuid\":\"77000\"},{\"app\":\"app1\",\"bizType\":5,\"count\":211,\"createTime\":1593434460000,\"days\":200629,\"hours\":20062912,\"id\":77001,\"keyName\":\"rule1\",\"minutes\":2006291241,\"rule\":\"rule1\",\"uuid\":\"77001\"},{\"app\":\"app1\",\"bizType\":5,\"count\":142,\"createTime\":1593434520000,\"days\":200629,\"hours\":20062912,\"id\":77002,\"keyName\":\"rule1\",\"minutes\":2006291242,\"rule\":\"rule1\",\"uuid\":\"77002\"},{\"app\":\"app1\",\"bizType\":5,\"count\":247,\"createTime\":1593434580000,\"days\":200629,\"hours\":20062912,\"id\":77003,\"keyName\":\"rule1\",\"minutes\":2006291243,\"rule\":\"rule1\",\"uuid\":\"77003\"},{\"app\":\"app1\",\"bizType\":5,\"count\":208,\"createTime\":1593434640000,\"days\":200629,\"hours\":20062912,\"id\":77004,\"keyName\":\"rule1\",\"minutes\":2006291244,\"rule\":\"rule1\",\"uuid\":\"77004\"},{\"app\":\"app1\",\"bizType\":5,\"count\":146,\"createTime\":1593434700000,\"days\":200629,\"hours\":20062912,\"id\":77005,\"keyName\":\"rule1\",\"minutes\":2006291245,\"rule\":\"rule1\",\"uuid\":\"77005\"},{\"app\":\"app1\",\"bizType\":5,\"count\":7,\"createTime\":1593434760000,\"days\":200629,\"hours\":20062912,\"id\":77006,\"keyName\":\"rule1\",\"minutes\":2006291246,\"rule\":\"rule1\",\"uuid\":\"77006\"},{\"app\":\"app1\",\"bizType\":5,\"count\":207,\"createTime\":1593434820000,\"days\":200629,\"hours\":20062912,\"id\":77007,\"keyName\":\"rule1\",\"minutes\":2006291247,\"rule\":\"rule1\",\"uuid\":\"77007\"},{\"app\":\"app1\",\"bizType\":5,\"count\":231,\"createTime\":1593434880000,\"days\":200629,\"hours\":20062912,\"id\":77008,\"keyName\":\"rule1\",\"minutes\":2006291248,\"rule\":\"rule1\",\"uuid\":\"77008\"},{\"app\":\"app1\",\"bizType\":5,\"count\":94,\"createTime\":1593434940000,\"days\":200629,\"hours\":20062912,\"id\":77009,\"keyName\":\"rule1\",\"minutes\":2006291249,\"rule\":\"rule1\",\"uuid\":\"77009\"},{\"app\":\"app1\",\"bizType\":5,\"count\":21,\"createTime\":1593435000000,\"days\":200629,\"hours\":20062912,\"id\":77010,\"keyName\":\"rule1\",\"minutes\":2006291250,\"rule\":\"rule1\",\"uuid\":\"77010\"},{\"app\":\"app1\",\"bizType\":5,\"count\":33,\"createTime\":1593435060000,\"days\":200629,\"hours\":20062912,\"id\":77011,\"keyName\":\"rule1\",\"minutes\":2006291251,\"rule\":\"rule1\",\"uuid\":\"77011\"},{\"app\":\"app1\",\"bizType\":5,\"count\":107,\"createTime\":1593435120000,\"days\":200629,\"hours\":20062912,\"id\":77012,\"keyName\":\"rule1\",\"minutes\":2006291252,\"rule\":\"rule1\",\"uuid\":\"77012\"},{\"app\":\"app1\",\"bizType\":5,\"count\":247,\"createTime\":1593435180000,\"days\":200629,\"hours\":20062912,\"id\":77013,\"keyName\":\"rule1\",\"minutes\":2006291253,\"rule\":\"rule1\",\"uuid\":\"77013\"},{\"app\":\"app1\",\"bizType\":5,\"count\":154,\"createTime\":1593435240000,\"days\":200629,\"hours\":20062912,\"id\":77014,\"keyName\":\"rule1\",\"minutes\":2006291254,\"rule\":\"rule1\",\"uuid\":\"77014\"},{\"app\":\"app1\",\"bizType\":5,\"count\":187,\"createTime\":1593435300000,\"days\":200629,\"hours\":20062912,\"id\":77015,\"keyName\":\"rule1\",\"minutes\":2006291255,\"rule\":\"rule1\",\"uuid\":\"77015\"},{\"app\":\"app1\",\"bizType\":5,\"count\":161,\"createTime\":1593435360000,\"days\":200629,\"hours\":20062912,\"id\":77016,\"keyName\":\"rule1\",\"minutes\":2006291256,\"rule\":\"rule1\",\"uuid\":\"77016\"},{\"app\":\"app1\",\"bizType\":5,\"count\":277,\"createTime\":1593435420000,\"days\":200629,\"hours\":20062912,\"id\":77017,\"keyName\":\"rule1\",\"minutes\":2006291257,\"rule\":\"rule1\",\"uuid\":\"77017\"},{\"app\":\"app1\",\"bizType\":5,\"count\":207,\"createTime\":1593435480000,\"days\":200629,\"hours\":20062912,\"id\":77018,\"keyName\":\"rule1\",\"minutes\":2006291258,\"rule\":\"rule1\",\"uuid\":\"77018\"},{\"app\":\"app1\",\"bizType\":5,\"count\":286,\"createTime\":1593435540000,\"days\":200629,\"hours\":20062912,\"id\":77019,\"keyName\":\"rule1\",\"minutes\":2006291259,\"rule\":\"rule1\",\"uuid\":\"77019\"},{\"app\":\"app1\",\"bizType\":5,\"count\":196,\"createTime\":1593435600000,\"days\":200629,\"hours\":20062913,\"id\":77020,\"keyName\":\"rule1\",\"minutes\":2006291300,\"rule\":\"rule1\",\"uuid\":\"77020\"},{\"app\":\"app1\",\"bizType\":5,\"count\":175,\"createTime\":1593435660000,\"days\":200629,\"hours\":20062913,\"id\":77021,\"keyName\":\"rule1\",\"minutes\":2006291301,\"rule\":\"rule1\",\"uuid\":\"77021\"},{\"app\":\"app1\",\"bizType\":5,\"count\":157,\"createTime\":1593435720000,\"days\":200629,\"hours\":20062913,\"id\":77022,\"keyName\":\"rule1\",\"minutes\":2006291302,\"rule\":\"rule1\",\"uuid\":\"77022\"},{\"app\":\"app1\",\"bizType\":5,\"count\":174,\"createTime\":1593435780000,\"days\":200629,\"hours\":20062913,\"id\":77023,\"keyName\":\"rule1\",\"minutes\":2006291303,\"rule\":\"rule1\",\"uuid\":\"77023\"},{\"app\":\"app1\",\"bizType\":5,\"count\":141,\"createTime\":1593435840000,\"days\":200629,\"hours\":20062913,\"id\":77024,\"keyName\":\"rule1\",\"minutes\":2006291304,\"rule\":\"rule1\",\"uuid\":\"77024\"},{\"app\":\"app1\",\"bizType\":5,\"count\":274,\"createTime\":1593435900000,\"days\":200629,\"hours\":20062913,\"id\":77025,\"keyName\":\"rule1\",\"minutes\":2006291305,\"rule\":\"rule1\",\"uuid\":\"77025\"},{\"app\":\"app1\",\"bizType\":5,\"count\":161,\"createTime\":1593435960000,\"days\":200629,\"hours\":20062913,\"id\":77026,\"keyName\":\"rule1\",\"minutes\":2006291306,\"rule\":\"rule1\",\"uuid\":\"77026\"}]";
		List<Statistics> list = JSON.parseArray(str, Statistics.class);
		LocalDateTime st = LocalDateTime.now().minusMinutes(31);
		LocalDateTime et = LocalDateTime.now();
		processData(st,  et, list, true);
	}
}
