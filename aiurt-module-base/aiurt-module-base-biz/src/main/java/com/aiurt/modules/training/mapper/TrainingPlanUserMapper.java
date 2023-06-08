package com.aiurt.modules.training.mapper;

import com.aiurt.modules.training.entity.TrainingPlanUser;
import com.aiurt.modules.training.param.PlanUserParam;
import com.aiurt.modules.training.vo.PlanUserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 培训计划对象
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */

public interface TrainingPlanUserMapper extends BaseMapper<TrainingPlanUser> {
	/**
	 *培训人员列表
	 * @param page 分页参数
	 * @param planId 培训计划id
	 * @return 培训人员列表
	 */
	IPage<TrainingPlanUser> listByPlanId(@Param("page")Page<TrainingPlanUser> page,@Param("planId") Long planId);

	/**
	 *培训计划-分页查询个人列表
	 * @param page 分页参数
	 * @param param 查询参数
	 * @return 培训计划-分页查询个人列表
	 */
	IPage<PlanUserVO> listPlan(@Param("page")Page<PlanUserVO> page, @Param("param") PlanUserParam param);

	/**
	 *培训计划-分页查询个人列表
	 * @param param 查询参数
	 * @return 培训计划-分页查询个人列表
	 */
	List<PlanUserVO> exportListPlan( @Param("param") PlanUserParam param);
}
