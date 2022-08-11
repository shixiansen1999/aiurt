package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
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
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Service
public class SparePartOutOrderServiceImpl extends ServiceImpl<SparePartOutOrderMapper, SparePartOutOrder> implements ISparePartOutOrderService {
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectList(Page page, SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.readAll(page,sparePartOutOrder);
    }
    /**
     * 查询已出库的物资编号
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectMaterial(Page page, SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.selectMaterial(page,sparePartOutOrder);
    }
    /**
     * 确认
     *
     * @param sparePartOutOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartOutOrder sparePartOutOrder) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartOutOrder outOrder = getById(sparePartOutOrder.getId());
        // 更新备件库存数据（原库存数-出库数量）
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,outOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,outOrder.getWarehouseCode()));
        if(null!=sparePartStock && sparePartStock.getNum()>=outOrder.getNum()){
            sparePartStock.setNum(sparePartStock.getNum()-outOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);
            //查询出库表同一仓库、同一备件是否有出库记录，没有则更新剩余数量为出库数量；有则更新同一仓库、同一备件所有数据的剩余数量=剩余数量+出库数量
            List<SparePartOutOrder> orderList = list(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,outOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,outOrder.getWarehouseCode()));
            if(orderList.isEmpty()){
                sparePartOutOrder.setUnused(outOrder.getNum()+"");
                updateOrder(sparePartOutOrder);
            }else{
                orderList.forEach(order -> {
                    Integer n = Integer.parseInt(order.getUnused())+outOrder.getNum();
                    order.setStatus(sparePartOutOrder.getStatus());
                    order.setUnused(n+"");
                    updateOrder(order);
                });
            }
            return Result.OK("编辑成功!");
        }else{
            return Result.error("库存数量不足!");
        }

    }
    public void updateOrder(SparePartOutOrder sparePartOutOrder){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setConfirmTime(new Date());
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        updateById(sparePartOutOrder);
    }
}
