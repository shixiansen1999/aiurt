package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.dto.SpareApplyMaterialDTO;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.service.ISparePartApplyMaterialService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * @Description: 备件申领物资
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartApplyMaterialServiceImpl extends ServiceImpl<SparePartApplyMaterialMapper, SparePartApplyMaterial> implements ISparePartApplyMaterialService {

    @Resource
    private SparePartApplyMaterialMapper sparePartApplyMaterialMapper;
    @Override
    public IPage<SpareApplyMaterialDTO> queryPageList(IPage<SpareApplyMaterialDTO> page, String applyCode) {
        return sparePartApplyMaterialMapper.queryPageList(page, applyCode);
    }
}
