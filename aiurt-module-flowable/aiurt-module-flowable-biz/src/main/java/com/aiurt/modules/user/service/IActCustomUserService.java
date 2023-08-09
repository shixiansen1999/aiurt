package com.aiurt.modules.user.service;

import com.aiurt.modules.user.entity.ActCustomUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 流程办理人与抄送人
 * @Author: aiurt
 * @Date: 2023-07-25
 * @Version: V1.0
 */
public interface IActCustomUserService extends IService<ActCustomUser> {


    /**
     * 根据给定的流程定义ID、任务ID和类型，获取用户名称列表。
     *
     * @param processDefinitionId 流程定义ID
     * @param taskId              任务ID
     * @param type                类型
     * @return 用户账号列表
     */
    List<String> getUserNamesByProcessAndTask(String processDefinitionId, String taskId, String type);

    /**
     * 根据任务信息获取 ActCustomUser 对象
     *
     * @param processDefinitionId 流程定义 ID
     * @param taskId              任务 ID
     * @param type                类型
     * @return 符合条件的 ActCustomUser 对象，如果不存在则返回 null
     */
    ActCustomUser getActCustomUserByTaskInfo(String processDefinitionId, String taskId, String type);
}
