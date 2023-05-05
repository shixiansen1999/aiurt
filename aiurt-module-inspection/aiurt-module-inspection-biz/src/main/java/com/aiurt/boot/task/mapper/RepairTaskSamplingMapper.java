package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.RepairTaskSampNameDTO;
import com.aiurt.boot.task.entity.RepairTaskSampling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface RepairTaskSamplingMapper extends BaseMapper<RepairTaskSampling> {


    /**
     * 获取检修任务ID和抽检人名称列表
     *
     * @param repairTaskIds 检修任务ID列表
     * @return 检修任务ID和抽检人名称列表的DTO对象
     */
    List<RepairTaskSampNameDTO> selectTaskIdWithSampNames(List<String> repairTaskIds);
}
