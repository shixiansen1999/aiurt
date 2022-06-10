package com.aiurt.boot.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.schedule.entity.ScheduleRecord;
import com.aiurt.boot.modules.statistical.vo.DepartDataVo;
import com.aiurt.boot.modules.system.entity.SysDepart;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门 Mapper 接口
 * <p>
 *
 * @Author: Steve
 * @Since：   2019-01-22
 */
@Repository
public interface SysDepartMapper extends BaseMapper<SysDepart> {

	/**
	 * 根据用户ID查询部门集合
	 */
	public List<SysDepart> queryUserDeparts(@Param("userId") String userId);

	/**
	 * 根据用户名查询部门
	 *
	 * @param username
	 * @return
	 */
	public List<SysDepart> queryDepartsByUsername(@Param("username") String username);

	@Select("select id from sys_depart where org_code=#{orgCode}")
	public String queryDepartIdByOrgCode(@Param("orgCode") String orgCode);

	@Select("select id,parent_id from sys_depart where id=#{departId}")
	public SysDepart getParentDepartId(@Param("departId") String departId);

	/**
	 * 查询根部门
	 * @return
	 */
	@Select("SELECT depart_name,org_code FROM sys_depart where org_category = '3' and org_type = '2' ")
    List<SysDepart> queryRootDepar();

	/**
	 * 查询所有正在使用的部门
	 * @return
	 */
	@Select("select org_code from sys_depart where del_flag = #{delFlag}")
    List<String> selectDepartsIsUsed(@Param("delFlag") Integer delFlag);

	@Select("select DISTINCT(team_id) from cs_station where line_id=#{lineId} ")
	List<String> getBanzuListByLine(@org.apache.ibatis.annotations.Param("lineId") Integer lineId);
	List<DepartDataVo> getDepartData(Map map);

	//大屏统计
	@Select("select * from schedule_record where user_id=#{userId} and date=#{today} and del_flag=0 limit 0,1")
	ScheduleRecord getScheduleRecord(@Param("userId") String userId,@Param("today") String today);

	/**
	 * 根据线路code查询班组
	 * @param lineCode
	 * @return
	 */
    List<SysDepart> getSysDepartByLineCode(@Param("lineCode")String lineCode);
}
