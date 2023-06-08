package com.aiurt.modules.training.service;

import com.aiurt.modules.training.entity.TrainingPlanUser;
import com.aiurt.modules.training.param.PlanUserParam;
import com.aiurt.modules.training.vo.PlanUserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 培训计划对象
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ITrainingPlanUserService extends IService<TrainingPlanUser> {

	/**
	 * 培训人员列表-通过培训计划查询
	 * @param page 分页参数
	 * @param planId 培训计划id
	 * @return 培训人员列表
	 */
	IPage<TrainingPlanUser> listByPlanId(Page<TrainingPlanUser> page, Long planId);

	/**
	 *培训计划-分页查询个人列表
	 * @param page 页码
	 * @param param 查询参数
	 * @return 培训计划-分页查询个人列表
	 */
	IPage<PlanUserVO> listPlan(Page<PlanUserVO> page,PlanUserParam param);


	/**
	 * 培训人员-导出列表
	 * @param param 查询参数
	 * @return  培训人员-导出列表
	 */
	ModelAndView exportListPlan(PlanUserParam param);

}
