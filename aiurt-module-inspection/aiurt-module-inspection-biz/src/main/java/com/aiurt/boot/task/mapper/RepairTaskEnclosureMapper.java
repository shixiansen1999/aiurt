package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.RepairTaskEnclosure;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * @Description: repair_task_enclosure
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairTaskEnclosureMapper extends BaseMapper<RepairTaskEnclosure> {

    String getById(@Param("id") String id);

}
