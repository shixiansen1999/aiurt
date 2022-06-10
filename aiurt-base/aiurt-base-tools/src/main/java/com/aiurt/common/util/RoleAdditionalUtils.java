package com.aiurt.common.util;


import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 获取用户id所对应的 部门权限与系统权限
 *
 * @author Mr.zhao
 * @date 2021/12/31 16:00
 */
@Component
public class RoleAdditionalUtils {

	@Resource
	private RedisUtil redisUtil;


	private final static String STR_SPACE = "''";

	/**
	 * 根据用户id获取部门ids,String形式
	 *
	 * @param userId 用户id
	 * @return {@code String}
	 */
	public String getStrDepartIdsByUserId(String userId) {
		String departmentIds = redisUtil.getStr(CommonConstant.PREFIX_USER_DEPARTMENT_IDS + userId);
		return DataValidateUtils.strToDbin(departmentIds);
	}

	/**
	 * 根据用户id获取系统code集合,String形式
	 *
	 * @param userId 用户id
	 * @return {@code String}
	 */
	public String getStrSystemCodesByUserId(String userId) {
		String systemDoces = redisUtil.getStr(CommonConstant.PREFIX_USER_SYSTEM_CODES + userId);
		return DataValidateUtils.strToDbin(systemDoces);
	}


	/**
	 * 根据用户id获取部门ids,List形式
	 *
	 * @param userId 用户id
	 * @return {@code String}
	 */
	public List<String> getListDepartIdsByUserId(String userId) {
		String departmentIds = redisUtil.getStr(CommonConstant.PREFIX_USER_DEPARTMENT_IDS + userId);
		if (StrUtil.isNotBlank(departmentIds) && !STR_SPACE.equals(departmentIds)) {
			return Arrays.asList(departmentIds.split(","));
		}
		return null;
	}

	/**
	 * 根据用户id获取系统code集合,List形式
	 *
	 * @param userId 用户id
	 * @return {@code String}
	 */
	public List<String> getListSystemCodesByUserId(String userId) {
		String systemDoces = redisUtil.getStr(CommonConstant.PREFIX_USER_SYSTEM_CODES + userId);
		if (StrUtil.isNotBlank(systemDoces) && !STR_SPACE.equals(systemDoces)) {
			return Arrays.asList(systemDoces.split(","));
		}
		return null;
	}

}
