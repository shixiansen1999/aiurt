package com.aiurt.boot.modules.system.service;

import java.util.List;
import java.util.Map;

import com.swsc.copsms.modules.system.entity.SysUserRole;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @Author swsc
 * @since 2018-12-21
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

	/**
	 * 查询所有的用户角色信息
	 * @return
	 */
	Map<String,String> queryUserRole();


	/**
	 * 根据用户id查询用户对应的角色
	 */
	List<String> getUserRole(String id);
}
