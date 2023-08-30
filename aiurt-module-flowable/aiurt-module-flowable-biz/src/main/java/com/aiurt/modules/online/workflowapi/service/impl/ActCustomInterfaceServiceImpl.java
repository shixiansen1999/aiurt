package com.aiurt.modules.online.workflowapi.service.impl;


import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import com.aiurt.modules.online.workflowapi.mapper.ActCustomInterfaceMapper;
import com.aiurt.modules.online.workflowapi.service.IActCustomInterfaceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;



/**
 * @Description: act_custom_interface
 * @Author: wgp
 * @Date:   2023-07-25
 * @Version: V1.0
 */
@Service
public class ActCustomInterfaceServiceImpl extends ServiceImpl<ActCustomInterfaceMapper, ActCustomInterface> implements IActCustomInterfaceService {

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
}
