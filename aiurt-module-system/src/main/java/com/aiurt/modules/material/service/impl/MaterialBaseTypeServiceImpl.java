package com.aiurt.modules.material.service.impl;

import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class MaterialBaseTypeServiceImpl extends ServiceImpl<MaterialBaseTypeMapper, MaterialBaseType> implements IMaterialBaseTypeService {

    @Override
    public List<MaterialBaseType> treeList(List<MaterialBaseType> materialBaseTypeList, String id) {
        return getTreeRes(materialBaseTypeList, id);
    }

    List<MaterialBaseType> getTreeRes(List<MaterialBaseType> materialBaseTypeList,String pid){
        List<MaterialBaseType> childList = materialBaseTypeList.stream().filter(materialBaseType -> pid.equals(materialBaseType.getPid())).collect(Collectors.toList());
        if(childList != null && childList.size()>0){
            for (MaterialBaseType materialBaseType : childList) {
                materialBaseType.setMaterialBaseTypeList(getTreeRes(materialBaseTypeList,materialBaseType.getId().toString()));
            }
        }
        return childList;
    }
}
