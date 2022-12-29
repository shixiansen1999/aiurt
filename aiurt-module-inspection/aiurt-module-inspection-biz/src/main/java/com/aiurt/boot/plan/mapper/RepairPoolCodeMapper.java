package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.RepairPoolCode;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: repair_pool_code
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */

@EnableDataPerm
public interface RepairPoolCodeMapper extends BaseMapper<RepairPoolCode> {

    /**
     * 查询检修计划关联的检修标准
     * @return
     */
    List<String> getRepairPoolCode();

}
