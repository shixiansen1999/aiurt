package com.aiurt.modules.stock.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.mapper.MaterialBaseTypeMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.dto.StockLevel2RespDTO;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.mapper.StockLevel2Mapper;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private IMaterialBaseService materialBaseService;

    @Override
    public IPage<StockLevel2RespDTO> pageList(Page<StockLevel2> page, StockLevel2 stockLevel2) {
        List<StockLevel2> baseList = baseMapper.pageList(page, stockLevel2);

        Page<StockLevel2RespDTO> pageList = new Page<>();
        pageList.setPages(page.getPages());
        pageList.setCurrent(page.getCurrent());
        pageList.setSize(page.getSize());
        pageList.setTotal(page.getTotal());

        // 如果查询数据为空，直接返回
        if (CollUtil.isEmpty(baseList)){
            return pageList;
        }

        // 二级库查询数据中的物资编码，要查询物资编码的一些信息
        List<String> materialCodeList = new ArrayList<>();

        for(StockLevel2 stockLevel21 : baseList){
            String baseTypeCodeCcName = getCcName(stockLevel21);
            stockLevel21.setBaseTypeCodeName(baseTypeCodeCcName);
            // 将物资编码存入materialCodeList
            materialCodeList.add(stockLevel21.getMaterialCode());
        }

        // 根据物资编码，查询物资，并根据物资编码做成一个物资编码为key,物资为value的map
        Map<String, MaterialBase> materialBaseMap = new HashMap<>();
        if (CollUtil.isNotEmpty(materialCodeList)){
            LambdaQueryWrapper<MaterialBase> materialBaseQueryWrapper = new LambdaQueryWrapper<>();
            materialBaseQueryWrapper.in(MaterialBase::getCode, materialCodeList);
            materialBaseQueryWrapper.eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0);
            materialBaseMap = materialBaseService.list(materialBaseQueryWrapper).stream()
                    .collect(Collectors.toMap(MaterialBase::getCode, v -> v));
        }

        // 根据物资的信息，给二级库库存信息列表查询添加信息
        Map<String, MaterialBase> finalMaterialBaseMap = materialBaseMap;
        List<StockLevel2RespDTO> collect = baseList.stream().map(s -> {
            StockLevel2RespDTO stockLevel2RespDTO = new StockLevel2RespDTO();
            BeanUtils.copyProperties(s, stockLevel2RespDTO);
            MaterialBase materialBase = finalMaterialBaseMap.get(s.getMaterialCode());

            Optional.ofNullable(materialBase).ifPresent((m)->{
                stockLevel2RespDTO.setPrice(m.getPrice() != null ? new BigDecimal(m.getPrice()): null);
                if (stockLevel2RespDTO.getNum() != null && stockLevel2RespDTO.getPrice() != null){
                    stockLevel2RespDTO.setTotalPrices(stockLevel2RespDTO.getPrice().multiply(BigDecimal.valueOf(stockLevel2RespDTO.getNum())));
                }else{
                    stockLevel2RespDTO.setTotalPrices(BigDecimal.valueOf(0));
                }
                // 物资表里面的manufactorCode字段实际上是厂商表的id
                stockLevel2RespDTO.setManufactorId(materialBase.getManufactorCode());
                stockLevel2RespDTO.setTechnicalParameter(materialBase.getTechnicalParameter());
            });
            return stockLevel2RespDTO;
        }).collect(Collectors.toList());

        pageList.setRecords(collect);
        return pageList;
    }

    @Override
    public StockLevel2 getDetailById(String id) {
        StockLevel2 stockLevel2 = stockLevel2Mapper.getDetailById(id);
        String baseTypeCodeCcName = getCcName(stockLevel2);
        stockLevel2.setBaseTypeCodeName(baseTypeCodeCcName);
        return stockLevel2;
    }

    @Override
    public StockLevel2RespDTO queryDetailById(String id){
        StockLevel2 stockLevel2 = getDetailById(id);
        StockLevel2RespDTO stockLevel2RespDTO = new StockLevel2RespDTO();
        BeanUtils.copyProperties(stockLevel2, stockLevel2RespDTO);

        // 查询库存信息对应的物资，并给响应DTO赋值
        LambdaQueryWrapper<MaterialBase> materialBaseQueryWrapper = new LambdaQueryWrapper<>();
        materialBaseQueryWrapper.in(MaterialBase::getCode, stockLevel2.getMaterialCode());
        materialBaseQueryWrapper.eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0);
        MaterialBase materialBase = materialBaseService.getOne(materialBaseQueryWrapper);
        // 物资表里面的manufactorCode字段实际上是厂商表的id
        stockLevel2RespDTO.setManufactorId(materialBase.getManufactorCode());
        stockLevel2RespDTO.setTechnicalParameter(materialBase.getTechnicalParameter());

        // 计算总价
        stockLevel2RespDTO.setPrice(materialBase.getPrice() != null ? new BigDecimal(materialBase.getPrice()): null);
        if (stockLevel2RespDTO.getNum() != null && stockLevel2RespDTO.getPrice() != null){
            stockLevel2RespDTO.setTotalPrices(stockLevel2RespDTO.getPrice().multiply(BigDecimal.valueOf(stockLevel2RespDTO.getNum())));
        }else{
            stockLevel2RespDTO.setTotalPrices(BigDecimal.valueOf(0));
        }

        return stockLevel2RespDTO;
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
            boolean filter = GlobalThreadLocal.setDataFilter(false);
            MaterialBaseType materialBaseType = materialBaseTypeMapper.selectOne(new QueryWrapper<MaterialBaseType>().eq("base_type_code",baseTypeCodeCc));
            GlobalThreadLocal.setDataFilter(filter);
            baseTypeCodeCcName = materialBaseType==null?"":materialBaseType.getBaseTypeName()+CommonConstant.SYSTEM_SPLIT_STR;
        }
        if(baseTypeCodeCcName.contains(CommonConstant.SYSTEM_SPLIT_STR)){
            baseTypeCodeCcName = baseTypeCodeCcName.substring(0,baseTypeCodeCcName.length()-1);
        }
        return baseTypeCodeCcName;
    }
}
