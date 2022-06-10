package com.aiurt.boot.modules.webHome.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: UserListenerUtils
 * @author: Mr.zhao
 * @date: 2021/12/3 16:15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserListenerUtils {

	private final RedisTemplate redisTemplate;

	private static final String USER_COUNT_STR = "user_count";

	/**
	 * 获取数量
	 *
	 * @return {@code Integer}
	 */
	public Integer getCount() {
		long time = System.currentTimeMillis();

		List<Object> values = redisTemplate.boundHashOps(USER_COUNT_STR).values();
		if (CollectionUtils.isNotEmpty(values)) {
			long count = values.stream().filter(v -> {
				try {
					long l = Long.parseLong(String.valueOf(v));
					if (time >= l) {
						return true;
					}
				} catch (NumberFormatException ignored) {
				}
				return false;
			}).count();
			return (int) count;
		}

		return 0;
	}

	/**
	 * 增加在线用户id
	 *
	 * @param token 用户token
	 */
	public void setUserToken(String token,Long time) {
		redisTemplate.boundHashOps(USER_COUNT_STR).put(token, time);
	}

	/**
	 * 删除在线用户id
	 *
	 * @param token 用户token
	 */
	public void delUserToken(List<Object> token) {
		if (CollectionUtils.isNotEmpty(token)) {
			redisTemplate.boundHashOps(USER_COUNT_STR).delete(token.toArray());
		}
	}

	/**
	 * 删除无用token
	 */
	public void delToken() {
		long millis = System.currentTimeMillis();
		Map<Object, Object> objMap =  redisTemplate.boundHashOps(USER_COUNT_STR).entries();
		if (CollectionUtils.isNotEmpty(objMap)){
			List<Object> delList = new ArrayList<>();
			Map<String,Long> map = new HashMap<>();
			for (Object obj : objMap.keySet()) {
				if (ObjectUtil.isNotNull(obj)){
					String str = String.valueOf(obj);
					Object longObj = objMap.get(obj);
					if (str!=null){
						long l = Long.parseLong(String.valueOf(longObj));
						if (millis>l){
							delList.add(str);
						}
					}
				}
			}
			delUserToken(delList);
		}

	}


}
