package com.jd.platform.hotkey.dashboard.util;



public class CommonUtil {

	/**
	 * 获取父级Key
	 * @param key key
	 * @return string
	 */
	public static String parentK(String key){
		if(key.endsWith("/")){
			key = key.substring(0,key.length()-1);
		}
		int index = key.lastIndexOf("/");
		return key.substring(0,index+1);
	}

	/**
	 * 获取AppName
	 * @param k k
	 * @return str
	 */
	public static String appName(String k){
		String[] arr = k.split("/");
		for (int i = 0; i < arr.length ; i++) {
			if(i == 3){
				return arr[i];
			}
		}
		return null;
	}



	public static String keyName(String k){
		int index = k.lastIndexOf("/");
		return k.substring(index+1);
	}



}
