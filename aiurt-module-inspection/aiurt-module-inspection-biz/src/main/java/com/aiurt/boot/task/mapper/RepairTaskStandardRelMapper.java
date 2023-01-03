package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.RepairTaskStandardRel;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: repair_task_standard_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface RepairTaskStandardRelMapper extends BaseMapper<RepairTaskStandardRel> {

    /**
     * 查询检修单号
     * @return
     */
    List<String> getRepairTaskCode();

}
