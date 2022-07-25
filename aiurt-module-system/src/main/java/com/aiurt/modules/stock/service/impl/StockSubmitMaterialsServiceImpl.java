package com.aiurt.modules.stock.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.mapper.StockSubmitMaterialsMapper;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockSubmitMaterialsServiceImpl extends ServiceImpl<StockSubmitMaterialsMapper, StockSubmitMaterials> implements IStockSubmitMaterialsService {

    @Autowired
    private MaterialBaseTypeMapper materialBaseTypeMapper;

    @Override
    public IPage<StockSubmitMaterials> pageList(Page<StockSubmitMaterials> page, StockSubmitMaterials stockSubmitMaterials) {
        List<StockSubmitMaterials> baseList = baseMapper.pageList(page, stockSubmitMaterials);
        if(baseList != null && baseList.size()>0){
            for(StockSubmitMaterials stockSubmitMaterials1 : baseList){
                String baseTypeCodeCcName = getCcName(stockSubmitMaterials1);
                stockSubmitMaterials1.setBaseTypeCodeCcName(baseTypeCodeCcName);
            }
        }
        page.setRecords(baseList);
        return page;
    }

    public String getCcName(StockSubmitMaterials stockSubmitMaterials){
        String baseTypeCodeCc = stockSubmitMaterials.getBaseTypeCodeCc()==null?"":stockSubmitMaterials.getBaseTypeCodeCc();
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
