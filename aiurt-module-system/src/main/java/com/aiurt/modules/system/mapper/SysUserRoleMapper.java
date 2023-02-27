package com.aiurt.modules.system.mapper;

import com.aiurt.modules.system.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.common.system.vo.SysUserRoleModel;

import java.util.List;

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

	/**
	 * 通过用户名称模糊查询username集合
	 * @param realName
	 * @return
	 */
	@Select("select userName from sys_user where realName  like concat( '%' , #{realName}, '%')")
	List<String> getUserNameByRealName(@Param("realName")String realName);

	/**
	 * 通过用户账号获取权限Id集合
	 * @param userId
	 * @return
	 */
	@Select("select role_id from sys_user_role where user_id = #{userId} ")
	List<String> getRoleIds(@Param("userId") String userId);

	/**
	 * 通过用户账号获取权限名称
	 * @param userId
	 * @return
	 */
	@Select("select a.role_name from sys_user_role v  left join sys_role a on v.role_id =a.id where user_id = #{userId} ")
	List<String> getRoleNames(@Param("userId") String userId);

	/**
	 * 根据用户id获取角色名称
	 * @param userId
	 * @return
	 */
	@Select("select role_name from sys_role where id in (select role_id from sys_user_role where user_id = #{userId})")
	List<String> getRoleName(@Param("userId") String userId);

	/**
	 * 根据专业code获得id
	 * @param code
	 * @return
	 */
	@Select("select id from cs_major where del_flag = 0 and major_code = #{code}")
	String getMajorId(@Param("code") String code);
	/**
	 * 根据子系统code获得id
	 * @param code
	 * @param subCode
	 * @return
	 */
	@Select("select id from cs_subsystem where del_flag = 0 and major_code = #{code} and system_code = #{subCode}")
	String getSubsystemId(@Param("code") String code,@Param("subCode") String subCode);

	/**
	 * 获取对应角色Id的所有用户
	 * @param roleId
	 * @return
	 */
	@Select("SELECT sur.*,su.realname as userName,sr.role_code,sr.role_name FROM sys_user_role sur " +
			"JOIN sys_user su ON sur.user_id = su.id AND su.del_flag =0 " +
			"join sys_role sr on sur.role_id = sr.id where sur.role_id = #{roleId} ")
    List<SysUserRoleModel> getUserByRoleId(String roleId);
}
