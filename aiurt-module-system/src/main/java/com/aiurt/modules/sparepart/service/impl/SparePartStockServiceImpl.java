package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartStatistics;
import com.aiurt.modules.sparepart.mapper.SparePartLendStockMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
@Service
public class SparePartStockServiceImpl extends ServiceImpl<SparePartStockMapper, SparePartStock> implements ISparePartStockService {
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartLendStockMapper sparePartLendStockMapper;
    @Autowired
    private IMaterialBaseTypeService iMaterialBaseTypeService;
    @Autowired
    private CommonAPI commonAPI;
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    @Override
    public List<SparePartStock> selectList(Page page, SparePartStock sparePartStock){
        return sparePartStockMapper.readAll(page,sparePartStock);
    }
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    @Override
    public List<SparePartStock> selectLendList(Page page, SparePartStock sparePartStock){
        return sparePartLendStockMapper.readAll(page,sparePartStock);
    }

    @Override
    public List<SparePartStatistics> selectSparePartStatistics(Page page, SparePartStatistics sparePartStatistics) {

        //获取登录的用户信息
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据用户id查询对应的子系统
        List<SparePartStatistics> subsystemByUserId = sparePartStockMapper.getSubsystemByUserId(page, user.getId());


        //拷贝子系统信息到新的实体
        List<SparePartStatistics> sparePartStatisticsList = new ArrayList<>();
        if (CollUtil.isNotEmpty(subsystemByUserId)){
            subsystemByUserId.forEach(e->{
                SparePartStatistics sparePartStatistics1 = new SparePartStatistics();
                BeanUtil.copyProperties(e,sparePartStatistics1);
                sparePartStatisticsList.add(sparePartStatistics1);
            });
        }

        //查询子系统和所对应的物资类型
        List<MaterialBaseType> materialBaseTypeList = iMaterialBaseTypeService.list(new LambdaQueryWrapper<MaterialBaseType>().eq(MaterialBaseType::getDelFlag,0));
        List<MaterialBaseType> materialBaseTypeLitres = iMaterialBaseTypeService.treeList(materialBaseTypeList,"0");
        if (CollUtil.isNotEmpty(sparePartStatisticsList)){
            sparePartStatisticsList.forEach(e->{
                //所属系统的二级库库存
                Long aLong = sparePartStockMapper.stockCount(e.getSystemCode(), null);
                //所属系统的三级库库存
                Long aLong1 = sparePartStockMapper.sparePartCount(e.getSystemCode(), null);
                e.setTwoCount(aLong);
                e.setThreeCount(aLong1);
                List<MaterialBaseType> collect = materialBaseTypeLitres.stream().filter(materialBaseType -> e.getSystemCode().equals(materialBaseType.getSystemCode())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)){
                    collect.forEach(q->{
                        //物资类型的二级库库存
                        Long aLong2 = sparePartStockMapper.stockCount(null, q.getBaseTypeCode());
                        //物资类型的三级库库存
                        Long aLong3 = sparePartStockMapper.sparePartCount(null, q.getBaseTypeCode());
                        e.setTwoCount(aLong2);
                        e.setThreeCount(aLong3);
                    });
                    e.setMaterialBaseTypeList(collect);
                }
            });
        }
        return sparePartStatisticsList;
    }

}
