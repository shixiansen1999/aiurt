package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.mapper.CsManufactorMapper;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Service
public class SparePartStockInfoServiceImpl extends ServiceImpl<SparePartStockInfoMapper, SparePartStockInfo> implements ISparePartStockInfoService {
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    /**
     * 添加
     *
     * @param sparePartStockInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(SparePartStockInfo sparePartStockInfo) {
        //判断编码是否重复
        LambdaQueryWrapper<SparePartStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartStockInfo::getWarehouseCode, sparePartStockInfo.getWarehouseCode());
        queryWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> list = sparePartStockInfoMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("备件仓库编号重复，请重新填写！");
        }
        //判断名称是否重复
        LambdaQueryWrapper<SparePartStockInfo> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SparePartStockInfo::getWarehouseName, sparePartStockInfo.getWarehouseName());
        nameWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> nameList = sparePartStockInfoMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("备件仓库名称重复，请重新填写！");
        }
        //判断一个仓库仅能所属一个机构
        LambdaQueryWrapper<SparePartStockInfo> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(SparePartStockInfo::getOrganizationId, sparePartStockInfo.getOrganizationId());
        deptWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> deptList = sparePartStockInfoMapper.selectList(deptWrapper);
        if (!deptList.isEmpty()) {
            return Result.error("已存在该组织机构的备件仓库！");
        }
        sparePartStockInfoMapper.insert(sparePartStockInfo);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param sparePartStockInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(SparePartStockInfo sparePartStockInfo) {
        SparePartStockInfo stockInfo = getById(sparePartStockInfo.getId());
        //判断编码是否重复
        LambdaQueryWrapper<SparePartStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SparePartStockInfo::getWarehouseCode, sparePartStockInfo.getWarehouseCode());
        queryWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> list = sparePartStockInfoMapper.selectList(queryWrapper);
        if (!list.isEmpty() && list.get(0).equals(sparePartStockInfo.getId())) {
            return Result.error("备件仓库编号重复，请重新填写！");
        }
        //判断名称是否重复
        LambdaQueryWrapper<SparePartStockInfo> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SparePartStockInfo::getWarehouseName, sparePartStockInfo.getWarehouseName());
        nameWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> nameList = sparePartStockInfoMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && nameList.get(0).equals(sparePartStockInfo.getId())) {
            return Result.error("备件仓库名称重复，请重新填写！");
        }
        //判断一个仓库仅能所属一个机构
        LambdaQueryWrapper<SparePartStockInfo> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(SparePartStockInfo::getOrganizationId, sparePartStockInfo.getOrganizationId());
        deptWrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SparePartStockInfo> deptList = sparePartStockInfoMapper.selectList(deptWrapper);
        if (!deptList.isEmpty() && deptList.get(0).equals(sparePartStockInfo.getId())) {
            return Result.error("已存在该组织机构的备件仓库！");
        }
        sparePartStockInfoMapper.updateById(sparePartStockInfo);
        return Result.OK("编辑成功！");
    }
}
