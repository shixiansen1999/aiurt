package com.aiurt.modules.system.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.system.entity.SysDepart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.common.system.vo.SysDepartModel;

import java.util.List;

/**
 * <p>
 * 部门 Mapper 接口
 * <p>
 *
 * @Author: Steve
 * @Since：   2019-01-22
 */
@EnableDataPerm
public interface SysDepartMapper extends BaseMapper<SysDepart> {

	/**
	 * 根据用户ID查询部门集合
     * @param userId 用户id
     * @return List<SysDepart>
	 */
	public List<SysDepart> queryUserDeparts(@Param("userId") String userId);

	/**
	 * 根据用户名查询部门
	 *
	 * @param username
	 * @return
	 */
	public List<SysDepart> queryDepartsByUsername(@Param("username") String username);

    /**
     * 通过部门编码获取部门id
     * @param orgCode 部门编码
     * @return String
     */
	@Select("select id from sys_depart where org_code=#{orgCode}")
	public String queryDepartIdByOrgCode(@Param("orgCode") String orgCode);

	/**
	 * 通过部门编码获取部门名称
	 * @param orgCode 部门编码
	 * @return String
	 */
	@Select("select depart_name from sys_depart where org_code=#{orgCode}")
	public String queryDepartNameByOrgCode(@Param("orgCode") String orgCode);

    /**
     * 通过部门id 查询部门id,父id
     * @param departId 部门id
     * @return
     */
	@Select("select id,parent_id from sys_depart where id=#{departId}")
	public SysDepart getParentDepartId(@Param("departId") String departId);

	/**
	 *  根据部门Id查询,当前和下级所有部门IDS
	 * @param departId
	 * @return
	 */
	List<String> getSubDepIdsByDepId(@Param("departId") String departId);

	/**
	 * 根据部门编码获取部门下所有IDS
	 * @param orgCodes
	 * @return
	 */
	List<String> getSubDepIdsByOrgCodes(@org.apache.ibatis.annotations.Param("orgCodes") String[] orgCodes);

    /**
     * 根据parent_id查询下级部门
     * @param parentId 父id
     * @return List<SysDepart>
     */
    List<SysDepart> queryTreeListByPid(@Param("parentId") String parentId);
	/**
	 * 根据id下级部门数量
	 * @param parentId
	 * @return
	 */
	@Select("SELECT count(*) FROM sys_depart where del_flag ='0' AND parent_id = #{parentId,jdbcType=VARCHAR}")
    Integer queryCountByPid(@Param("parentId")String parentId);
	/**
	 * 根据OrgCod查询所属公司信息
	 * @param orgCode
	 * @return
	 */
	SysDepart queryCompByOrgCode(@Param("orgCode")String orgCode);
	/**
	 * 根据id下级部门
	 * @param parentId
	 * @return
	 */
	@Select("SELECT * FROM sys_depart where del_flag ='0' AND parent_id = #{parentId,jdbcType=VARCHAR}")
	List<SysDepart> queryDeptByPid(@Param("parentId")String parentId);

	/**
	 * 通过部门编码获取部门
	 * @param orgCode 部门编码
	 * @return String
	 */
	@Select("select * from sys_depart where org_code=#{orgCode}")
	public SysDepart queryDepartByOrgCode(@Param("orgCode") String orgCode);
	/**
	 * 通过管理负责人id,获取部门信息
	 * @param userId
	 * @return List
	 */
	@Select("select * from sys_depart where manager_id=#{userId}")
	List<SysDepartModel> getUserDepart(@Param("userId")String userId);

	/**
	 * 通过id，查询改id的子级
	 * @param orgCode
	 * @param workLogOrgCategory 实施配置里面组织机构是班组的编码
	 * @return
	 */
	List<SysDepartModel> getUserOrgCategory(@Param("orgCode")String orgCode,
											@Param("workLogOrgCategory") String workLogOrgCategory);

	/**
	 * 通过指定的一组父级部门 ID，递归查询所有子部门信息。
	 *
	 * @param ids 一组父级部门 ID
	 * @return 包含所有子部门信息的列表
	 */
    List<SysDepart> selectRecursiveChildrenByIds(@Param("ids") List<String> ids);
}
