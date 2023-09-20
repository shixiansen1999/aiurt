package com.aiurt.modules.modeler.service.impl;

import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.mapper.ActCustomModelExtMapper;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程属性扩展表
 * @Author: aiurt
 * @Date:   2023-09-12
 * @Version: V1.0
 */
@Service
public class ActCustomModelExtServiceImpl extends ServiceImpl<ActCustomModelExtMapper, ActCustomModelExt> implements IActCustomModelExtService {

    /**
     * 根据流程定义信息查询流属性
     *
     * @param processDefinitionId
     * @return
     */
    @Override
    public ActCustomModelExt getByProcessDefinitionId(String processDefinitionId) {
        LambdaQueryWrapper<ActCustomModelExt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomModelExt::getProcessDefinitionId, processDefinitionId).last("limit 1");

        ActCustomModelExt modelExt = baseMapper.selectOne(queryWrapper);
        return modelExt;
    }
}
