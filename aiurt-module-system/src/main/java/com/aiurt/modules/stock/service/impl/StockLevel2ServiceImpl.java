package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.mapper.StockLevel2Mapper;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockLevel2ServiceImpl extends ServiceImpl<StockLevel2Mapper, StockLevel2> implements IStockLevel2Service {

    @Autowired
    private StockLevel2Mapper stockLevel2Mapper;
    @Autowired
    private MaterialBaseTypeMapper materialBaseTypeMapper;

    @Override
    public IPage<StockLevel2> pageList(Page<StockLevel2> page, StockLevel2 stockLevel2) {
        List<StockLevel2> baseList = baseMapper.pageList(page, stockLevel2);
        if(baseList != null && baseList.size()>0){
            for(StockLevel2 stockLevel21 : baseList){
                String baseTypeCodeCcName = getCcName(stockLevel21);
                stockLevel21.setBaseTypeCodeName(baseTypeCodeCcName);
            }
        }
        page.setRecords(baseList);
        return page;
    }

    @Override
    public StockLevel2 getDetailById(String id) {
        StockLevel2 stockLevel2 = stockLevel2Mapper.getDetailById(id);
        String baseTypeCodeCcName = getCcName(stockLevel2);
        stockLevel2.setBaseTypeCodeName(baseTypeCodeCcName);
        return stockLevel2;
    }

    @Override
    public List<StockLevel2> exportXls(StockLevel2 stockLevel2,String ids) {
        List<String> strings = new ArrayList<>();
        if (StrUtil.isNotEmpty(ids)) {
            String[] split = ids.split(",");
           strings = Arrays.asList(split);
        }
        List<StockLevel2> stockLevel2s = stockLevel2Mapper.exportXls(stockLevel2,strings);
        if(stockLevel2s != null && stockLevel2s.size()>0){
            for(StockLevel2 stockLevel21 : stockLevel2s){
                String baseTypeCodeCcName = getCcName(stockLevel21);
                stockLevel21.setBaseTypeCodeName(baseTypeCodeCcName);
            }
        }
        return stockLevel2s;
    }

    public String getCcName(StockLevel2 stockLevel2){
        String baseTypeCodeCc = stockLevel2.getBaseTypeCode()==null?"":stockLevel2.getBaseTypeCode();
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
