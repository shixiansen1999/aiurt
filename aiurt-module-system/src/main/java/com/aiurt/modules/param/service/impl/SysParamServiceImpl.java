package com.aiurt.modules.param.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.param.dto.SysParamDTO;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.mapper.SysParamMapper;
import com.aiurt.modules.param.service.ISysParamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Description: sys_param
 * @Author: aiurt
 * @Date: 2022-12-15
 * @Version: V1.0
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements ISysParamService {

    @Override
    public IPage<SysParam> queryPageList(Page<SysParam> page, SysParamDTO sysParamDTO) {
        QueryWrapper<SysParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysParam::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (ObjectUtil.isNotEmpty(sysParamDTO)) {
            Optional.ofNullable(sysParamDTO.getCode()).ifPresent(code -> wrapper.lambda().like(SysParam::getCode, code));
            Optional.ofNullable(sysParamDTO.getCategory()).ifPresent(category -> wrapper.lambda().eq(SysParam::getCategory, category));
            Optional.ofNullable(sysParamDTO.getValue()).ifPresent(value -> wrapper.lambda().like(SysParam::getValue, value));
            Optional.ofNullable(sysParamDTO.getExplain()).ifPresent(explain -> wrapper.lambda().like(SysParam::getExplain, explain));
        }
        Page<SysParam> paramPage = this.page(page, wrapper);
        return paramPage;
    }

    @Override
    public String add(SysParam sysParam) {
        boolean exists = this.lambdaQuery()
                .eq(SysParam::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysParam::getCode, sysParam.getCode())
                .exists();
        if (exists) {
            throw new AiurtBootException("参数编号已存在！");
        }
        this.save(sysParam);
        return sysParam.getId();
    }

    @Override
    public String edit(SysParam sysParam) {
        boolean exists = this.lambdaQuery()
                .eq(SysParam::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysParam::getCode, sysParam.getCode())
                .exists();
        if (exists) {
            throw new AiurtBootException("参数编号已存在！");
        }
        String id = sysParam.getId();
        SysParam param = this.getById(id);
        if (ObjectUtil.isEmpty(param)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        BeanUtils.copyProperties(sysParam, param);
        this.updateById(param);
        return id;
    }
}
