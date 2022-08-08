package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.common.constant.CacheConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.SparePartLendMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartLendService;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.SysUserMapper;
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
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartLendServiceImpl extends ServiceImpl<SparePartLendMapper, SparePartLend> implements ISparePartLendService {
    @Autowired
    private SparePartLendMapper sparePartLendMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private ISparePartOutOrderService sparePartOutOrderService;
    @Autowired
    private ISparePartInOrderService sparePartInOrderService;
    /**
     * 查询列表
     * @param page
     * @param sparePartLend
     * @return
     */
    @Override
    public List<SparePartLend> selectList(Page page, SparePartLend sparePartLend){
        return sparePartLendMapper.readAll(page,sparePartLend);
    }
    /**
     * 添加
     *
     * @param sparePartLend
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(SparePartLend sparePartLend) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询借入仓库
        SparePartStockInfo sparePartStockInfo = sparePartStockInfoMapper.selectOne(new LambdaQueryWrapper<SparePartStockInfo>().eq(SparePartStockInfo::getOrganizationId,user.getOrgId()).eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(null!=sparePartStockInfo){
            sparePartLend.setBackWarehouseCode(sparePartStockInfo.getWarehouseCode());
        }
        sparePartLend.setOutTime(new Date());
        sparePartLend.setLendPerson(user.getUsername());
        sparePartLendMapper.insert(sparePartLend);
        return Result.OK("添加成功！");
    }
    /**
     * 借出确认
     *
     * @param sparePartLend
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> lendConfirm(SparePartLend sparePartLend) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        SparePartLend partLend = getById(sparePartLend.getId());
        //1.更新借出数量、更改状态为“已借”
        updateById(sparePartLend);
        //2.借出仓库库存数做减法
        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getLendWarehouseCode()));
        lendStock.setNum(lendStock.getNum()-partLend.getLendNum());
        sparePartStockMapper.updateById(lendStock);
        //3.添加出库记录
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartOutOrder.setNum(partLend.getLendNum());
        sparePartOutOrder.setConfirmTime(date);
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setApplyOutTime(date);
        sparePartOutOrder.setApplyUserId(partLend.getLendPerson());
        sparePartOutOrder.setStatus(2);
        sparePartOutOrderService.save(sparePartOutOrder);
        //4.借入仓库库存数做加法
        SparePartStock backStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getBackWarehouseCode()));
        backStock.setNum(backStock.getNum()+partLend.getLendNum());
        sparePartStockMapper.updateById(backStock);
        //5.添加入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartInOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartInOrder.setNum(partLend.getLendNum());
        sparePartInOrder.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getLendPerson())).getOrgId());
        sparePartInOrder.setConfirmStatus("1");
        sparePartInOrder.setConfirmId(user.getUsername());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrderService.save(sparePartInOrder);
        return Result.OK("编辑成功！");
    }
    /**
     * 归还确认
     *
     * @param sparePartLend
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> backConfirm(SparePartLend sparePartLend) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        SparePartLend partLend = getById(sparePartLend.getId());
        //1.更新归还数量、更改状态为“已完结”
        updateById(sparePartLend);
        //2.借出仓库库存数做加法
        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getLendWarehouseCode()));
        lendStock.setNum(lendStock.getNum()+partLend.getLendNum());
        sparePartStockMapper.updateById(lendStock);
        //3.添加入库记录
        SparePartInOrder sparePartInOrder = new SparePartInOrder();
        sparePartInOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartInOrder.setWarehouseCode(partLend.getLendWarehouseCode());
        sparePartInOrder.setNum(partLend.getLendNum());
        sparePartInOrder.setOrgId(sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,partLend.getBackPerson())).getOrgId());
        sparePartInOrder.setConfirmStatus("1");
        sparePartInOrder.setConfirmId(user.getUsername());
        sparePartInOrder.setConfirmTime(date);
        sparePartInOrderService.save(sparePartInOrder);
        //3.借入仓库库存数做减法
        SparePartStock backStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode,partLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode,partLend.getBackWarehouseCode()));
        backStock.setNum(backStock.getNum()-partLend.getLendNum());
        sparePartStockMapper.updateById(backStock);
        //4.添加出库记录
        SparePartOutOrder sparePartOutOrder = new SparePartOutOrder();
        sparePartOutOrder.setMaterialCode(partLend.getMaterialCode());
        sparePartOutOrder.setWarehouseCode(partLend.getBackWarehouseCode());
        sparePartOutOrder.setNum(partLend.getLendNum());
        sparePartOutOrder.setConfirmTime(date);
        sparePartOutOrder.setConfirmUserId(user.getUsername());
        sparePartOutOrder.setApplyOutTime(date);
        sparePartOutOrder.setApplyUserId(partLend.getBackPerson());
        sparePartOutOrder.setStatus(2);
        sparePartOutOrderService.save(sparePartOutOrder);
        return Result.OK("编辑成功！");
    }
}
