package com.aiurt.modules.modeler.service.impl;

import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.mapper.ActCustomTaskExtMapper;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description: 流程定义节点属性
 * @Author: aiurt
 * @Date: 2022-08-02
 * @Version: V1.0
 */
@Service
public class ActCustomTaskExtServiceImpl extends ServiceImpl<ActCustomTaskExtMapper, ActCustomTaskExt> implements IActCustomTaskExtService {

    /**
     * 查询指定的流程任务扩展对象。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param taskId              流程引擎的任务唯一标识。
     * @return 查询结果。
     */
    @Override
    public ActCustomTaskExt getByProcessDefinitionIdAndTaskId(String processDefinitionId, String taskId) {
        ActCustomTaskExt filter = new ActCustomTaskExt();
        filter.setProcessDefinitionId(processDefinitionId);
        filter.setTaskId(taskId);
        return baseMapper.selectOne(new QueryWrapper<>(filter));
    }
}
