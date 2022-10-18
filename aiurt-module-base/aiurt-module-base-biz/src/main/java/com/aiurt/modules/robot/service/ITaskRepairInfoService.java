package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.dto.TaskRepairInfoDTO;
import com.aiurt.modules.robot.entity.TaskRepairInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: task_repair_info
 * @Author: jeecg-boot
 * @Date: 2022-10-08
 * @Version: V1.0
 */
public interface ITaskRepairInfoService extends IService<TaskRepairInfo> {

    /**
     * 巡检记录-生成维修单
     *
     * @param taskRepairInfoDTO
     */
    void add(TaskRepairInfoDTO taskRepairInfoDTO);

    /**
     * 巡检记录-查看报修单
     * @param taskId
     * @return
     */
    List<TaskRepairInfo> queryByTaskId(String taskId);
}
