package com.aiurt.boot.task.mapper;


import com.aiurt.boot.task.dto.RepairTaskPeerNameDTO;
import com.aiurt.boot.task.entity.RepairTaskPeerRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: repair_task_peer_rel
 * @Author: aiurt
 * @Date:   2022-06-30
 * @Version: V1.0
 */
public interface RepairTaskPeerRelMapper extends BaseMapper<RepairTaskPeerRel> {

    /**
     * 获取检修任务ID和同行人名称列表
     *
     * @param repairTaskIds 检修任务ID列表
     * @return 检修任务ID和同行人名称列表的DTO对象
     */
    List<RepairTaskPeerNameDTO> selectTaskIdWithPeerNames(List<String> repairTaskIds);

}
