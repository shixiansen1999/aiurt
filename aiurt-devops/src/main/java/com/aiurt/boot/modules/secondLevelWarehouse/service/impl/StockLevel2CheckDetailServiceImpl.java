package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.enums.MaterialTypeEnum;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailEditDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockLevel2CheckDetailVO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockLevel2CheckDetailMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockLevel2CheckMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockLevel2Mapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockLevel2CheckDetailService;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockLevel2CheckService;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockLevel2Service;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 二级库盘点列表记录
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
@Service
public class StockLevel2CheckDetailServiceImpl extends ServiceImpl<StockLevel2CheckDetailMapper, StockLevel2CheckDetail> implements IStockLevel2CheckDetailService {

    @Resource
    private StockLevel2CheckDetailMapper stockLevel2CheckDetailMapper;
    @Resource
    private StockLevel2CheckMapper stockLevel2CheckMapper;
    @Resource @Lazy
    private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
    @Resource
    private IStockLevel2Service iStockLevel2Service;
    @Resource
    private IStockLevel2CheckService iStockLevel2CheckService;
    @Override
    public IPage<StockLevel2CheckDetailVO> queryPageList(Page<StockLevel2CheckDetailVO> page,
                                                         StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO) {
        IPage<StockLevel2CheckDetailVO> checkDetailVOList =
                stockLevel2CheckDetailMapper.queryPageList(page,stockLevel2CheckDetailDTO);
        checkDetailVOList.getRecords().forEach(e->{
            StockLevel2 one = iStockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                    .eq("warehouse_code", e.getWarehouseCode())
                    .eq("material_code", e.getMaterialCode()), false);
            if(ObjectUtil.isNotEmpty(one)){
                //账面数量
                e.setBookNum(one.getNum());
                //账面价值
                e.setBookPrice(e.getPrice().multiply(new BigDecimal(e.getBookNum())));
            }
        });
        return checkDetailVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDetail(List<StockLevel2CheckDetailEditDTO> checkDetailList) {
        //更新二级库盘点列表记录
        List<StockLevel2CheckDetail> list = new ArrayList<>();
        checkDetailList.forEach(e->{
            StockLevel2CheckDetail detail = new StockLevel2CheckDetail();
            BeanUtils.copyProperties(e,detail);
            list.add(detail);
        });
        iStockLevel2CheckDetailService.updateBatchById(list);

        //更新二级库盘点列表的盘点结束时间
        StockLevel2CheckDetail one = iStockLevel2CheckDetailService.getById(checkDetailList.get(0).getId());

        if(ObjectUtil.isNotEmpty(one)){
            StockLevel2Check check = new StockLevel2Check();
            check.setCheckEndTime(new Date());
            iStockLevel2CheckService.update(check,new QueryWrapper<StockLevel2Check>()
                    .eq("stock_check_code",one.getStockCheckCode()));
        }
    }

    @Override
    public IPage<StockLevel2CheckDetailVO> queryNewestStockList(Page<StockLevel2CheckDetailVO> page,
                                                                String warehouseCode) {
        List<StockLevel2Check> stockLevel2Checks =
                stockLevel2CheckMapper.selectList(new QueryWrapper<StockLevel2Check>().eq("warehouse_code", warehouseCode)
                .orderByDesc("update_time"));
        if(CollUtil.isNotEmpty(stockLevel2Checks)){
            String stockCheckCode = stockLevel2Checks.get(0).getStockCheckCode();
            StockLevel2CheckDetailDTO dto = new StockLevel2CheckDetailDTO();
            dto.setStockCheckCode(stockCheckCode);
            return this.queryPageList(page, dto);
        }
        return null;
    }

    @Override
    public List<StockLevel2CheckDetailExcel> exportNewestStockXls(String warehouseCode) {
        List<StockLevel2CheckDetailExcel> excels = new ArrayList<>();
        List<StockLevel2Check> stockLevel2Checks = stockLevel2CheckMapper.selectList(
                new QueryWrapper<StockLevel2Check>().eq("warehouse_code", warehouseCode)
                .orderByDesc("update_time"));
        if(CollUtil.isNotEmpty(stockLevel2Checks)){
            String stockCheckCode = stockLevel2Checks.get(0).getStockCheckCode();
            StockLevel2CheckDetailDTO dto = new StockLevel2CheckDetailDTO();
            dto.setStockCheckCode(stockCheckCode);
            List<StockLevel2CheckDetailVO> list = stockLevel2CheckDetailMapper.queryList(dto);
            for (StockLevel2CheckDetailVO e : list) {
                StockLevel2 one = iStockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                        .eq("warehouse_code", e.getWarehouseCode())
                        .eq("material_code", e.getMaterialCode()), false);
                if (ObjectUtil.isNotEmpty(one)) {
                    //账面数量
                    e.setBookNum(one.getNum());
                    //账面价值
                    e.setBookPrice(e.getPrice().multiply(new BigDecimal(e.getBookNum())));
                }
                StockLevel2CheckDetailExcel detail = new StockLevel2CheckDetailExcel();
                BeanUtils.copyProperties(e,detail);
                detail.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
                excels.add(detail);
            }
            return excels;
        }
        return null;
    }
}
