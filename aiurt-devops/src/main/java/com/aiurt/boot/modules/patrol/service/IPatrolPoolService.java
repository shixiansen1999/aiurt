package com.aiurt.boot.modules.patrol.service;

import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.param.PoolAppointParam;
import com.aiurt.boot.modules.patrol.param.PoolPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 巡检计划池
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IPatrolPoolService extends IService<PatrolPool> {
	/**
	 * 巡检池分页查询
	 *
	 * @param param 参数
	 * @return {@code Result<?>}
	 */
	Result<?> selectPage(PoolPageParam param);

	/**
	 * 指派人员
	 *
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> appoint(HttpServletRequest req, PoolAppointParam param);

	/**
	 * 领取任务
	 *
	 * @param req
	 * @param id
	 * @return
	 */
	Result<?> receive(HttpServletRequest req, Long id);

	/**
	 * 详情
	 * @param req
	 * @param id
	 * @return
	 */
	Result<?> detail(HttpServletRequest req, Long id);

	/**
	 * 重新指派
	 * @param req
	 * @param param
	 * @return
	 */
	Result<?> reAppoint(HttpServletRequest req, PoolAppointParam param);

	/**
	 * 查看指派详情
	 * @param req
	 * @param poolId
	 * @return
	 */
	Result<PatrolTask> appointDetail(HttpServletRequest req, Long poolId);
}
