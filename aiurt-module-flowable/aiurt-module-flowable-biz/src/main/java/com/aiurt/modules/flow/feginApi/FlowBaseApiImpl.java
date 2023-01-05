package com.aiurt.modules.flow.feginApi;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.StartBpmnImportDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.aiurt.modules.online.businessdata.entity.ActCustomBusinessData;
import com.aiurt.modules.online.businessdata.service.IActCustomBusinessDataService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author fgw
 * @Data 2022-12-28
 */
@Slf4j
@Component
public class FlowBaseApiImpl implements FlowBaseApi {

    @Autowired
    private FlowApiService flowApiService;

    @Autowired
    private FlowElementUtil flowElementUtil;

    @Autowired
    private IActCustomModelInfoService modelInfoService;

    @Autowired
    private IActCustomVariableService variableService;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    private TaskService taskService;


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IActCustomBusinessDataService businessDataService;

    /**
     * 查询当前任务的权限信息（页面，按钮权限）
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    @Override
    public TaskInfoDTO viewRuntimeTaskInfo(String processInstanceId, String taskId) {
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        try {
             taskInfoDTO = flowApiService.viewRuntimeTaskInfo(null, processInstanceId, taskId);
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        }
        return taskInfoDTO;
    }

    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param startBpmnDTO 流程定义Id。
     * @return 新启动的流程实例。
     */
    @Override
    public void startAndTakeFirst(StartBpmnDTO startBpmnDTO) {
        flowApiService.startAndTakeFirst(startBpmnDTO);
    }

    /**
     * 启动流程，导入
     *
     * @param startBpmnImportDTO
     */
    @Override
    public void startBpmnWithImport(StartBpmnImportDTO startBpmnImportDTO) {
        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnImportDTO));
        String userName = startBpmnImportDTO.getUserName();
        if (StrUtil.isBlank(userName)) {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (Objects.isNull(loginUser)) {
                throw new AiurtBootException("无法启动流程，请重新登录！");
            }
            userName = loginUser.getUsername();
        }

        String businessKey = startBpmnImportDTO.getBusinessKey();

        String modelKey = startBpmnImportDTO.getModelKey();

        if (StrUtil.isBlank(businessKey) && StrUtil.isBlank(modelKey)) {
            log.error("流程标识或者业务唯一标识为空");
            return;
        }


        // 验证流程定义数据的合法性。
        Result<ProcessDefinition> processDefinitionResult = flowElementUtil.verifyAndGetFlowEntry(modelKey);
        if (!processDefinitionResult.isSuccess()) {
            throw new AiurtBootException(processDefinitionResult.getMessage());
        }

        ProcessDefinition result = processDefinitionResult.getResult();
        if (result.isSuspended()) {
            throw new AiurtBootException("当前流程定义已被挂起，不能启动新流程！");
        }
        // 设置流程变量
        Map<String, Object> busData = startBpmnImportDTO.getBusData();
        Map<String, Object> variableData = new HashMap<>(16);
        variableData.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, userName);
        variableData.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, userName);
        if (Objects.nonNull(busData)) {
            // 流程key
            // 流程模板信息
            LambdaQueryWrapper<ActCustomModelInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ActCustomModelInfo::getModelKey, modelKey).last("limit 1");
            ActCustomModelInfo one = modelInfoService.getOne(queryWrapper);

            List<ActCustomVariable> list = variableService.list(new LambdaQueryWrapper<ActCustomVariable>().eq(ActCustomVariable::getModelId, one.getModelId())
                    .eq(ActCustomVariable::getVariableType, 1).eq(ActCustomVariable::getType, 0));
            list.stream().forEach(variable->{
                String variableName = variable.getVariableName();
                variableData.put(variableName, busData.get(variableName));
            });
        }



        Authentication.setAuthenticatedUserId(userName);
        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(modelKey, Objects.isNull(businessKey)?null:(String) businessKey, variableData);

        // 获取流程启动后的第一个任务。
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();

        // 设置办理人
        taskService.setAssignee(task.getId(), userName);

        // 保存数据
        if (Objects.nonNull(busData)) {
            saveData(task, busData, processInstance.getProcessInstanceId(), task.getId(), processInstance);
        }
    }

    private void saveData(Task task, Map<String, Object> busData, String processInstanceId, String taskId, ProcessInstance processInstance) {
        // 判断是否存在
        boolean exists = businessDataService.getBaseMapper().exists(new LambdaQueryWrapper<ActCustomBusinessData>()
                .eq(ActCustomBusinessData::getTaksId, taskId).eq(ActCustomBusinessData::getProcessInstanceId, processInstanceId));

        if (exists) {
            // mybatis 不支持json 数据更新；Cannot create a JSON value from a string with CHARACTER SET 'binary'.
            LambdaUpdateWrapper<ActCustomBusinessData> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.set(ActCustomBusinessData::getData, JSONObject.toJSONString(busData)).eq(ActCustomBusinessData::getTaksId, taskId)
                    .eq(ActCustomBusinessData::getProcessInstanceId, processInstanceId);
            businessDataService.update(updateWrapper);
        }else {
            // 保存每个节点的业务数据
            ActCustomBusinessData data = ActCustomBusinessData.builder()
                    .taksId(taskId)
                    .processDefinitionKey(processInstance.getProcessDefinitionKey())
                    .processDefinitionId(processInstance.getProcessDefinitionId())
                    .taskDefinitionKey(task.getTaskDefinitionKey())
                    .data(new JSONObject(busData))
                    .taskName(task.getName())
                    .processInstanceId(processInstanceId)
                    .build();
            businessDataService.save(data);
        }
    }
}
