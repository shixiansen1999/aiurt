package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.RepairTaskUserNameDTO;
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: repair_task_user
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairTaskUserMapper extends BaseMapper<RepairTaskUser> {
    /**
     * 获取检修任务ID和用户名称列表
     *
     * @param repairTaskIds 检修任务id列表
     * @return 检修任务ID和用户名称列表的DTO对象
     */
    List<RepairTaskUserNameDTO> selectTaskIdWithUserNames(@Param("repairTaskIds") List<String> repairTaskIds);

    /**
     * 批量插入
     * @param repairTaskUserList
     * @return
     */
    int batchInsert(List<RepairTaskUser> repairTaskUserList);
}
