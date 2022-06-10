package com.aiurt.boot.modules.training.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.training.entity.TrainingPlanUser;
import com.aiurt.boot.modules.training.mapper.TrainingPlanUserMapper;
import com.aiurt.boot.modules.training.param.PlanUserParam;
import com.aiurt.boot.modules.training.service.ITrainingPlanUserService;
import com.aiurt.boot.modules.training.vo.PlanUserVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: ITrainingPlanUserServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/28 17:52
 */
@Service
public class ITrainingPlanUserServiceImpl extends ServiceImpl<TrainingPlanUserMapper, TrainingPlanUser> implements ITrainingPlanUserService {


	@Override
	public IPage<TrainingPlanUser> listByPlanId(Integer pageNo, Integer pageSize, Long planId) {

		return this.baseMapper.listByPlanId(new Page<>(pageNo, pageSize), planId);
	}

	@Override
	public IPage<PlanUserVO> listPlan(HttpServletRequest req, PlanUserParam param, Integer pageNo, Integer pageSize) {

		return this.baseMapper.listPlan(new Page<PlanUserVO>(pageNo, pageSize), param);
	}
}
