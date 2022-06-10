package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.*;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2DTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.MaterialVO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.MaterialBaseMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockInOrderLevel2Mapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Service
public class StockInOrderLevel2ServiceImpl
        extends ServiceImpl<StockInOrderLevel2Mapper, StockInOrderLevel2> implements IStockInOrderLevel2Service {

    @Resource
    private MaterialBaseMapper materialBaseMapper;
    @Resource
    private IMaterialBaseService iMaterialBaseService;
    @Resource
    private IStockLevel2Service iStockLevel2Service;
    @Resource
    private IStockInOrderLevel2DetailService iStockInOrderLevel2DetailService;
    @Resource
    private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;


    @Override
    public void addWarehouseIn(StockInOrderLevel2DTO stockInOrderLevel2DTO) {
        //模拟入库单号
        String s = simulateStockInCode();
        StockInOrderLevel2 stockInOrderLevel2 = new StockInOrderLevel2();
        stockInOrderLevel2.setOrderCode(s);
        stockInOrderLevel2.setStockInTime(stockInOrderLevel2DTO.getStockInTime());
        stockInOrderLevel2.setWarehouseCode(stockInOrderLevel2DTO.getWarehouseCode());
        stockInOrderLevel2.setNote(stockInOrderLevel2DTO.getNote());

        stockInOrderLevel2Mapper.insert(stockInOrderLevel2);

        //新增入库单添加的物资信息
        List<MaterialVO> materialVOList = stockInOrderLevel2DTO.getMaterialVOList();

        materialVOList.forEach(e->{
            MaterialBase materialBase = iMaterialBaseService.getOne(
                    new QueryWrapper<MaterialBase>().eq("code", e.getMaterialCode()), false);
            if(ObjectUtil.isNotEmpty(materialBase)){
                //去库存查找该物料的库存信息,为空则插入一条数据，否则更新库存
                StockLevel2 one = iStockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                        .eq("material_code", materialBase.getCode())
                        .eq("warehouse_code", stockInOrderLevel2.getWarehouseCode()),false);
                if(ObjectUtil.isEmpty(one)){
                    StockLevel2 stockLevel2 = new StockLevel2();
                    BeanUtils.copyProperties(materialBase,stockLevel2);
                    BeanUtils.copyProperties(stockInOrderLevel2,stockLevel2);
                    stockLevel2.setMaterialCode(materialBase.getCode());
                    stockLevel2.setMaterialName(materialBase.getName());
                    stockLevel2.setNum(e.getMaterialNum());
                    stockLevel2.setStockInTime(stockInOrderLevel2.getStockInTime());
                    iStockLevel2Service.save(stockLevel2);
                }else{
                    one.setStockInTime(stockInOrderLevel2.getStockInTime());
                    one.setNum(one.getNum()+e.getMaterialNum());
                    iStockLevel2Service.updateById(one);
                }

                //插入入库单的详情信息
                StockInOrderLevel2Detail level2Detail = new StockInOrderLevel2Detail();
                level2Detail.setOrderId(stockInOrderLevel2.getId());
                level2Detail.setOrderCode(stockInOrderLevel2.getOrderCode());
                level2Detail.setMaterialCode(e.getMaterialCode());
                level2Detail.setNum(e.getMaterialNum());
                level2Detail.setCreateBy(stockInOrderLevel2.getCreateBy());
                iStockInOrderLevel2DetailService.save(level2Detail);

            }
        });
    }

    @Override
    public IPage<StockInOrderLevel2VO> queryPageList(Page<StockInOrderLevel2VO> page,
                                                     StockInOrderLevel2 stockInOrderLevel2,
                                                     String startTime,
                                                     String endTime) {

        IPage<StockInOrderLevel2VO> stockInOrderLevel2VOIPage = stockInOrderLevel2Mapper.queryPageList(page, stockInOrderLevel2, startTime, endTime);
        return stockInOrderLevel2VOIPage;
    }

    @Override
    public List<StockInOrderLevel2Excel> selectExcelData(List<Integer> ids) {
        return stockInOrderLevel2Mapper.selectExcelData(ids);
    }

    private String simulateStockInCode() {
        String applyCode="IS101.";
        String y = String.valueOf(LocalDateTime.now().getYear());
        String year = y.substring(y.length() - 2);
        int monthValue = LocalDateTime.now().getMonthValue();
        String month= monthValue +".";
        if(monthValue<10){
            month="0"+ monthValue+".";
        }
        Integer integer = stockInOrderLevel2Mapper.selectCount(null);
        return applyCode+year+month+(integer+1);
    }
}
