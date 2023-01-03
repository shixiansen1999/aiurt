package com.aiurt.modules.param.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SysParamAPIImpl implements ISysParamAPI {
    @Autowired
    private SysParamMapper sysParamMapper;

    @Override
    public SysParamModel selectByCode(String code) {
        QueryWrapper<SysParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysParam::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysParam::getCode, code)
                .last("limit 1");
        SysParam sysParam = Optional.of(sysParamMapper.selectOne(wrapper)).orElseGet(SysParam::new);
        SysParamModel paramModel = new SysParamModel();
        BeanUtils.copyProperties(sysParam, paramModel);
        return paramModel;
    }
}
