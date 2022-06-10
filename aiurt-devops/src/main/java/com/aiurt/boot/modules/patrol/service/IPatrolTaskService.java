package com.aiurt.boot.modules.patrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.patrol.entity.PatrolTask;
import com.swsc.copsms.modules.patrol.param.IgnoreTaskParam;
import com.swsc.copsms.modules.patrol.param.PatrolPoolParam;
import com.swsc.copsms.modules.patrol.param.TaskAddParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 巡检人员任务
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface IPatrolTaskService extends IService<PatrolTask> {

	/**
	 * 查询巡检列表
	 * @param req
	 * @param pageNo
	 * @param pageSize
	 * @param param
	 * @return
	 */
	Result<?> pageList(HttpServletRequest req, Integer pageNo, Integer pageSize, PatrolPoolParam param);


	/**
	 * 手动添加任务
	 *
	 * @param req   请求
	 * @param param 参数
	 * @return {@link Result}<{@link ?}>
	 */
	Result<?> manuallyAddTasks(HttpServletRequest req, TaskAddParam param);

	/**
	 * 漏检任务处理
	 *
	 * @param req   请求
	 * @param param 参数
	 */
	Result<?> ignoreTasks(HttpServletRequest req, IgnoreTaskParam param);

	/**
	 * 根据巡检表id查询详情
	 * @param req
	 * @param id
	 * @param code
	 * @return
	 */
	Result<?> detail(HttpServletRequest req, Long id, String code);
}
