package com.aiurt.modules.system.mapper;

import java.util.List;

import com.aiurt.modules.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 通过用户账号查询角色集合
     * @param username 用户账号名称
     * @return List<String>
     */
	@Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
	List<String> getRoleByUserName(@Param("username") String username);

	/**
     * 通过用户账号查询角色Id集合
     * @param username 用户账号名称
     * @return List<String>
     */
	@Select("select id from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
	List<String> getRoleIdByUserName(@Param("username") String username);

	@Select("select role_id from sys_user_role where user_id = #{userId} ")
	List<String> getRoleIds(@Param("userId") String userId);
	@Select("select a.role_name from sys_user_role v  left join sys_role a on v.role_id =a.id where user_id = #{userId} ")
	List<String> getRoleNames(@Param("userId") String userId);

	/**
	 * 根据用户id获取角色名称
	 * @param userId
	 * @return
	 */
	@Select("select role_name from sys_role where id in (select role_id from sys_user_role where user_id = #{userId})")
	List<String> getRoleName(@Param("userId") String userId);

}
