package com.aiurt.modules.system.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.model.SysUserSysDepartModel;
import com.aiurt.modules.system.vo.SysUserDepVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@EnableDataPerm
public interface SysUserMapper extends BaseMapper<SysUser> {
	/**
	  * 通过用户账号查询用户信息
	 * @param username
	 * @return
	 */
	public SysUser getUserByName(@Param("username") String username);

	/**
	 *  根据部门Id查询用户信息
	 * @param page
	 * @param departId
     * @param username 用户登录账户
	 * @return
	 */
	IPage<SysUser> getUserByDepId(Page page, @Param("departId") String departId, @Param("username") String username);

	/**
	 *  根据用户Ids,查询用户所属部门名称信息
	 * @param userIds
	 * @return
	 */
	List<SysUserDepVo> getDepNamesByUserIds(@Param("userIds")List<String> userIds);

	/**
	 *  根据部门Ids,查询部门下用户信息
	 * @param page
	 * @param departIds
     * @param username 用户登录账户
	 * @return
	 */
	IPage<SysUser> getUserByDepIds(Page page, @Param("departIds") List<String> departIds, @Param("username") String username);

	/**
	 * 根据角色Id查询用户信息
	 * @param page
	 * @param roleId 角色id
     * @param username 用户登录账户
	 * @return
	 */
	IPage<SysUser> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);

	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(@Param("username") String username,@Param("orgCode") String orgCode);

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
	public SysUser getUserByEmail(@Param("email")String email);

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

    /**
     * 批量删除角色与用户关系
     * @Author scott
     * @Date 2019/12/13 16:10
     * @param roleIdArray
     */
	void deleteBathRoleUserRelation(@Param("roleIdArray") String[] roleIdArray);

    /**
     * 批量删除角色与权限关系
     * @Author scott
     * @Date 2019/12/13 16:10
     * @param roleIdArray
     */
	void deleteBathRolePermissionRelation(@Param("roleIdArray") String[] roleIdArray);

	/**
	 * 查询被逻辑删除的用户
     * @param wrapper
     * @return List<SysUser>
	 */
	List<SysUser> selectLogicDeleted(@Param(Constants.WRAPPER) Wrapper<SysUser> wrapper);

	/**
	 * 还原被逻辑删除的用户
     * @param userIds 用户id
     * @param entity
     * @return int
	 */
	int revertLogicDeleted(@Param("userIds") String userIds, @Param("entity") SysUser entity);

	/**
	 * 彻底删除被逻辑删除的用户
     * @param userIds 多个用户id
     * @return int
	 */
	int deleteLogicDeleted(@Param("userIds") String userIds);

    /**
     * 更新空字符串为null【此写法有sql注入风险，禁止随便用】
     * @param fieldName
     * @return int
     */
    @Deprecated
    int updateNullByEmptyString(@Param("fieldName") String fieldName);

	/**
	 *  根据部门Ids,查询部门下用户信息
	 * @param departIds
     * @param username 用户账户名称
	 * @return
	 */
	List<SysUser> queryByDepIds(@Param("departIds")List<String> departIds,@Param("username") String username);

	/**
	 *根据用户姓名模糊查询用户账号
	 * @param realName
	 * @return
	 */
	List<String> getUserListByName(String realName);

	/**
	 * 查询机构下的子机构id
	 * @param code
	 * @return
	 */
	List<String> queryByOrgCode(@Param("code") String code);

	/**
	 * 根据机构Ids查询用户信息
	 * @param page
	 * @param orgId
	 * @param phone
	 * @param realname
	 * @param username
	 * @param status
	 * @param name
	 * @return
	 */
	IPage<SysUser> queryByorgIds(Page<SysUser> page, @Param("orgId") List<String> orgId, @Param("phone") String phone, @Param("realname") String realname, @Param("username") String username, @Param("status") Integer status, String name,@Param("roleCode") String roleCode);
	/**
	 * 翻译部门名字
	 * @param asList
	 * @return
	 */
	List<String> getDepartIds(@Param("asList")List<String> asList);


	/**
	 * 根据角色查询用户
	 * @param role
	 * @return
	 */
    List<SysUser> querySysUserForWorkTicket(@Param("list") List<String> role);

	/**
	 * 根据用户名或者用户账号查询用户信息
	 * @param userNameList
	 * @return
	 */
    List<SysUser> queryUserListByName(@Param("usernameList") List<String> userNameList);

	/**
	 * 根据用户姓名查询用户账号
	 * @param realName
	 * @return
	 */
	String getUserName(@Param("realName") String realName);

	/**
	 * 根据用户姓名查询用户账号
	 * @param realName
	 * @return
	 */
	List<String> getUserLikeName(@Param("realName") String realName);

	/**
	 * 查询 角色Id
	 * @param list
	 * @return
	 */
    List<String> getSysRole(@Param("list") List<String>list);
	/**
	 * 根据用户姓名,工号查询用户信息
	 * @param realName
	 * @param workNo
	 * @return
	 */
	List<LoginUser> getUserByRealName(@Param("realName")String realName, @Param("workNo")String workNo);

	/**
	 * 根据部门，角色编码查询人员账号
	 * @param orgCode
	 * @param roleCode
	 * @return
	 */
    List<String> getUserNameByOrgCodeAndRoleCode(@Param("orgCode") List<String> orgCode,@Param("roleCode") List<String> roleCode);

	/**
	 * 根据用户的部门权限编码和角色编码获取用户账号
	 *
	 * @param orgCodes
	 * @param roleCodes
	 * @return
	 */
	List<String> getUserNameByDeptAuthCodeAndRoleCode(@Param("orgCodes") List<String> orgCodes, @Param("roleCodes") List<String> roleCodes);

	/**
	 * 根据数据库名称和表名验证，该数据库中是存在这张表
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	String selectTableName(@Param("dbName") String dbName,@Param("tableName") String tableName);

	/**
	 * 根据部门codes,获取用户信息
	 * @param orgCodes
	 * @return
	 */
    List<LoginUser> getUserByCodes(@Param("orgCodes")List<String> orgCodes);

	@MapKey("userId")
	List<Map<String,String>> getRealNameMap(@Param("orgId")String orgId);

	/**
	 * 根据部门，角色编码查询人员姓名
	 * @param orgCode
	 * @param roleCode
	 * @return
	 */
	List<String> getRealNameByOrgCodeAndRoleCode(@Param("orgCode") List<String> orgCode,@Param("roleCode") List<String> roleCode);

	/**
	 * 获取用户工龄以及最大值和最小值
	 *
	 * @param orgCode
	 * @return
	 */
	List<LoginUser> getSeniorityNumber(@Param("orgCode") String orgCode);

	/**
	 * 根据用户id列表，获取用户的角色，多个角色使用英文逗号“,”分隔
	 * @param userIds 用户id列表，当此参数为空时，查询所有用户
	 * @return 返回一个List<Map<String, String>>，Map中是类似{"userId":"xxx","roleName":"xxx"}的形式
	 */
    List<Map<String, String>> getRoleNamesByUserIds(@Param("userIds") List<String> userIds);
}
