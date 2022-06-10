package com.aiurt.boot.modules.patrol.utils;

import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 任务编号生成工具类
 *
 * @description: NumberGenerateUtils
 * @author: Mr.zhao
 * @date: 2021/9/17 13:44
 */
@Component
@RequiredArgsConstructor
public class NumberGenerateUtils {

	private final RedisTemplate redisTemplate;

	/**
	 * redis获取编号
	 *
	 * @param before
	 * @return
	 */
	public String getCodeNo(String before) throws RuntimeException {

		if (StringUtils.isBlank(before)) {
			throw new RuntimeException("before不能为空");
		}

		//当前时间的4位
		String time = new SimpleDateFormat("yyMM").format(new Date());
		//组合编号
		String no = before.concat(PatrolConstant.NO_SPL).concat(time);
		//任务编号
		Integer number = 1;

		Object o = redisTemplate.boundHashOps("code").get(no);
		if (o != null) {
			number = Integer.parseInt(o.toString());
			number++;
		}
		redisTemplate.boundHashOps("code").put(no, number);

		//格式化编号
		if (number < 10) {
			//个位数拼两个0
			no = no.concat(PatrolConstant.NO_SPL).concat("00") + number;
		} else if (number < 100) {
			//十位数拼一个0
			no = no.concat(PatrolConstant.NO_SPL).concat("0") + number;
		} else {
			no = no.concat(PatrolConstant.NO_SPL) + number;
		}

		return no;
	}


	/**
	 * redis删除编号信息
	 *
	 * @return
	 */
	public void delCode2Redis() {
		redisTemplate.delete("code");
	}

}
