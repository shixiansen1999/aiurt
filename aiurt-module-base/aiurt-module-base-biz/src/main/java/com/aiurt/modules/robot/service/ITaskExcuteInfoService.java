package com.aiurt.modules.robot.service;


import com.aiurt.modules.robot.dto.TaskPatrolDTO;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import com.aiurt.modules.robot.vo.DeviceInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: task_excute_info
 * @Author: aiurt
 * @Date: 2022-09-29
 * @Version: V1.0
 */
public interface ITaskExcuteInfoService extends IService<TaskExcuteInfo> {
    /**
     * 根据任务id获取机器人完成任务数据信息
     *
     * @param taskId
     */
//    void synchronizeRobotTaskExcuteInfo(String taskId);

    /**
     * 巡检记录分页查询
     *
     * @param page
     * @param taskId
     * @param device
     * @param excuteState
     * @return
     */
    IPage<TaskPatrolDTO> getPatrolListPage(Page<TaskPatrolDTO> page, String taskId, String device, String excuteState);

    /**
     * 根据任务id获取同步巡视任务的巡视记录
     *
     * @param taskId
     * @return
     */
    List<TaskExcuteInfo> getSynchronizeRobotTaskExcuteInfo(String taskId);

    /**
     * 同步巡视任务的巡视记录
     *
     * @param taskExcuteInfos
     */
    void synchronizeRobotTaskExcuteInfo(List<TaskExcuteInfo> taskExcuteInfos);
//    // 需要下载图片的
//    List<TaskExcuteInfo> synchronizeRobotTaskExcuteInfo(List<TaskExcuteInfo> taskExcuteInfos);

    /**
     * 根据taskId查询设备信息
     *
     * @param taskId
     * @return
     */
    List<DeviceInfoVO> getDeviceInfo(String taskId);
}
