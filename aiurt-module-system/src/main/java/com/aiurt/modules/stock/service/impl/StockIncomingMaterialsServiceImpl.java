package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.mapper.StockIncomingMaterialsMapper;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
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
public class StockIncomingMaterialsServiceImpl extends ServiceImpl<StockIncomingMaterialsMapper, StockIncomingMaterials> implements IStockIncomingMaterialsService {
    @Autowired
    private MaterialBaseTypeMapper materialBaseTypeMapper;

    @Override
    public String getCcName(StockIncomingMaterials stockIncomingMaterials){
        String baseTypeCodeCc = stockIncomingMaterials.getBaseTypeCodeCc()==null?"":stockIncomingMaterials.getBaseTypeCodeCc();
        String baseTypeCodeCcName = "";
        if(baseTypeCodeCc.contains(CommonConstant.SYSTEM_SPLIT_STR)){
            List<String> strings = Arrays.asList(baseTypeCodeCc.split(CommonConstant.SYSTEM_SPLIT_STR));
            for(String typecode : strings){
                MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",typecode));
                baseTypeCodeCcName += materialBaseType==null?"":materialBaseType.getBaseTypeName()+"/";
            }
        }else{
            MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",baseTypeCodeCc));
            baseTypeCodeCcName = materialBaseType==null?"":materialBaseType.getBaseTypeName()+CommonConstant.SYSTEM_SPLIT_STR;
        }
        if(baseTypeCodeCcName.contains(CommonConstant.SYSTEM_SPLIT_STR)){
            baseTypeCodeCcName = baseTypeCodeCcName.substring(0,baseTypeCodeCcName.length()-1);
        }
        return baseTypeCodeCcName;
    }
}
