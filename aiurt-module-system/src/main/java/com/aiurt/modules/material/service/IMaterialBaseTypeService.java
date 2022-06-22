package com.aiurt.modules.material.service;

import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IMaterialBaseTypeService extends IService<MaterialBaseType> {
    List<MaterialBaseType> treeList(List<MaterialBaseType> materialBaseTypeList, String id);
}
