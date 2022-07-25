package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.util.ObjectUtil;

import com.aiurt.common.enums.MaterialTypeEnum;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.dto.SparePartOutExcel;
import com.aiurt.modules.sparepart.entity.vo.SparePartOutVO;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
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
    public Result<?> addOutOrder(Result<?> result, SparePartOutOrder sparePartOutOrder) {
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
