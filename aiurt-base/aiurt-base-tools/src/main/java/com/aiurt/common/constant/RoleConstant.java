package com.aiurt.common.constant;

/**
 * 鉴权code  对应表sys_role中role_code字段
 *
 * @description: RoleConstant
 * @author: Mr.zhao
 * @date: 2021/11/27 18:37
 */
public interface RoleConstant {

	/**
	 * 管理员
	 */
	String ADMIN = "admin";
	/**
	 * 班组成员
	 */
	String TEAM_MEMBER = "banzuchengyuan";
	/**
	 * 班组长
	 */
	String TEAM_LEADER = "banzhang";
	/**
	 * 技术员
	 */
	String TECHNICIAN = "jishuyuan";
	/**
	 * 材料员
	 */
	String MATERIAL_CLERK = "cailiaoyuan";
	/**
	 * 主任
	 */
	String DIRECTOR ="zhuren";
}
