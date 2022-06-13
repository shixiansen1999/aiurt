package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInDetailVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockInOrderLevel2DetailMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockInOrderLevel2Mapper;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2Mapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockInOrderLevel2DetailService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2Service;
import com.aiurt.common.enums.ProductiveTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 二级入库单详细信息
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockInOrderLevel2DetailServiceImpl
        extends ServiceImpl<StockInOrderLevel2DetailMapper, StockInOrderLevel2Detail> implements IStockInOrderLevel2DetailService {

    @Resource
    private StockInOrderLevel2DetailMapper stockInOrderLevel2DetailMapper;

    @Resource
    private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;

    @Resource
    private StockLevel2Mapper stockLevel2Mapper;

    @Resource
    private IStockLevel2Service stockLevel2Service;


    /**
     * 根据入库单号查询入库备件列表
     * @param page
     * @param applyCode
     * @return
     */
    @Override
    public IPage<StockInDetailVO> queryPageList(IPage<StockInDetailVO> page, String applyCode) {
        IPage<StockInDetailVO> stockInDetailVOIPage = stockInOrderLevel2DetailMapper.selectPageList(page, applyCode);
        List<StockInDetailVO> records = stockInDetailVOIPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            for (int i = 0; i < records.size(); i++) {
                records.get(i).setTypeName(ProductiveTypeEnum.findMessage(records.get(i).getType()));
                if (records.get(i).getNum() != null && records.get(i).getPrice() != null) {
                    records.get(i).setTotalPrice(records.get(i).getNum() * records.get(i).getPrice());
                } else {
                    records.get(i).setTotalPrice(0);
                }
            }
        }
        return stockInDetailVOIPage;
    }

    /**
     * 根据id添加数量
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addNumById(List<StockDTO> dto) {
        for (StockDTO stockDTO : dto) {
            //修改盘点记录
            StockInOrderLevel2Detail inOrderLevel2Detail = this.getOne(new QueryWrapper<StockInOrderLevel2Detail>().eq(StockInOrderLevel2Detail.ID, stockDTO.getId()), false);
            StockInOrderLevel2Detail stockInOrderLevel2Detail = inOrderLevel2Detail.setNum(stockDTO.getNum());
            stockInOrderLevel2DetailMapper.updateById(stockInOrderLevel2Detail);
            //修改二级库库存信息
            StockInOrderLevel2 one = stockInOrderLevel2Mapper.selectOne(new QueryWrapper<StockInOrderLevel2>().eq(StockInOrderLevel2.ORDER_CODE, inOrderLevel2Detail.getOrderCode()));
            StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>()
                    .eq(StockLevel2.MATERIAL_CODE, inOrderLevel2Detail.getMaterialCode())
                    .eq(StockLevel2.WAREHOUSE_CODE, one.getWarehouseCode()));
            stockLevel2.setNum(stockLevel2.getNum()+inOrderLevel2Detail.getNum());
            stockLevel2Mapper.updateById(stockLevel2);
        }
        return Result.ok();
    }
}
