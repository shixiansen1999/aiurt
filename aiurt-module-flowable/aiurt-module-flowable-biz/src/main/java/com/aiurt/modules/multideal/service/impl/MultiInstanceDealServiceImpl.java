package com.aiurt.modules.multideal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.multideal.dto.MultiDealDTO;
import com.aiurt.modules.multideal.service.IMultiInTaskService;
import com.aiurt.modules.multideal.service.IMultiInstanceDealService;
import com.aiurt.modules.multideal.service.IMultiInstanceUserService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.engine.ProcessEngines;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.aiurt.common.enums.SentinelErrorInfoEnum.FlowException;

/**
 * @author fgw
 */
@Service
@Slf4j
public class MultiInstanceDealServiceImpl implements IMultiInstanceDealService {

    @Autowired
    private IMultiInTaskService multiInTaskService;

    @Autowired
    private IMultiInstanceUserService multiInstanceUserService;

    /**
     * 多实例任务执行
     */
    @Override
    public void multiInstanceDeal(MultiDealDTO multiDealDTO) {
        log.debug("多实例任务执行模块 multiInstanceDeal");
        String taskId = multiDealDTO.getTaskId();
        Task task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().
                taskId(taskId).singleResult();
        if (task == null){
            throw new AiurtBootException(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getCode(),
                    String.format(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getMessage(),taskId));
        }
        // todo 处理当前活动是否多实例（针对串行情况，需要拿出串行人员列表），设置人员列表变量
        // this.dealOneNodeMulti(taskId, null, null, null);
        if (multiInTaskService.areMultiInTask(task)){
            log.info("当前活动是多少实例，且不是多实例的最后一个活动，不设置下一步多实例办理人:{}",taskId);
            return;
        }

        List<MultiDealDTO.NextNodeUserVo> nextNodeUser = multiDealDTO.getNextNodeUser();

        if (CollUtil.isNotEmpty(nextNodeUser)) {
            for (int i = 0; i < nextNodeUser.size(); i++) {
                MultiDealDTO.NextNodeUserVo nextNodeUserVo = nextNodeUser.get(i);
                //处理下一步活动是否多实例，设置人员列表变量
                this.dealOneNodeMulti(taskId,
                        nextNodeUserVo.getNextNodeId(), multiDealDTO.getBusinessData(),nextNodeUserVo.getUser());

            }
        }
    }


    /**
     * 处理单节点用户多实例
     * @param taskId 活动ID
     * @param nodeId 当前活动不能传此参数
     * @param businessData 业务数据 当前活动不能传此参数
     * @param user 用户选择用户 当前活动不能传此参数
     */
    void dealOneNodeMulti(String taskId, String nodeId, Map<String, Object> businessData, List<String> user) {
        Task task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().
                taskId(taskId).singleResult();
        if (task == null){
            throw new AiurtBootException(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getCode(),
                    String.format(AiurtErrorEnum.FLOW_TASK_NOT_FOUND.getMessage(),taskId));
        }

        List<String> userList = null;
        boolean isCurrNode = false;
        // 判断是否为多实例
        Boolean multiInTask = multiInTaskService.isMultiInTask(task);
        if (!multiInTask) {
            return;
        }
        //判断是否是当前节点
        if (StrUtil.isBlank(nodeId)) {
            log.info("当前节点");
            isCurrNode = true;
            nodeId = task.getTaskDefinitionKey();
            userList = multiInstanceUserService.getCurrentUserList(taskId);
        }
        String variableName =  FlowVariableConstant.ASSIGNEE_LIST + nodeId;
        log.info("活动（{}）,节点（{}），是多实例", taskId, nodeId);
        //是否当前活动
        if (isCurrNode){
            log.info("当前活动（{}）,节点（{}），多实例，设置多实例用户（{}）", taskId, nodeId, userList);
            if (CollectionUtils.isEmpty(userList)){
                throw new AiurtBootException(AiurtErrorEnum.MULTI_INSTANCE_USER_NULL.getCode(),
                        AiurtErrorEnum.MULTI_INSTANCE_USER_NULL.getMessage());
            }
            //设置多实例用户
            ProcessEngines.getDefaultProcessEngine().getTaskService().setVariable(taskId, variableName, userList);
            return;
        }
        //下一步节点，获取下一步活动多实例人员列表
        userList = multiInstanceUserService.getNextNodeUserList(nodeId, businessData, user);
        if (CollectionUtils.isEmpty(userList)){
            throw new AiurtBootException(AiurtErrorEnum.MULTI_INSTANCE_USER_NULL.getCode(),
                    AiurtErrorEnum.MULTI_INSTANCE_USER_NULL.getMessage());
        }
        //设置下一步节点多实例用户
        ProcessEngines.getDefaultProcessEngine().getTaskService().setVariable(taskId, variableName, userList);
    }
}
