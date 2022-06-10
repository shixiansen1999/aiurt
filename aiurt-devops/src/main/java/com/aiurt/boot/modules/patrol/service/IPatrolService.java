package com.aiurt.boot.modules.patrol.service;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.patrol.entity.Patrol;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.patrol.param.PatrolPageParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 巡检标准
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IPatrolService extends IService<Patrol> {

	Result<?> pageList(PatrolPageParam param, Integer pageNo, Integer pageSize, HttpServletRequest req);
}
