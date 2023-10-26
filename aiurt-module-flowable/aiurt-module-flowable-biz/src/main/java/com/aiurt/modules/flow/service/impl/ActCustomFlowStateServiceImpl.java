package com.aiurt.modules.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.flow.entity.ActCustomFlowState;
import com.aiurt.modules.flow.enums.FlowStatesEnum;
import com.aiurt.modules.flow.mapper.ActCustomFlowStateMapper;
import com.aiurt.modules.flow.service.IActCustomFlowStateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程系统状态表
 * @Author: fugaowei
 * @Date:   2023-10-25
 * @Version: V1.0
 */
@Slf4j
@Service
public class ActCustomFlowStateServiceImpl extends ServiceImpl<ActCustomFlowStateMapper, ActCustomFlowState> implements IActCustomFlowStateService {

    /**
     * 更新流程状态
     *
     * @param processInstanceId
     * @param state
     */
    @Override
    public void updateFlowState(String processInstanceId, Integer state) {
        if (StrUtil.isBlank(processInstanceId) || Objects.isNull(state)) {
            if (log.isDebugEnabled()) {
                log.debug("流程实例为空或者状态为空，不修改状态");
            }
            return;
        }

        LambdaQueryWrapper<ActCustomFlowState> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomFlowState::getProcessInstanceId, processInstanceId).last("limit 1");

        ActCustomFlowState actCustomFlowState = baseMapper.selectOne(wrapper);
        if (Objects.isNull(actCustomFlowState)) {
            actCustomFlowState = new ActCustomFlowState();
            actCustomFlowState.setState(state);
            actCustomFlowState.setProcessInstanceId(processInstanceId);
            this.save(actCustomFlowState);
        }else {
            actCustomFlowState.setState(state);
            this.updateById(actCustomFlowState);
        }
    }

    /**
     * 获取流程在状态
     *
     * @param processInstanceIdSet
     * @return
     */
    @Override
    public Map<String, String> flowStateMap(Set<String> processInstanceIdSet) {
        if (CollUtil.isEmpty(processInstanceIdSet)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<ActCustomFlowState> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ActCustomFlowState::getProcessInstanceId, processInstanceIdSet);

        List<ActCustomFlowState> actCustomFlowStates = baseMapper.selectList(wrapper);
        if (CollUtil.isEmpty(actCustomFlowStates)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = actCustomFlowStates.stream().collect(Collectors.toMap(ActCustomFlowState::getProcessInstanceId, actCustomFlowState -> {
            FlowStatesEnum statesEnum = FlowStatesEnum.getByCode(actCustomFlowState.getState());
            if (Objects.nonNull(statesEnum)) {
                return statesEnum.getMessage();
            } else {
                return "";
            }
        }, (t1, t2) -> t1));
        return map;
    }
}
