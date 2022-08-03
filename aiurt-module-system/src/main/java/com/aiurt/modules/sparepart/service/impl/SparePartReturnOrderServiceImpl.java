package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.mapper.SparePartReturnOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartReturnOrderServiceImpl extends ServiceImpl<SparePartReturnOrderMapper, SparePartReturnOrder> implements ISparePartReturnOrderService {
    @Autowired
    private SparePartReturnOrderMapper sparePartReturnOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private ISparePartInOrderService sparePartInOrderService;
    /**
     * 查询列表
     * @param page
     * @param sparePartReturnOrder
     * @return
     */
    @Override
    public List<SparePartReturnOrder> selectList(Page page, SparePartReturnOrder sparePartReturnOrder){
        return sparePartReturnOrderMapper.readAll(page,sparePartReturnOrder);
    }
    /**
     * 修改
     *
     * @param sparePartReturnOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartReturnOrder sparePartReturnOrder) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartReturnOrder returnOrder = getById(sparePartReturnOrder.getId());
        //1.更改状态为“已确认”
        returnOrder.setConfirmId(user.getUsername());
        returnOrder.setConfirmTime(new Date());
        returnOrder.setStatus(sparePartReturnOrder.getStatus());
        sparePartReturnOrderMapper.updateById(returnOrder);
        //2.库存做对应的加法
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,returnOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,returnOrder.getWarehouseCode()));
        if(null!=sparePartStock){
            sparePartStock.setNum(sparePartStock.getNum()+returnOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);
        }
        //3.插入备件入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(returnOrder.getMaterialCode());
        sparePartInOrder.setWarehouseCode(returnOrder.getWarehouseCode());
        sparePartInOrder.setNum(returnOrder.getNum());
        sparePartInOrder.setOrgId(user.getOrgId());
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_0);
        //sparePartInOrder.setOutOrderCode(orderCode);
        sparePartInOrderService.save(sparePartInOrder);

        return Result.OK("编辑成功！");
    }
}
