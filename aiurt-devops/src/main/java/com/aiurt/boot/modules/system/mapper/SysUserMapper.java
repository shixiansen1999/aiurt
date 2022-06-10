package com.aiurt.boot.modules.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.statistical.vo.StaffDataVo;
import com.aiurt.boot.modules.statistical.vo.UserScheduleVo;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.model.SysUserSysDepartModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
    List<SysUser> findUserListByUserId(@Param("userId") String userId);

    @Select("SELECT t1.school_code,t2.school_name as schoolName FROM `sys_user` t1 left join base_school t2 on t1.school_code = t2.school_code where t1.id = #{userId}")
    SysUser getOtherInfo(@Param("userId") String userId);
    @Select("select MAX(username) from sys_user where username like CONCAT(#{pinyin},'%') ")
    String getMaxUserName(@Param("pinyin") String pinyin);

	@Select("select su.* from sys_user su inner join sys_user_role sur on su.id = sur.user_id inner join sys_role sr on sur.role_id = sr.id\n" +
			"where sr.role_code = #{roleCode} and su.del_flag = 0 and su.org_id = #{orgId};")
	List<SysUser> selectUserByOrgIdAndRoleCode(@Param("orgId") String orgId, @Param("roleCode") String roleCode);

	@Select("select DISTINCT(team_id) from cs_station where line_id=#{lineId} ")
	List<String> getBanzuListByLine(@Param("lineId") Integer lineId);
	Integer getTotalNum(Map map);
	@Select("select a.* from sys_user a LEFT JOIN sys_user_role b on a.id=b.user_id LEFT JOIN sys_role c on b.role_id=c.id where c.role_code='banzhang' and a.del_flag=0 and a.org_id=#{orgId} ")
    List<SysUser> getBanZhangByBanZu(@Param("orgId") String orgId);
	@Select("select id as userId ,realname as userName from sys_user where del_flag=0 and org_id=#{orgId} ")
	List<UserScheduleVo> getUserByBanZu(@Param("orgId") String orgId);
	List<StaffDataVo> getStaffData(Map map);
	//1查询巡检数
	@Select("select COUNT(id) from t_patrol_task where del_flag=0 and `status`=1 and  staff_ids like CONCAT('%',#{staffId},'%') ")
	Integer getPatrolTaskNumByStaffId(@Param("staffId") String staffId);
	//2查询检修数
	@Select("select COUNT(id) from repair_task where del_flag=0 and `status`=4 and  staff_ids like CONCAT('%',#{staffId},'%') ")
	Integer getRepairTaskNumByStaffId(@Param("staffId") String staffId);
	//3查询故障数
	@Select("select COUNT(id) from fault_repair_record where del_flag=0 and `solve_status`=1 and  appoint_user_id =#{staffId} ")
	Integer getFaultNumByStaffId(@Param("staffId") String staffId);

	//根据用户id查询用户角色code
    @Select("select role_code from sys_user a inner join sys_user_role b on a.id = b.user_id inner join sys_role c on b.role_id = c.id where a.id = #{id}")
    List<String> getRoleCodeById(@Param("id") String id);

    //根据时间和排班查询当班用户
    List<SysUser> selectUserByTimeAndItemAndOrgId(@Param("date")String date,@Param("itemName")String itemName,@Param("orgId")String orgId);

	@Select("select id from sys_user where username =#{username}")
	String getUserIdByUsername(String username);

	@Select("select * from sys_user WHERE del_flag='0' AND org_code LIKE concat(#{orgCode},'%') order by convert(realname using gbk) ASC ")
    List<SysUser> getUsersByOrgCode(String orgCode);

    List<SysUser> getSysUsersByLineCodeAndOrgId(String lineCode, String orgId);
}
