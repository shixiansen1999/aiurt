package com.aiurt.modules.online.businessdata.service.impl;

import com.aiurt.modules.online.businessdata.entity.ActCustomBusinessData;
import com.aiurt.modules.online.businessdata.mapper.ActCustomBusinessDataMapper;
import com.aiurt.modules.online.businessdata.service.IActCustomBusinessDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程中间业务数据
 * @Author: aiurt
 * @Date:   2022-10-27
 * @Version: V1.0
 */
@Service
public class ActCustomBusinessDataServiceImpl extends ServiceImpl<ActCustomBusinessDataMapper, ActCustomBusinessData> implements IActCustomBusinessDataService {

    /**
     * 更新实例id 查询业务数据， 如果该节点存在了该业务数据，则返回该节点的任务数据， 如果不存在，则返回该实例最新的一条数据
     * @param processInstanceId
     * @return
     */
    @Override
    public ActCustomBusinessData queryByProcessInstanceId(String processInstanceId, String taskId) {
        LambdaQueryWrapper<ActCustomBusinessData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ActCustomBusinessData::getProcessInstanceId, processInstanceId).eq(ActCustomBusinessData::getTaksId, taskId);

        boolean exists = baseMapper.exists(lambdaQueryWrapper);

        // 存在
        if (exists) {
            lambdaQueryWrapper.last("limit 1").orderByDesc(ActCustomBusinessData::getCreateTime);
            ActCustomBusinessData actCustomBusinessData = baseMapper.selectOne(lambdaQueryWrapper);
            return actCustomBusinessData;
        }

        lambdaQueryWrapper.clear();

        lambdaQueryWrapper.eq(ActCustomBusinessData::getProcessInstanceId, processInstanceId).last("limit 1")
                .orderByDesc(ActCustomBusinessData::getCreateTime);
        ActCustomBusinessData actCustomBusinessData = baseMapper.selectOne(lambdaQueryWrapper);
        return actCustomBusinessData;
    }
}
