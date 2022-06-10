package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.SparePartApplyMaterialMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartApplyMaterialService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

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
