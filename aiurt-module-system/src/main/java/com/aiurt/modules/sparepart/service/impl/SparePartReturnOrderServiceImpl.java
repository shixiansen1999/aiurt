package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartReturnOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private ISparePartOutOrderService sparePartOutOrderService;
    /**
     * 查询列表
     * @param page
     * @param sparePartReturnOrder
     * @return
     */
    @Override
    public List<SparePartReturnOrder> selectList(Page page, SparePartReturnOrder sparePartReturnOrder){
        List<SparePartReturnOrder> sparePartReturnOrders = sparePartReturnOrderMapper.readAll(page, sparePartReturnOrder);
        List<SparePartReturnOrder> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(sparePartReturnOrders)){
            List<SparePartReturnOrder> collect = sparePartReturnOrders.stream().distinct().collect(Collectors.toList());
            list.addAll(collect);
        }
        return list;
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
        Date date = new Date();
        SparePartReturnOrder returnOrder = getById(sparePartReturnOrder.getId());
        //更新已出库库存数量,做减法
        List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0).eq(SparePartOutOrder::getMaterialCode,sparePartReturnOrder.getMaterialCode()).eq(SparePartOutOrder::getWarehouseCode,sparePartReturnOrder.getWarehouseCode()));
        if(!orderList.isEmpty()){
            for(int i =0;i<orderList.size();i++){
                SparePartOutOrder order = orderList.get(i);
                if(Integer.parseInt(order.getUnused())>=returnOrder.getNum()){
                    Integer number = Integer.parseInt(order.getUnused())-returnOrder.getNum();
                    order.setUnused(number+"");
                    updateOrder(order);
                }else{
                    return Result.error("剩余数量不足！");
                }
            }
        }
        //1.更改状态为“已确认”
        returnOrder.setConfirmId(user.getUsername());
        returnOrder.setConfirmTime(date);
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
        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_CONFRM_STATUS_1);
        sparePartInOrder.setConfirmId(user.getUsername());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrderService.save(sparePartInOrder);

        return Result.OK("操作成功！");


    }
    @Override
    public void updateOrder(SparePartOutOrder sparePartOutOrder){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setConfirmTime(new Date());
        sparePartOutOrder.setSysOrgCode(user.getOrgCode());
        sparePartOutOrderService.updateById(sparePartOutOrder);
    }
}
