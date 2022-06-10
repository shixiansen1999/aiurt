package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.MaterialBase;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.MaterialBaseMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IMaterialBaseService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Service
public class MaterialBaseServiceImpl extends ServiceImpl<MaterialBaseMapper, MaterialBase> implements IMaterialBaseService {

    @Resource @Lazy
    private IMaterialBaseService materialBaseService;

    @Override
    public Integer getTypeByMaterialCode(String code) {
        MaterialBase materialBase = materialBaseService.
                getOne(new QueryWrapper<MaterialBase>().eq("code", code), false);
        if(ObjectUtil.isNotEmpty(materialBase)){
            return materialBase.getType();
        }else{
            return null;
        }
    }
}
