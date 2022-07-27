package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.aiurt.modules.stock.mapper.StockOutboundMaterialsMapper;
import com.aiurt.modules.stock.service.IStockOutOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockOutboundMaterialsService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockOutboundMaterialsServiceImpl extends ServiceImpl<StockOutboundMaterialsMapper, StockOutboundMaterials> implements IStockOutboundMaterialsService {

    @Autowired
    private IMaterialBaseService iMaterialBaseService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;
    @Autowired
    private ICsMajorService csMajorService;
    @Autowired
    private ICsSubsystemService csSubsystemService;
    @Autowired
    private ICsManufactorService csManufactorService;
    @Override
    public StockOutboundMaterials translate(StockOutboundMaterials materials){
        String materialCode = materials.getMaterialCode();
        MaterialBase materialBase = iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
        materialBase = iMaterialBaseService.translate(materialBase);
        CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_code",materialBase.getMajorCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
        String zyname = csMajor==null?"":csMajor.getMajorName();
        CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("system_code",materialBase.getSystemCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
        String zxyname = csSubsystem==null?"":csSubsystem.getSystemName();
        CsManufactor csManufactor = csManufactorService.getOne(new QueryWrapper<CsManufactor>().eq("code",materialBase.getManufactorCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
        String csname = csManufactor==null?"":csManufactor.getName();
        String wztype = materialBase.getType()==null?"":materialBase.getType().toString();
        String wztypename = sysBaseApi.translateDict("material_type",wztype);
        String unitcode = materialBase.getUnit()==null?"":materialBase.getUnit();
        String unitname = sysBaseApi.translateDict("materian_unit",unitcode);
        materials.setMajorCodeName(zyname);
        materials.setSystemCodeName(zxyname);
        materials.setBaseTypeCodeCcName(materialBase.getBaseTypeCodeCcName());
        materials.setName(materialBase.getName());
        materials.setTypeName(wztypename);
        materials.setUnitName(unitname);
        materials.setManufactorCodeName(csname);
        materials.setSpecifications(materialBase.getSpecifications());
        materials.setPrice(materialBase.getPrice());
        return materials;
    }
}
