package com.aiurt.boot.modules.webHome.task;

import com.aiurt.boot.modules.webHome.utils.UserListenerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: UserListenRedisTask
 * @author: Mr.zhao
 * @date: 2021/12/3 18:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserListenRedisTask {

	private final UserListenerUtils userListenerUtils;

	/**
	 * 每30分钟删除redis中无用的计数参数
	 */
	@Scheduled(cron = "0 */30 * * * ? ")
	public void delTokenRedis() {
		userListenerUtils.delToken();
	}

}
