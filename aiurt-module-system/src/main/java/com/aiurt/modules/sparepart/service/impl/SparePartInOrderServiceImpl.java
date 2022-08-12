package com.aiurt.modules.sparepart.service.impl;


import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.mapper.SparePartApplyMaterialMapper;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Service
public class SparePartInOrderServiceImpl extends ServiceImpl<SparePartInOrderMapper, SparePartInOrder> implements ISparePartInOrderService {
    @Autowired
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartApplyMaterialMapper sparePartApplyMaterialMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartInOrder
     * @return
     */
    @Override
    public List<SparePartInOrder> selectList(Page page, SparePartInOrder sparePartInOrder){
         return sparePartInOrderMapper.readAll(page,sparePartInOrder);
    }

    /**
     * 确认
     * @param sparePartInOrder
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirm(SparePartInOrder sparePartInOrder){
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SparePartInOrder partInOrder = getById(sparePartInOrder.getId());
        // 1.更新当前表状态为已确认
        partInOrder.setConfirmId(user.getUsername());
        partInOrder.setConfirmTime(new Date());
        partInOrder.setConfirmStatus(sparePartInOrder.getConfirmStatus());
        sparePartInOrderMapper.updateById(partInOrder);
        // 2.回填申领单
        SparePartApplyMaterial material = sparePartApplyMaterialMapper.selectOne(new LambdaQueryWrapper<SparePartApplyMaterial>().eq(SparePartApplyMaterial::getMaterialCode,sparePartInOrder.getMaterialCode()).eq(SparePartApplyMaterial::getApplyCode,sparePartInOrder.getApplyCode()));
        if(null!=material){
            material.setActualNum(sparePartInOrder.getNum());
            sparePartApplyMaterialMapper.updateById(material);
        }
        // 3.更新备件库存数据（原库存数+入库的数量）
        //查询要入库的物资，备件库存中是否存在
        SparePartStock sparePartStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partInOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partInOrder.getWarehouseCode()));
        if(null!=sparePartStock){
            sparePartStock.setNum(sparePartStock.getNum()+partInOrder.getNum());
            sparePartStockMapper.updateById(sparePartStock);
        }else{
            SparePartStock stock = new SparePartStock();
            stock.setMaterialCode(partInOrder.getMaterialCode());
            stock.setNum(partInOrder.getNum());
            stock.setWarehouseCode(partInOrder.getWarehouseCode());
            stock.setOrgId(user.getOrgId());
            stock.setSysOrgCode(user.getOrgCode());
            sparePartStockMapper.insert(stock);
        }
    }
    /**
     * 修改
     *
     * @param sparePartInOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartInOrder sparePartInOrder) {
        confirm(sparePartInOrder);
        return Result.OK("操作成功！");
    }
    /**
     * 批量入库
     *
     * @param sparePartInOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchStorage(List<SparePartInOrder>  sparePartInOrder) {
        //查询状态为“已确认”的数据数量
        Long confirmedNum = sparePartInOrder.stream().filter(order -> order.getStatus().equals("1")).count();
        if(sparePartInOrder.size() == confirmedNum){
            return Result.error("勾选备件已入库，不用重复操作！");
        }
        //查询状态为“待确认”的数据
        sparePartInOrder.stream().filter(order -> order.getStatus().equals("0")).forEach(order -> {
            confirm(order);
        });
        return Result.OK("操作成功！");
    }
}
