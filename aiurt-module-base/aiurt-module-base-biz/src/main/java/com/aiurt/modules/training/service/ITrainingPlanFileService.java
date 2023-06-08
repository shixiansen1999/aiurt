package com.aiurt.modules.training.service;

import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.aiurt.modules.training.vo.TrainingPlanFileVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: TrainingPlanFileService
 * @author: Mr.zhao
 * @date: 2021/11/28 16:49
 */

public interface ITrainingPlanFileService extends IService<TrainingPlanFile> {
	
	/**
	 * 通过培训计划id查询列表
	 *
	 * @param page  分页参数
	 * @param planId   计划id
	 * @return {@code IPage<TrainingPlanFile>}
	 */
	IPage<TrainingPlanFileVO> listByPlanId(Page<TrainingPlanFileVO> page, Long planId);

	/**
	 * 编辑-文件查询
	 * @param page 分页参数
	 * @param sysFile 查询
	 * @return
	 */
	Page<SysFileManageVO> getFilePageList(Page<SysFileManageVO> page, SysFileWebParam sysFile);
}
