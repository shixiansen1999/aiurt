package com.aiurt.modules.online.workflowapi.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.mapper.ActCustomModelInfoMapper;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import com.aiurt.modules.online.workflowapi.mapper.ActCustomInterfaceMapper;
import com.aiurt.modules.online.workflowapi.mapper.ActCustomInterfaceModuleMapper;
import com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


/**
 * @Description: act_custom_interface
 * @Author: wgp
 * @Date: 2023-07-25
 * @Version: V1.0
 */
@Service
public class ActCustomInterfaceServiceImpl extends ServiceImpl<ActCustomInterfaceMapper, ActCustomInterface> implements IActCustomInterfaceService {
    @Autowired
    private ActCustomInterfaceModuleMapper actCustomInterfaceModuleMapper;
    @Autowired
    private ActCustomModelInfoMapper actCustomModelInfoMapper;

    @Override
    public boolean isNameExists(String name, String id) {
        LambdaQueryWrapper<ActCustomInterface> lam = new LambdaQueryWrapper<>();
        lam.eq(ActCustomInterface::getName, name);
        if (StrUtil.isNotEmpty(id)) {
            lam.ne(ActCustomInterface::getId, id);
        }
        Long count = baseMapper.selectCount(lam);
        return count > 0;
    }

    @Override
    public boolean removeInterfaceById(String id) {
        if (StrUtil.isEmpty(id)) {
            return false;
        }
        // 被流程关联的接口不能被删除
        long count = actCustomModelInfoMapper.countByCustomInterfaceIds(Arrays.asList(id));
        if (count > 0) {
            throw new AiurtBootException("接口在流程中被使用，无法删除");
        }
        return this.removeById(id);
    }

    @Override
    public void checkAndThrowIfModuleHasAssociatedInterfaces(List<String> moduleIds) throws AiurtBootException {
        if (CollUtil.isEmpty(moduleIds)) {
            return;
        }
        // 查询指定模块下的接口数量
        long cusInterfaceCount = this.count(new LambdaQueryWrapper<ActCustomInterface>()
                .in(ActCustomInterface::getModule, moduleIds)
                .eq(ActCustomInterface::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 如果存在关联接口，则抛出异常
        if (cusInterfaceCount > 0) {
            throw new AiurtBootException("模块下有接口信息，无法删除模块");
        }
    }

    @Override
    public boolean removeInterfaceByIds(List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        // 被流程关联的接口不能被删除
        long count = actCustomModelInfoMapper.countByCustomInterfaceIds(ids);
        if (count > 0) {
            throw new AiurtBootException("接口在流程中被使用，无法删除");
        }
        return this.removeByIds(ids);
    }

}
