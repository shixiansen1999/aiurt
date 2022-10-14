package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.mapper.CsManufactorMapper;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Slf4j
@Service
public class SparePartStockInfoServiceImpl extends ServiceImpl<SparePartStockInfoMapper, SparePartStockInfo> implements ISparePartStockInfoService {
    @Autowired
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Autowired
    private ISysDepartService iSysDepartService;

    @Autowired
    private ISysBaseAPI sysBaseApi;
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
        String organizationId = sparePartStockInfo.getOrganizationId();
        SysDepart sysDepart = iSysDepartService.getById(organizationId);
        sparePartStockInfo.setOrgCode(sysDepart.getOrgCode());
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
        String organizationId = sparePartStockInfo.getOrganizationId();
        SysDepart sysDepart = iSysDepartService.getById(organizationId);
        sparePartStockInfo.setOrgCode(sysDepart.getOrgCode());
        sparePartStockInfoMapper.updateById(sparePartStockInfo);
        return Result.OK("编辑成功！");
    }

    /**
     * 根据用户名查询管理的仓库
     *
     * @param userName
     * @return
     */
    @Override
    public SparePartStockInfo getSparePartStockInfoByUserName(String userName) {
        if (StrUtil.isBlank(userName)) {
            return null;
        }
        LoginUser loginUser = sysBaseApi.getUserByName(userName);

        if (Objects.isNull(loginUser)) {
            return null;
        }

        String orgId = loginUser.getOrgId();

        if (StrUtil.isBlank(orgId)) {
            log.info("该用户没绑定机构：{}-{}", loginUser.getRealname(), loginUser.getUsername());
            return null;
        }
        // 查询仓库
        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId, orgId).last("limit 1");
        SparePartStockInfo stockInfo = baseMapper.selectOne(wrapper);
        return stockInfo;
    }
}
