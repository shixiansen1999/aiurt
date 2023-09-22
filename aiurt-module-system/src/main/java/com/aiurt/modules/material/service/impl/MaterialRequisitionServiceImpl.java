package com.aiurt.modules.material.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.constant.MaterialRequisitionConstant;
import com.aiurt.modules.material.dto.MaterialRequisitionDetailInfoDTO;
import com.aiurt.modules.material.dto.MaterialRequisitionInfoDTO;
import com.aiurt.modules.material.entity.MaterialRequisition;
import com.aiurt.modules.material.mapper.MaterialRequisitionMapper;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 领料单service的实现类
 *
 * @author 华宜威
 * @date 2023-09-18 16:40:11
 */
@Service
public class MaterialRequisitionServiceImpl extends ServiceImpl<MaterialRequisitionMapper, MaterialRequisition> implements IMaterialRequisitionService {

    @Autowired
    private MaterialRequisitionMapper materialRequisitionMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public MaterialRequisitionInfoDTO queryByCode(String code, Integer requisitionType) {
        MaterialRequisition materialRequisition = this.getOne(new LambdaQueryWrapper<MaterialRequisition>()
                .eq(MaterialRequisition::getCode, code)
                .eq(MaterialRequisition::getMaterialRequisitionType, requisitionType)
                .eq(MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0), false);
        if (ObjectUtil.isNull(materialRequisition)) {
            throw new AiurtBootException("未找到对应领料单");
        }
        List<DictModel> stockLevel2InfoList = sysBaseAPI.queryTableDictItemsByCode("stock_level2_info", "warehouse_name", "warehouse_code");
        List<DictModel> sparePartStockList = sysBaseAPI.queryTableDictItemsByCode("spare_part_stock_info", "warehouse_name", "warehouse_code");
        Map<String, String> map2 = new HashMap<>(1);
        Map<String, String> map3 = new HashMap<>(1);
        if (CollUtil.isNotEmpty(stockLevel2InfoList)) {
            map2 = stockLevel2InfoList.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        }
        if (CollUtil.isNotEmpty(sparePartStockList)) {
            map3 = sparePartStockList.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        }
        MaterialRequisitionInfoDTO sparePartRequisitionInfoDTO = new MaterialRequisitionInfoDTO();
        BeanUtil.copyProperties(materialRequisition, sparePartRequisitionInfoDTO);
        //翻译仓库名称
        if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(requisitionType)) {
            //维修申领
            sparePartRequisitionInfoDTO.setApplyWarehouseName(map3.get(materialRequisition.getApplyWarehouseCode()));
        }
        if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL2.equals(requisitionType)) {
            //二级库领用
            sparePartRequisitionInfoDTO.setCustodialWarehouseName(map2.get(materialRequisition.getCustodialWarehouseCode()));
        }
        if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(requisitionType)) {
            //三级库领用
            sparePartRequisitionInfoDTO.setApplyWarehouseName(map2.get(materialRequisition.getApplyWarehouseCode()));
            sparePartRequisitionInfoDTO.setCustodialWarehouseName(map3.get(materialRequisition.getCustodialWarehouseCode()));
        }
        return sparePartRequisitionInfoDTO;
    }

    @Override
    public void queryDetailList(Page<MaterialRequisitionDetailInfoDTO> page, String code, Integer requisitionType) {
        materialRequisitionMapper.queryDetailByRequisitionId(page, code, requisitionType);
    }
}
