package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.aiurt.boot.common.enums.MaterialTypeEnum;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2Query;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2VO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.StockLevel2Mapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 二级库库存信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Service
public class StockLevel2ServiceImpl extends ServiceImpl<StockLevel2Mapper, StockLevel2> implements IStockLevel2Service {

    @Resource
    private StockLevel2Mapper stockLevel2Mapper;

    /**
     * 二级库库存信息
     * @param page
     * @param stockLevel2Query
     * @return
     */
    @Override
    public IPage<StockLevel2VO> queryPageList(Page<StockLevel2VO> page, StockLevel2Query stockLevel2Query) {
        IPage<StockLevel2VO> pageList = stockLevel2Mapper.queryPageList(page, stockLevel2Query);
        pageList.getRecords().forEach(e->{
            if(e.getType()!=null){
                e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
            }
           /* //计算总价
            if (e.getNum()!=null && e.getPrice()!=null) {
                e.setTotalPrice(e.getNum() * e.getPrice());
            }*/
        });

        return pageList;
    }

    /**
     * 导出excel
     * @param stockLevel2Query
     * @return
     */
    @Override
    public List<StockLevel2VO> exportXls(StockLevel2Query stockLevel2Query) {
        List<StockLevel2VO> pageList = stockLevel2Mapper.exportXls(stockLevel2Query);
        pageList.forEach(e->{
            if(e.getType()!=null){
                e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
            }
 /*           //计算总价
            if (e.getNum()!=null && e.getPrice()!=null) {
                e.setTotalPrice(e.getNum() * e.getPrice());
            }*/
        });

        return pageList;
    }

    /**
     * 填写备注
     * @param id
     * @param remark
     * @return
     */
    @Override
    public Result addRemark(Integer id, String remark) {
        stockLevel2Mapper.addRemark(id,remark);
        return Result.ok();
    }
}
