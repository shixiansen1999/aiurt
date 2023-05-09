package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.entity.RepairPoolCodeContent;
import com.aiurt.boot.task.entity.RepairTaskResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: repair_pool_code_content
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairPoolCodeContentMapper extends BaseMapper<RepairPoolCodeContent> {

    /**
     * 获取检查项
     * @param id
     * @return
     */
    List<RepairTaskResult> getRepairTaskResultList(@Param("id") String id);
}
