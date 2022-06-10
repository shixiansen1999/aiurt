package com.aiurt.boot.modules.patrol.utils;

import com.swsc.copsms.modules.patrol.constant.PatrolConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: SplUtils
 * @author: Mr.zhao
 * @date: 2021/9/18 17:32
 */
public class SplUtils {
	/**
	 * 接收转换字符串
	 *
	 * @param str
	 * @return
	 */
	public static List<Number> splList(String str) {
		List<Number> list = new ArrayList<>();
		str = str.trim();


		if (str.contains(PatrolConstant.SPL)) {
			String[] split = str.split(PatrolConstant.SPL);
			for (String s : split) {
				list.add(Long.parseLong(s));
			}
		} else {
			list.add(Long.parseLong(str));
		}
		return list;


	}

}
