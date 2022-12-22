package org.jeecg.common.system.api;

import com.aiurt.modules.todo.dto.BpmnTodoDTO;
import com.aiurt.modules.todo.dto.TodoDTO;

/**
 * @Description 待办接口api, 提供其他独立模块调用
 * @Author wgp
 * @Date 2022/12/21
 * @Version V1.0
 */
public interface ISTodoBaseAPI {
    /**
     * 创建流程待办任务
     *
     * @param bpmnTodoDTO
     */
    void createBbmnTodoTask(BpmnTodoDTO bpmnTodoDTO);

    /**
     * 创建普通待办任务
     * @param todoDTO
     */
    void createTodoTask(TodoDTO todoDTO);

    /**
     * 更新待办任务状态
     * @param todoId  待办任务id
     * @param todoType 待办任务状态 （CommonTodoType）
     */
    void updateTodoTaskState(String todoId,String todoType);

    /**
     * 更新流程待办任务
     * @param taskId
     * @param processInstanceId
     * @param username
     * @param todoType
     */
    void updateBpmnTaskState(String taskId, String processInstanceId, String username, String todoType);
}
