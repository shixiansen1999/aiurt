package com.aiurt.boot.modules.sysFile.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.common.util.TaskStatusUtil;
import com.aiurt.boot.modules.sysFile.entity.DefaultUser;
import com.aiurt.boot.modules.sysFile.service.DefaultUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: PatrolMission
 * @author: Mr.zhao
 * @date: 2021/11/23 20:29
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefUserMission {

	private final DefaultUserService defaultUserService;

	private final Integer DEFAULT_USER_SIZE = 10;

	/**
	 * 每天删除过多的常用用户
	 */
	@Scheduled(cron = "0 0 2 * * ? ")
	public void delDef() {
		if (!TaskStatusUtil.getTaskStatus()) {
			return;
		}
		List<DefaultUser> list = defaultUserService.list(new LambdaQueryWrapper<DefaultUser>().orderByDesc(DefaultUser::getCreateTime));
		if (CollectionUtils.isNotEmpty(list)) {
			Map<String, List<DefaultUser>> listMap = list.stream().collect(Collectors.groupingBy(DefaultUser::getUserId));
			List<Long> ids = new ArrayList<>();
			//查询是否有超过最大最近用户的
			for (String userId : listMap.keySet()) {
				List<DefaultUser> users = listMap.get(userId);
				if (CollectionUtils.isNotEmpty(users)) {
					if (DEFAULT_USER_SIZE < users.size()) {
						for (int i = DEFAULT_USER_SIZE; i <users.size(); i++) {
							if (users.get(i)!=null && users.get(i).getId()!=null){
								ids.add(users.get(i).getId());
							}
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(ids)) {
				//删除过多的
				defaultUserService.removeByIds(ids);
			}
		}
	}
}
