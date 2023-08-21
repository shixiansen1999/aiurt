package com.aiurt.modules.state.service.impl;


import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.state.entity.ActCustomState;
import com.aiurt.modules.state.mapper.ActCustomStateMapper;
import com.aiurt.modules.state.service.IActCustomStateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: act_custom_state
 * @Author: wgp
 * @Date:   2023-08-15
 * @Version: V1.0
 */
@Service
public class ActCustomStateServiceImpl extends ServiceImpl<ActCustomStateMapper, ActCustomState> implements IActCustomStateService {

    @Override
    public ActCustomState getCustomStateByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<ActCustomState> lam = new LambdaQueryWrapper();
        lam.eq(ActCustomState::getDelFlag, CommonConstant.DEL_FLAG_0);
        lam.eq(ActCustomState::getProcessInstanceId, processInstanceId);
        return baseMapper.selectOne(lam);
    }


}
