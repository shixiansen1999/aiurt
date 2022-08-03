package com.aiurt.modules.flow.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.flow.dto.FlowTaskCommentDTO;
import com.aiurt.modules.flow.entity.ActCustomTaskComment;
import com.aiurt.modules.flow.mapper.ActCustomTaskCommentMapper;
import com.aiurt.modules.flow.service.IActCustomTaskCommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description: act_custom_task_comment
 * @Author: aiurt
 * @Date: 2022-07-26
 * @Version: V1.0
 */
@Service
public class ActCustomTaskCommentServiceImpl extends ServiceImpl<ActCustomTaskCommentMapper, ActCustomTaskComment> implements IActCustomTaskCommentService {

    /**
     * 查询指定流程实例Id下的所有审批任务的批注。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果集。
     */
    @Override
    public List<ActCustomTaskComment> getFlowTaskCommentList(String processInstanceId) {
        LambdaQueryWrapper<ActCustomTaskComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomTaskComment::getProcessInstanceId, processInstanceId);
        queryWrapper.orderByAsc(ActCustomTaskComment::getId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 转换ActCustomTaskComment实体对象转成FlowTaskCommentDTO。
     *
     * @param actCustomTaskComments Model实体对象列表。
     * @return Domain域对象列表。
     */
    @Override
    public List<FlowTaskCommentDTO> convertToCustomTaskCommentList(List<ActCustomTaskComment> actCustomTaskComments) {

        if (CollUtil.isEmpty(actCustomTaskComments)) {
            return new ArrayList<>();
        }

        List<FlowTaskCommentDTO> result = new ArrayList<>(actCustomTaskComments.size());
        for (ActCustomTaskComment actCustomTaskComment : actCustomTaskComments) {
            FlowTaskCommentDTO flowTaskCommentDTO = new FlowTaskCommentDTO();
            flowTaskCommentDTO.setId(actCustomTaskComment.getId());
            flowTaskCommentDTO.setProcessInstanceId(actCustomTaskComment.getProcessInstanceId());
            flowTaskCommentDTO.setTaskId(actCustomTaskComment.getTaskId());
            flowTaskCommentDTO.setTaskKey(actCustomTaskComment.getTaskKey());
            flowTaskCommentDTO.setTaskName(actCustomTaskComment.getTaskName());
            flowTaskCommentDTO.setApprovalType(actCustomTaskComment.getApprovalType());
            flowTaskCommentDTO.setComment(actCustomTaskComment.getComment());
            flowTaskCommentDTO.setDelegateAssginee(actCustomTaskComment.getDelegateAssignee());
            flowTaskCommentDTO.setCustomBusinessData(actCustomTaskComment.getCustomBusinessData());
            flowTaskCommentDTO.setCreateBy(actCustomTaskComment.getCreateBy());
            flowTaskCommentDTO.setCreateRealname(actCustomTaskComment.getCreateRealname());
            flowTaskCommentDTO.setCreateTime(actCustomTaskComment.getCreateTime());
            result.add(flowTaskCommentDTO);
        }
        return result;
    }

    /**
     * 查询与指定流程任务Id集合关联的所有审批任务的批注。
     *
     * @param taskIdSet 流程任务Id集合。
     * @return 查询结果集。
     */
    @Override
    public List<ActCustomTaskComment> getFlowTaskCommentListByTaskIds(Set<String> taskIdSet) {
        if (CollUtil.isEmpty(taskIdSet)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ActCustomTaskComment> queryWrapper =
                new LambdaQueryWrapper<ActCustomTaskComment>().in(ActCustomTaskComment::getTaskId, taskIdSet);
        queryWrapper.orderByDesc(ActCustomTaskComment::getId);
        return baseMapper.selectList(queryWrapper);
    }
}
