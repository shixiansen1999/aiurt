package com.aiurt.modules.manage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.mapper.ActCustomVersionMapper;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 版本管理
 * @Author: aiurt
 * @Date:   2022-07-15
 * @Version: V1.0
 */
@Slf4j
@Service
public class ActCustomVersionServiceImpl extends ServiceImpl<ActCustomVersionMapper, ActCustomVersion> implements IActCustomVersionService {

    @Lazy
    @Autowired
    private FlowElementUtil flowElementUtil;

    /**
     * 流程版本管理-挂起
     * @param actCustomVersion
     */
    @Override
    public void suspendFlowProcess(ActCustomVersion actCustomVersion) {
        // 参数校验
        ActCustomVersion customVersion = vaildEntity(actCustomVersion);

        if (actCustomVersion.getStatus() == 0) {
            throw new AiurtBootException("当前流程发布版本已处于挂起状态！");
        }

        customVersion.setStatus(0);

        // 更新状态
        updateById(customVersion);

        flowElementUtil.suspendProcessDefinition(customVersion.getProcessDefinitionId());
    }

    /**
     * 激活
     * @param actCustomVersion
     */
    @Override
    public void activeFlowProcess(ActCustomVersion actCustomVersion) {
        // 参数校验
        ActCustomVersion customVersion = vaildEntity(actCustomVersion);

        if (actCustomVersion.getStatus() == 1) {
            throw new AiurtBootException("当前流程发布版本已处于激活状态！");
        }

        customVersion.setStatus(1);

        // 更新状态
        updateById(customVersion);

        // 激活
        flowElementUtil.activateProcessDefinition(customVersion.getProcessDefinitionId());
    }

    /**
     * 参数校验
     * @param actCustomVersion
     * @return
     */
    private ActCustomVersion vaildEntity(ActCustomVersion actCustomVersion) {
        String id = actCustomVersion.getId();
        if (StrUtil.isBlank(id)) {
            throw new AiurtBootException("请求参数有误, 请刷新重试!");
        }
        ActCustomVersion customVersion = baseMapper.selectById(id);

        if (Objects.isNull(customVersion)) {
            throw new AiurtBootException("当前流程发布版本并不存在，请刷新后重试！");
        }

        return customVersion;
    }
}
