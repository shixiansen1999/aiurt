package com.aiurt.modules.sparepart.service.impl;


import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: spare_part_apply_material
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Service
public class SparePartApplyMaterialServiceImpl extends ServiceImpl<SparePartApplyMaterialMapper, SparePartApplyMaterial> implements ISparePartApplyMaterialService {
    @Autowired
    private SparePartApplyMaterialMapper sparePartApplyMaterialMapper;
    /**
     * 查询列表
     * @param
     * @param
     * @return
     */
    @Override
    public List<SparePartApplyMaterial> selectList(){
        return sparePartApplyMaterialMapper.readAll();
    }
}
