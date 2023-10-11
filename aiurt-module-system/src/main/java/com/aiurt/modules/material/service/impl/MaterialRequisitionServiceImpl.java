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
import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.aiurt.modules.material.mapper.MaterialRequisitionDetailMapper;
import com.aiurt.modules.material.mapper.MaterialRequisitionMapper;
import com.aiurt.modules.material.service.IMaterialRequisitionService;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.service.IStockLevel2Service;
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
    private MaterialRequisitionDetailMapper materialRequisitionDetailMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IStockLevel2Service stockLevel2Service;
    @Autowired
    private ISparePartStockService sparePartStockService;

    @Override
    public MaterialRequisitionInfoDTO queryByCode(String code) {
        MaterialRequisition materialRequisition = this.getOne(new LambdaQueryWrapper<MaterialRequisition>()
                .eq(MaterialRequisition::getCode, code)
                .eq(MaterialRequisition::getDelFlag, CommonConstant.DEL_FLAG_0), false);
        if (ObjectUtil.isNull(materialRequisition)) {
            throw new AiurtBootException("未找到对应领料单");
        }
        List<DictModel> stockLevel2InfoList = sysBaseApi.queryTableDictItemsByCode("stock_level2_info", "warehouse_name", "warehouse_code");
        List<DictModel> sparePartStockList = sysBaseApi.queryTableDictItemsByCode("spare_part_stock_info", "warehouse_name", "warehouse_code");
        Map<String, String> map2 = new HashMap<>(1);
        Map<String, String> map3 = new HashMap<>(1);
        if (CollUtil.isNotEmpty(stockLevel2InfoList)) {
            map2 = stockLevel2InfoList.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        }
        if (CollUtil.isNotEmpty(sparePartStockList)) {
            map3 = sparePartStockList.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        }
        MaterialRequisitionInfoDTO sparePartRequisitionInfoDTO = new MaterialRequisitionInfoDTO();
        Integer requisitionType = materialRequisition.getMaterialRequisitionType();
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
    public void queryPageDetail(Page<MaterialRequisitionDetailInfoDTO> page, String code) {
        materialRequisitionMapper.queryPageDetail(page, code);
    }

    @Override
    public MaterialRequisitionInfoDTO getDetailById(String id) {
        MaterialRequisition materialRequisition = this.getById(id);
        Integer requisitionType = materialRequisition.getMaterialRequisitionType();
        // 二级库仓库
        Map<String, String> level2Map = sysBaseApi.queryTableDictItemsByCode("stock_level2_info", "warehouse_name", "warehouse_code")
                .stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 三级库仓库
        Map<String, String> level3Map = sysBaseApi.queryTableDictItemsByCode("spare_part_stock_info", "warehouse_name", "warehouse_code")
                .stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        MaterialRequisitionInfoDTO materialRequisitionInfoDTO = new MaterialRequisitionInfoDTO();
        BeanUtil.copyProperties(materialRequisition, materialRequisitionInfoDTO);
        //翻译仓库名称
        if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(requisitionType)) {
            //维修申领
            materialRequisitionInfoDTO.setApplyWarehouseName(level3Map.get(materialRequisition.getApplyWarehouseCode()));
            materialRequisitionInfoDTO.setLeve2WarehouseName(level2Map.get(materialRequisition.getLeve2WarehouseCode()));
        }else if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL2.equals(requisitionType)) {
            //二级库领用
            materialRequisitionInfoDTO.setCustodialWarehouseName(level2Map.get(materialRequisition.getCustodialWarehouseCode()));
        } else if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(requisitionType)) {
            //三级库领用
            materialRequisitionInfoDTO.setApplyWarehouseName(level2Map.get(materialRequisition.getApplyWarehouseCode()));
            materialRequisitionInfoDTO.setCustodialWarehouseName(level3Map.get(materialRequisition.getCustodialWarehouseCode()));
        }

        // 获取物资明细
        // 物资明细需要单位翻译
        Map<String, String> unitMap = sysBaseApi.queryDictItemsByCode("materian_unit").stream()
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        //物资类型字典
        Map<String, String> typeMap = sysBaseApi.queryDictItemsByCode("material_type").stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        List<MaterialRequisitionDetailInfoDTO> materialRequisitionDetailInfoDTOList =  materialRequisitionMapper.queryRequisitionDetailByRequisitionId(id);
        // 物资明细转化从对应DTO，并翻译单位
        materialRequisitionDetailInfoDTOList.forEach(detail -> {
            detail.setUnitName(unitMap.get(detail.getUnit()));
            detail.setTypeName(typeMap.get(detail.getType()));
            //查询实时可用量
            if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_REPAIR.equals(requisitionType)) {
                //维修申领
                SparePartStock sparePartStock = sparePartStockService.getOne(new LambdaQueryWrapper<SparePartStock>()
                        .eq(SparePartStock::getWarehouseCode, materialRequisition.getApplyWarehouseCode())
                        .eq(SparePartStock::getMaterialCode, detail.getMaterialsCode())
                        .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0), false);
                detail.setAvailableNum(ObjectUtil.isNotNull(sparePartStock) ? sparePartStock.getAvailableNum() : 0);
            } else if (MaterialRequisitionConstant.MATERIAL_REQUISITION_TYPE_LEVEL3.equals(requisitionType)) {
                //三级库领用
                StockLevel2 stockLevel2 = stockLevel2Service.getOne(new LambdaQueryWrapper<StockLevel2>()
                        .eq(StockLevel2::getWarehouseCode, materialRequisition.getApplyWarehouseCode())
                        .eq(StockLevel2::getMaterialCode, detail.getMaterialsCode())
                        .eq(StockLevel2::getDelFlag, CommonConstant.DEL_FLAG_0), false);
                detail.setAvailableNum(ObjectUtil.isNotNull(stockLevel2) ? stockLevel2.getAvailableNum() : 0);
            }
        });
        materialRequisitionInfoDTO.setMaterialRequisitionDetailInfoDTOList(materialRequisitionDetailInfoDTOList);

        return materialRequisitionInfoDTO;
    }

    @Override
    public void deleteById(String id) {
        this.removeById(id);
        LambdaQueryWrapper<MaterialRequisitionDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialRequisitionDetail::getMaterialRequisitionId, id);
        materialRequisitionDetailMapper.delete(queryWrapper);
    }
}
