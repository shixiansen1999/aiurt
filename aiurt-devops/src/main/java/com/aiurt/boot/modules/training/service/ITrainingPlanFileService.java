package com.aiurt.boot.modules.training.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.training.entity.TrainingPlanFile;
import com.aiurt.boot.modules.training.vo.TrainingPlanFileVO;

/**
 * @description: TrainingPlanFileService
 * @author: Mr.zhao
 * @date: 2021/11/28 16:49
 */

public interface ITrainingPlanFileService extends IService<TrainingPlanFile> {

	/**
	 * 通过培训计划id查询列表
	 *
	 * @param pageNo   页面没有
	 * @param pageSize 页面大小
	 * @param planId   计划id
	 * @return {@code IPage<TrainingPlanFile>}
	 */
	IPage<TrainingPlanFileVO> listByPlanId(Integer pageNo, Integer pageSize, Long planId);
}
