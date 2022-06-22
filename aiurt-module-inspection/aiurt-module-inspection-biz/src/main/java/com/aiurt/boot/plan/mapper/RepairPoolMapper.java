package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.RepairPool;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface RepairPoolMapper extends BaseMapper<RepairPool> {

    /**
     * 检修计划池列表查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<RepairPool> queryList(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
