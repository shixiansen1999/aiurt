package com.aiurt.boot.modules.training.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.training.entity.TrainingPlanFile;
import com.aiurt.boot.modules.training.mapper.TrainingPlanFileMapper;
import com.aiurt.boot.modules.training.service.ITrainingPlanFileService;
import com.aiurt.boot.modules.training.vo.TrainingPlanFileVO;
import org.springframework.stereotype.Service;
/**
 * @description: TrainingPlanFileServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/28 16:49
 */

@Service
public class TrainingPlanFileServiceImpl extends ServiceImpl<TrainingPlanFileMapper, TrainingPlanFile> implements ITrainingPlanFileService {

	@Override
	public IPage<TrainingPlanFileVO> listByPlanId(Integer pageNo, Integer pageSize, Long planId) {


		IPage<TrainingPlanFileVO> page = this.baseMapper.listByPlanId(new Page<TrainingPlanFileVO>(pageNo,pageSize),planId);


		return page;
	}
}
