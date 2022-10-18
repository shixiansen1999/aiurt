package com.aiurt.modules.robot.service;


import com.aiurt.modules.robot.dto.TaskPathInfoDTO;
import com.aiurt.modules.robot.entity.TaskPathInfo;
import com.aiurt.modules.robot.taskdata.wsdl.ControlTaskType;
import com.aiurt.modules.robot.taskdata.wsdl.TaskExcuteData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: task_path_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
public interface ITaskPathInfoService extends IService<TaskPathInfo> {

    /**
     * 任务模板列表分页查询
     *
     * @param page         分页参数
     * @param taskPathInfo 查询条件
     * @return
     */
    IPage<TaskPathInfoDTO> queryPageList(Page<TaskPathInfoDTO> page, TaskPathInfoDTO taskPathInfo);

    /**
     * 同步机器人任务模板
     */
    void synchronizeTaskPathInfo();

    /**
     * 根据任务模板id给机器人发任务
     * @param taskPathId 任务模板id
     * @return 0成功，1失败
     */
    int startTaskByPathId(String taskPathId);

    /**
     * 当前机器人执行的任务
     * @param robotIp 机器人ip
     * @return
     */
    TaskExcuteData getTaskExcuteData(String robotIp);

    /**
     * 机器人任务操作
     * @param robotIp 机器人ip
     * @param controlTaskType 机器人任务操作类型
     * @return
     */
    int robotControlTask(String robotIp, ControlTaskType controlTaskType);
}
