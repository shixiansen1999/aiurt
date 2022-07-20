package com.aiurt.modules.sysFile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.sysFile.entity.SysFileRole;
import com.aiurt.modules.sysFile.param.SysFileRoleParam;

import java.util.List;

/**
 * @Description: 文档权限表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
public interface ISysFileRoleService extends IService<SysFileRole> {

	/**
	 * 添加文档权限
	 *
	 * @param param 参数
	 * @return boolean
	 */
	boolean addRole(SysFileRoleParam param);

	/**
	 * 修改文档权限
	 *
	 * @param param 参数
	 * @return boolean
	 */
	boolean updateRole(SysFileRoleParam param);


	/**
	 * 删除文档权限
	 *
	 * @param ids    用户id集合
	 * @param typeId id类型
	 * @return boolean
	 */
	boolean delRole(List<String> ids,Long typeId);


	/**
	 * 以用户id查询权限id
	 *
	 * @param userId 用户id
	 * @return {@link List}<{@link Long}>
	 */
	List<Long> queryRoleByUserId(String userId);


	/**
	 * 查询角色的用户id
	 *
	 * @param userId 用户id
	 * @param typeId 类型id
	 * @return {@link List}<{@link Long}>
	 */
	List<Long> queryRoleByUserId(String userId, Long typeId);
}
