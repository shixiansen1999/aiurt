package com.aiurt.boot.modules.patrol.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.param.OneTreeParam;
import com.aiurt.boot.modules.patrol.param.PatrolPoolParam;
import com.aiurt.boot.modules.patrol.param.PatrolTaskDetailParam;
import com.aiurt.boot.modules.patrol.param.TaskAddParam;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.patrol.vo.export.ExportTaskSubmitVO;
import com.aiurt.boot.modules.patrol.vo.statistics.AppStationPatrolStatisticsVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 巡检人员任务
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
public interface IPatrolTaskService extends IService<PatrolTask> {

	/**
	 * 查询巡检列表
	 *
	 * @param param    参数
	 * @return {@code Result<?>}
	 */
	Result<IPage<PatrolTaskVO>> pageList( PatrolPoolParam param);

	/**
	 * app查询巡检列表
	 *
	 * @param req   要求的事情
	 * @param param 参数
	 * @return {@code Result<?>}
	 */
	Result<IPage<PatrolTaskVO>> appPage(HttpServletRequest req, PatrolPoolParam param);

	/**
	 * 手动添加任务
	 *
	 * @param req   请求
	 * @param param 参数
	 * @return {@link Result}<{@link ?}>
	 */
	Result<?> manuallyAddTasks(HttpServletRequest req, TaskAddParam param);

	/**
	 * 根据巡检表id查询详情
	 *
	 * @param req   请求
	 * @param param 参数
	 * @return {@code Result<?>}
	 */
	Result<?> detail(HttpServletRequest req, PatrolTaskDetailParam param);

	/**
	 * 查询导出列表
	 *
	 * @param param 参数
	 * @return {@code List<PatrolTaskVO>}
	 */
	List<PatrolTaskVO> selectExportListVO(PatrolPoolParam param);

	/**
	 * 查询单个项目,树形
	 *
	 * @param req   请求
	 * @param param 参数
	 * @return {@code Result<?>}
	 */
	Result<?> appOneDetail(HttpServletRequest req, OneTreeParam param);

	/**
	 * 获取人员名称与巡检数量
	 *
	 * @param statisticsVO 统计VO
	 * @return {@code Map<String, Integer>}
	 */
	Map<String, Integer> getUserNameMap(StatisticsVO statisticsVO);

	/**
	 * 查询任务导出列表
	 * @param param
	 * @return
	 */
	List<ExportTaskSubmitVO> selectExportTaskList(PatrolPoolParam param);

	/**
	 * 根据patrol_pool_id批量删除
	 * @param poolIdList
	 */
	Result deleteTaskByIds(List<String> poolIdList);

	Integer countCompletedPatrolNumByOrgIdAndTime(String orgId, DateTime startTime, DateTime endTime);

	Integer countIgnoredPatrolNumByOrgIdAndTime(String orgId, DateTime start, DateTime endTime);

	/**
	 * 查询班组下个站点计划数、完成数、漏检数
	 * @return
	 */
	Result<List<AppStationPatrolStatisticsVO>> appStationPatrolStatistics();
}
