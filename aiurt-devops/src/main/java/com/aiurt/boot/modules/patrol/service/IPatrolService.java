package com.aiurt.boot.modules.patrol.service;

import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.param.PatrolPageParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 巡检标准
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IPatrolService extends IService<Patrol> {

	Result<?> pageList(PatrolPageParam param, Integer pageNo, Integer pageSize);

	Result<?> detailStrategy(Long id);
}
