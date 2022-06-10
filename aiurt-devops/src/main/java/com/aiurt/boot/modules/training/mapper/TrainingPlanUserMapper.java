package com.aiurt.boot.modules.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.training.entity.TrainingPlanUser;
import com.aiurt.boot.modules.training.param.PlanUserParam;
import com.aiurt.boot.modules.training.vo.PlanUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 培训计划对象
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Mapper
public interface TrainingPlanUserMapper extends BaseMapper<TrainingPlanUser> {

	IPage<TrainingPlanUser> listByPlanId(Page<TrainingPlanUser> trainingPlanUserPage,@Param("planId") Long planId);

	IPage<PlanUserVO> listPlan(Page<PlanUserVO> page, @Param("param") PlanUserParam param);
}
