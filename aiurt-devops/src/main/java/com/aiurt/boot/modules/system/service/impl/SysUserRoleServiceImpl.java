package com.aiurt.boot.modules.system.service.impl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.entity.SysUserRole;
import com.aiurt.boot.modules.system.entity.SysRole;
import com.aiurt.boot.modules.system.mapper.SysRoleMapper;
import com.aiurt.boot.modules.system.service.ISysRoleService;
import com.aiurt.boot.modules.system.service.ISysUserRoleService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.boot.modules.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @Author swsc
 * @since 2018-12-21
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

	@Autowired
	private ISysUserService userService;
	@Autowired
	private ISysRoleService roleService;
	@Autowired
	private SysRoleMapper sysRoleMapper;

	/**
	 * 查询所有用户对应的角色信息
	 */
	@Override
	public Map<String,String> queryUserRole() {
		List<SysUserRole> uRoleList = this.list();
		List<SysUser> userList = userService.list();
		List<SysRole> roleList = roleService.list();
		Map<String,String> map = new IdentityHashMap<>();
		String userId = "";
		String roleId = "";
		String roleName = "";
		if(uRoleList != null && uRoleList.size() > 0) {
			for(SysUserRole uRole : uRoleList) {
				roleId = uRole.getRoleId();
				for(SysUser user : userList) {
					userId = user.getId();
					if(uRole.getUserId().equals(userId)) {
						roleName = this.searchByRoleId(roleList,roleId);
						map.put(userId, roleName);
					}
				}
			}
			return map;
		}
		return map;
	}

	@Override
	public List<String> getUserRole(String id) {
		List<String> roles=new ArrayList<>();
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("user_id", id);
		List<SysUserRole> roleList = this.baseMapper.selectList(wrapper);
		if (roleList != null && roleList.size() > 0) {
			for (SysUserRole sysUserRole : roleList) {
				SysRole role = sysRoleMapper.selectById(sysUserRole.getRoleId());
				roles.add(role.getRoleCode());
			}
		}
		return roles;
	}

	/**
	 * queryUserRole调用的方法
	 * @param roleList
	 * @param roleId
	 * @return
	 */
	private String searchByRoleId(List<SysRole> roleList, String roleId) {
		while(true) {
			for(SysRole role : roleList) {
				if(roleId.equals(role.getId())) {
					return role.getRoleName();
				}
			}
		}
	}

}
