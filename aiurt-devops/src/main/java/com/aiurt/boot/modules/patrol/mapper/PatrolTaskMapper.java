package com.aiurt.boot.modules.patrol.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.param.PatrolAppHomeParam;
import com.aiurt.boot.modules.patrol.param.PatrolPoolParam;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.patrol.vo.export.ExportTaskSubmitVO;
import com.aiurt.boot.modules.patrol.vo.statistics.AppStationPatrolStatisticsVO;
import com.aiurt.boot.modules.patrol.vo.statistics.TaskErrorVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsPatrolVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsResultVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Description: 巡检人员任务
 * @Author: Mr.zhao
 * @Date: 2021-09-17
 * @Version: V1.0
 */
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {

	/**
	 * 巡检人员分页查询
	 *
	 * @param page
	 * @param param
	 * @return
	 */
	IPage<PatrolTaskVO> selectPageList(IPage<PatrolTaskVO> page, @Param("param") PatrolPoolParam param);

	/**
	 * app巡检人员分页查询
	 *
	 * @param page
	 * @param param
	 * @return
	 */
	IPage<PatrolTaskVO> selectAppPage(IPage<PatrolTaskVO> page, @Param("param") PatrolPoolParam param);

	/**
	 * app首页查询
	 *
	 * @param param
	 * @return
	 */
	List<PatrolTaskVO> selectAppHome(@Param("param")PatrolAppHomeParam param);

	/**
	 * 漏检任务联查
	 *
	 * @return
	 */
	List<PatrolTask> selectMissed(@Param("executionTime") LocalDateTime executionTime);

	/**
	 * 导出export列表
	 *
	 * @param param 参数
	 * @return {@code List<PatrolTaskVO>}
	 */
	List<PatrolTaskVO> selectExportListVO(@Param("param") PatrolPoolParam param);

	/**
	 * 查询已完结数量
	 * @param homeParam
	 * @return
	 */
	Integer selectAppHomeCount(@Param("param")PatrolAppHomeParam homeParam);

	List<TaskErrorVO> selectErrors(@Param("ids") List<Long> ids);

	/**
	 * 导出巡检任务列表
	 *
	 * @param param 参数
	 * @return {@code List<ExportTaskSubmitVO>}
	 */
	List<ExportTaskSubmitVO> selectExportTaskList(@Param("param") PatrolPoolParam param);

	/**
	 * 查询时间范围内各班组巡检任务数
	 */
	List<StatisticsPatrolVO> getPatrolCountGroupByOrg(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("lineCode")String lineId);

	Integer countIgnoreNumByTimeAndLineIds(@Param("lineIds") List<Integer> lineIds, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	Integer countCompleteNumByTimeAndLineIds(@Param("lineIds") List<Integer> lineIds, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	Integer countExceptionNumByTimeAndLineIds(@Param("lineIds") List<Integer> lineIds, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	IPage<PatrolTaskVO> selectPageList2(IPage<PatrolTaskVO> page, @Param("param") PatrolPoolParam param);

	Integer countPatrolNumByTimeAndLineIds(@Param("lineIds") List<Integer> lineIds, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	/**
	 * 根据班组统计巡检数
	 */
	Integer countPatrolNumByOrgIdAndTime(@Param("orgId") String orgId, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	/**
	 * 根据班组统计检修数
	 */
	Integer countCompletedPatrolNumByOrgIdAndTime(@Param("orgId") String orgId, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	/**
	 * 根据班组统计漏检数
	 */
	Integer countIgnoredPatrolNumByOrgIdAndTime(@Param("orgId") String orgId, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);

	Integer countTwiceIgnoreContentPatrolNum(@Param("orgId") String orgId, @Param("startTime") DateTime startTime, @Param("endTime") DateTime endTime);


	/**
	 * 查询app站点统计数据
	 * @param orgId
	 * @return
	 */
	List<AppStationPatrolStatisticsVO> appStationPatrolStatistics(@Param("startTime")Date startTime, @Param("endTime")Date endTime
			, @Param("orgId")String orgId);


	/**
	 * 获取维修人员统计数量
	 * @param startTime
	 * @param endTime
	 * @param orgId
	 * @param userName
	 * @return
	 */
	List<StatisticsResultVO> getCount(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("orgId")String orgId,@Param("userName")String userName);

}
