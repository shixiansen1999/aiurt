package com.aiurt.boot.modules.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.system.model.SysUserSysDepartModel;
import com.swsc.copsms.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author swsc
 * @since 2018-12-20
 */
@Repository
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
	/**
	  * 通过用户账号查询用户信息
	 * @param username
	 * @return
	 */
	public SysUser getUserByName(@Param("username") String username);
	List<SysUser> findUserByAccount(@Param("account") String account);
	public String getRoleCodeByName(@Param("username") String username);

	/**
	 * 根据用户id查询用户信息
	 * @param id
	 * @return
	 */
	 SysUser getUserById(String id);

	/**
	 *  根据部门Id查询用户信息
	 * @param page
	 * @param departId
	 * @return
	 */
	IPage<SysUser> getUserByDepId(Page page, @Param("departId") String departId, @Param("username") String username);

	/**
	 * 根据角色Id查询用户信息
	 * @param page
	 * @param
	 * @return
	 */
	IPage<SysUser> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);

	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(@Param("username") String username, @Param("orgCode") String orgCode);

	/**
	 * 根据手机号查询用户信息
	 * @param phone
	 * @return
	 */
	public SysUser getUserByPhone(@Param("phone") String phone);


	/**
	 * 根据邮箱查询用户信息
	 * @param email
	 * @return
	 */
	public SysUser getUserByEmail(@Param("email") String email);

	/**
	 * 根据 orgCode 查询用户，包括子部门下的用户
	 *
	 * @param page 分页对象, xml中可以从里面进行取值,传递参数 Page 即自动分页,必须放在第一位(你可以继承Page实现自己的分页对象)
	 * @param orgCode
	 * @param userParams 用户查询条件，可为空
	 * @return
	 */
	List<SysUserSysDepartModel> getUserByOrgCode(IPage page, @Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);


    /**
     * 查询 getUserByOrgCode 的Total
     *
     * @param orgCode
     * @param userParams 用户查询条件，可为空
     * @return
     */
    Integer getUserByOrgCodeTotal(@Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);
    List<SysUser> findUserListByUserId(@Param("userId") String userId);

    @Select("SELECT t1.school_code,t2.school_name as schoolName FROM `sys_user` t1 left join base_school t2 on t1.school_code = t2.school_code where t1.id = #{userId}")
    SysUser getOtherInfo(@Param("userId") String userId);
    @Select("select MAX(username) from sys_user where username like CONCAT(#{pinyin},'%') ")
    String getMaxUserName(@Param("pinyin") String pinyin);
}
