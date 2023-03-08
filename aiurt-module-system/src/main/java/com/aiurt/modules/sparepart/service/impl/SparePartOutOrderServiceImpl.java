package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class SparePartOutOrderServiceImpl extends ServiceImpl<SparePartOutOrderMapper, SparePartOutOrder> implements ISparePartOutOrderService {
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;
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
     * 查询列表
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectListById( SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.readAll(sparePartOutOrder);
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
            return Result.OK("操作成功!");
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

    /**
     *
     * @param materialCode 物资编码
     * @return
     */
    @Override
    public List<SparePartOutOrder> querySparePartOutOrder(String materialCode) {

        if (StrUtil.isBlank(materialCode)) {
            return Collections.emptyList();
        }

        // 获取当前登录人所属机构， 根据所属机构擦查询管理二级管理仓库
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        String orgId = loginUser.getOrgId();

        if (StrUtil.isBlank(orgId)) {
            log.info("该用户没绑定机构：{}-{}", loginUser.getRealname(), loginUser.getUsername());
            return Collections.emptyList();
        }
        // todo 能否查询下级机构的仓库信息
        SysDepart sysDepart = sysDepartService.getById(orgId);
        if (Objects.isNull(sysDepart)) {
            log.info("该机构不存在：{}", orgId);
            return Collections.emptyList();
        }

        // 查询仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId, orgId);
        List<SparePartStockInfo> stockInfoList = sparePartStockInfoService.getBaseMapper().selectList(wrapper);
        if (CollectionUtil.isEmpty(stockInfoList)) {
            return Collections.emptyList();
        }

        List<String> wareHouseCodeList = stockInfoList.stream().map(SparePartStockInfo::getWarehouseCode).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(wareHouseCodeList)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<SparePartOutOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartOutOrder::getMaterialCode, materialCode)
                .in(SparePartOutOrder::getWarehouseCode, wareHouseCodeList).eq(SparePartOutOrder::getStatus, 2);
        List<SparePartOutOrder> outOrders = baseMapper.selectList(queryWrapper);
        return outOrders;
    }
}
