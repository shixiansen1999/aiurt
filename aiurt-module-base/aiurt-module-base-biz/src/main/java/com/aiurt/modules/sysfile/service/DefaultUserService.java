package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.entity.DefaultUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: DefaultUserService
 * @author: Mr.zhao
 * @date: 2021/11/22 14:50
 */

public interface DefaultUserService extends IService<DefaultUser> {


	/**
	 * 获取人员常用列表
	 *
	 * @param userId 用户id
	 * @return {@link List}<{@link DefaultUser}>
	 */
	List<DefaultUser> listDefault(String userId);
}
