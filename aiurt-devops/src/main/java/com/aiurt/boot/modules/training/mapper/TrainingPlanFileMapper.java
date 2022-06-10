package com.aiurt.boot.modules.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.training.entity.TrainingPlanFile;
import com.aiurt.boot.modules.training.vo.TrainingPlanFileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description: TrainingPlanFileMapper
 * @author: Mr.zhao
 * @date: 2021/11/28 16:49
 */

@Mapper
public interface TrainingPlanFileMapper extends BaseMapper<TrainingPlanFile> {

	IPage<TrainingPlanFileVO> listByPlanId(Page<TrainingPlanFileVO> page, @Param("planId") Long planId);
}
