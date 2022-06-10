package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartApplyMaterialMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartApplyMaterialService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2Service;
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
    @Resource
    private IStockLevel2Service stockLevel2Service;

    /**
     * 分页列表查询
     * @param page
     * @param applyCode 申领编号
     * @return
     */
    @Override
    public IPage<SpareApplyMaterialDTO> queryPageList(IPage<SpareApplyMaterialDTO> page, String applyCode) {
        IPage<SpareApplyMaterialDTO> pageList = sparePartApplyMaterialMapper.queryPageList(page, applyCode);
        pageList.getRecords().forEach(e->{
            StockLevel2 one = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                    .eq(StockLevel2.WAREHOUSE_CODE, e.getOutWarehouseCode())
                    .eq(StockLevel2.MATERIAL_CODE, e.getMaterialCode()));
            if(ObjectUtil.isNotEmpty(one)){
                e.setMaterialNum(one.getNum());
            }
        });
        return pageList;
    }
}
