package com.aiurt.boot.modules.patrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.patrol.entity.PatrolPool;
import com.swsc.copsms.modules.patrol.param.PoolAppointParam;
import com.swsc.copsms.modules.patrol.param.PoolPageParam;

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
	 * @param param
	 * @param req
	 * @return
	 */
	Result<?> selectPage(PoolPageParam param, HttpServletRequest req);

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
}
