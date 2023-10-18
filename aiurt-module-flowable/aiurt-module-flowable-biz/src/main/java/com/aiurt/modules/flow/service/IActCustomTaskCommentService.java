package com.aiurt.modules.flow.service;


import com.aiurt.modules.flow.dto.FlowTaskCommentDTO;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @Description: act_custom_task_comment
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
public interface IActCustomTaskCommentService extends IService<ActCustomTaskComment> {
    /**
     * 查询指定流程实例Id下的所有审批任务的批注。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果集。
     */
    List<ActCustomTaskComment> getFlowTaskCommentList(String processInstanceId);

    /**
     *
     * 查询可见的数据
     * @param processInstanceId
     * @return
     */
    List<ActCustomTaskComment> listFlowTaskCommentVisible(String processInstanceId);

    /**
     * 转换ActCustomTaskComment实体对象转成FlowTaskCommentDTO。
     *
     * @param actCustomTaskComments Model实体对象列表。
     * @return Domain域对象列表。
     */
    List<FlowTaskCommentDTO> convertToCustomTaskCommentList(List<ActCustomTaskComment> actCustomTaskComments);
    /**
     * 查询与指定流程任务Id集合关联的所有审批任务的批注。
     *
     * @param taskIdSet 流程任务Id集合。
     * @return 查询结果集。
     */
    List<ActCustomTaskComment> getFlowTaskCommentListByTaskIds(Set<String> taskIdSet);
    /**
     * 获取指定流程实例和任务定义标识的最后一条审批任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskDefinitionKey 任务定义标识。
     * @return 查询结果。
     */
    ActCustomTaskComment getLatestFlowTaskComment(String processInstanceId, String taskDefinitionKey);
}
