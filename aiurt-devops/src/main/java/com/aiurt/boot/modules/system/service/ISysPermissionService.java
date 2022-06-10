package com.aiurt.boot.modules.system.service;

import java.util.List;

import com.swsc.copsms.modules.system.model.TreeModel;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.modules.system.entity.SysPermission;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @Author swsc
 * @since 2018-12-21
 */
public interface ISysPermissionService extends IService<SysPermission> {

	public List<TreeModel> queryListByParentId(String parentId);

	/**真实删除*/
	public void deletePermission(String id) throws SwscException;
	/**逻辑删除*/
	public void deletePermissionLogical(String id) throws SwscException;

	public void addPermission(SysPermission sysPermission) throws SwscException;

	public void editPermission(SysPermission sysPermission) throws SwscException;

	public List<SysPermission> queryByUser(String username);

	/**
	 * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
	 *
	 * @param id
	 * @return
	 */
	public void deletePermRuleByPermId(String id);

	/**
	  * 查询出带有特殊符号的菜单地址的集合
	 * @return
	 */
	public List<String> queryPermissionUrlWithStar();
}
