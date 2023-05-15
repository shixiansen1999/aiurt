package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.RepairTaskOrgRel;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: repair_task_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface RepairTaskOrgRelMapper extends BaseMapper<RepairTaskOrgRel> {

    /**
     * 批量新增
     * @param list
     * @return
     */
    int batchInsert(@Param("list") List<RepairTaskOrgRel> list);
}
