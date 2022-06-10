package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.enums.MaterialLendStatus;
import com.swsc.copsms.common.enums.MaterialTypeEnum;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartStock;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.SparePartOutOrderMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartOutOrderService;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartStockService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件出库表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class SparePartOutOrderServiceImpl extends ServiceImpl<SparePartOutOrderMapper, SparePartOutOrder> implements ISparePartOutOrderService {
    @Resource
    private ISparePartStockService iSparePartStockService;
    @Resource
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Resource @Lazy
    private ISparePartOutOrderService iSparePartOutOrderService;
    @Override
    public IPage<SparePartOutVO> queryPageList(Page<SparePartOutVO> page, SparePartLendQuery sparePartLendQuery) {

        return sparePartOutOrderMapper.queryPageList(page,sparePartLendQuery);
    }

    @Override
    public Result<?> addOutOrder(Result<?> result,SparePartOutOrder sparePartOutOrder) {
        //判断库存够不够
        SparePartStock one = iSparePartStockService.getOne(new QueryWrapper<SparePartStock>()
                .eq("warehouse_code", sparePartOutOrder.getWarehouseCode())
                .eq("material_code", sparePartOutOrder.getMaterialCode()), false);
        if(ObjectUtil.isNotEmpty(one)){
            if(one.getNum()<sparePartOutOrder.getNum()){
                return result.error500("备件："+sparePartOutOrder.getMaterialCode()+" 库存不足");
            }
        }
        //新增出库信息
        iSparePartOutOrderService.save(sparePartOutOrder);
        //库存减少
        one.setNum(one.getNum()-sparePartOutOrder.getNum());
        iSparePartStockService.updateById(one);
        return result.success("添加成功");
    }

    @Override
    public List<SparePartOutExcel> exportXls(SparePartLendQuery sparePartLendQuery) {
        List<SparePartOutExcel> list = sparePartOutOrderMapper.exportXls(sparePartLendQuery);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
            list.get(i).setTypeName(MaterialTypeEnum.getNameByCode(list.get(i).getType()));
        }
        return list;
    }
}
