package com.aiurt.boot.modules.system.service;


import java.util.List;

import com.swsc.copsms.modules.system.model.DepartIdModel;
import com.swsc.copsms.modules.system.entity.SysUser;
import com.swsc.copsms.modules.system.entity.SysUserDepart;


import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * SysUserDpeart用户组织机构service
 * </p>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<SysUserDepart> {


	/**
	 * 根据指定用户id查询部门信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);


	/**
	 * 根据部门id查询用户信息
	 * @param depId
	 * @return
	 */
	List<SysUser> queryUserByDepId(String depId);
}
