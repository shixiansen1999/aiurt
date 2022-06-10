package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.util.PageLimitUtil;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockLevel2CheckDetailMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockLevel2CheckMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockLevel2Mapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockLevel2CheckService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class StockLevel2CheckServiceImpl extends ServiceImpl<StockLevel2CheckMapper, StockLevel2Check> implements IStockLevel2CheckService {
    @Resource
    private StockLevel2Mapper stockLevel2Mapper;
    @Resource
    private StockLevel2CheckMapper stockLevel2CheckMapper;
    @Resource
    private StockLevel2CheckDetailMapper stockLevel2CheckDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCheck(StockLevel2Check check) {
        //模拟盘点任务单号
        String simulateCheckCode = simulateCheckCode();

        check.setStockCheckCode(simulateCheckCode);
        stockLevel2CheckMapper.insert(check);

        //去二级库存中查询该仓库的所有物资
        List<StockLevel2> stockLevel2List = stockLevel2Mapper.selectList(new QueryWrapper<StockLevel2>()
                .eq("warehouse_code", check.getWarehouseCode()));
        stockLevel2List.forEach(e->{
            //插入二级库盘点详情初始数据
            StockLevel2CheckDetail checkDetail = new StockLevel2CheckDetail();
            checkDetail.setStockCheckCode(check.getStockCheckCode());
            checkDetail.setWarehouseCode(check.getWarehouseCode());
            checkDetail.setMaterialCode(e.getMaterialCode());
            stockLevel2CheckDetailMapper.insert(checkDetail);
        });


    }

    @Override
    public IPage<Stock2CheckVO> queryPageList(IPage<Stock2CheckVO> page,StockLevel2CheckDTO stockLevel2CheckDTO) {
        IPage<Stock2CheckVO> stock2CheckVOIPage = stockLevel2CheckMapper.queryPageList(
                page,
                stockLevel2CheckDTO.getStockCheckCode(),
                stockLevel2CheckDTO.getWarehouseCode(),
                stockLevel2CheckDTO.getStartTime(),
                stockLevel2CheckDTO.getEndTime());
        stock2CheckVOIPage.getRecords().forEach(e->{
            List<StockLevel2CheckDetail> checkDetailList = stockLevel2CheckDetailMapper.selectList(new QueryWrapper<StockLevel2CheckDetail>()
                    .eq("stock_check_code", e.getStockCheckCode()));
            if(CollUtil.isNotEmpty(checkDetailList)){
                int sum = checkDetailList.stream().filter(j->j.getActualNum()!=null)
                        .mapToInt(StockLevel2CheckDetail::getActualNum).sum();
                e.setCheckAllNum(sum);
            }
        });

        return  stock2CheckVOIPage;
    }

    @Override
    public List<StockLevel2CheckExcel> exportXls(List<Integer> ids) {
        List<StockLevel2CheckExcel> excels = stockLevel2CheckMapper.exportXls(ids);
        excels.forEach(e->{
            List<StockLevel2CheckDetail> checkDetailList = stockLevel2CheckDetailMapper.selectList(new QueryWrapper<StockLevel2CheckDetail>()
                    .eq("stock_check_code", e.getStockCheckCode()));
            if(CollUtil.isNotEmpty(checkDetailList)){
                int sum = checkDetailList.stream().mapToInt(StockLevel2CheckDetail::getActualNum).sum();
                e.setCheckNum(sum);
            }
        });
        return excels;
    }

    private String simulateCheckCode() {
        String applyCode="PD101.";
        String y = String.valueOf(LocalDateTime.now().getYear());
        String year = y.substring(y.length() - 2);
        int monthValue = LocalDateTime.now().getMonthValue();
        String month= monthValue +".";
        if(monthValue<10){
            month="0"+ monthValue+".";
        }
        Integer integer = stockLevel2CheckMapper.selectCount(null);
        return applyCode+year+month+(integer+1);
    }
}
