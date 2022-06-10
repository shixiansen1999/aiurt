package com.aiurt.boot.modules.training.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.training.entity.TrainingPlanUser;
import com.aiurt.boot.modules.training.param.PlanUserParam;
import com.aiurt.boot.modules.training.vo.PlanUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 培训计划对象
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ITrainingPlanUserService extends IService<TrainingPlanUser> {


	IPage<TrainingPlanUser> listByPlanId(Integer pageNo, Integer pageSize, Long planId);

	IPage<PlanUserVO> listPlan(HttpServletRequest req, PlanUserParam param, Integer pageNo, Integer pageSize);
}
