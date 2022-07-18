package com.aiurt.modules.flow.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author fgw
 */
@Slf4j
@Service
public class FlowApiServiceImpl implements FlowApiService {

    @Autowired
    private RuntimeService runtimeService;

    /**
     *
     * @param startBpmnDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance start(StartBpmnDTO startBpmnDTO) {
        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnDTO));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("无法启动流程，请重新登录！");
        }

        //todo 判断是否是动态表单

        // 保存中间业务数据
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(FlowConstant.PROC_INSTANCE_INITIATOR_VAR, loginUser.getUsername());
        variableMap.put(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR, loginUser.getUsername());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(startBpmnDTO.getModelKey());
        // 启动流程
        return processInstance;
    }

    /**
     *
     * @param startBpmnDTO 流程定义Id。
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startAndTakeFirst(StartBpmnDTO startBpmnDTO) {
        log.info("启动流程请求参数：[{}]", JSON.toJSONString(startBpmnDTO));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("无法启动流程，请重新登录！");
        }
        // 判断是否是动态表单

        // 保存中间业务数据

        // 启动流程
        return null;
    }
}
