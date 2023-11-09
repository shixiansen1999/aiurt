package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.aiurt.modules.sparepart.mapper.SparePartMalfunctionMapper;
import com.aiurt.modules.sparepart.service.ISparePartMalfunctionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: spare_part_malfunction
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartMalfunctionServiceImpl extends ServiceImpl<SparePartMalfunctionMapper, SparePartMalfunction> implements ISparePartMalfunctionService {
    @Autowired
    private SparePartMalfunctionMapper sparePartMalfunctionMapper;
    @Autowired
    private IDeviceChangeSparePartService deviceChangeSparePartService;

    /**
     * 查询列表
     * @param
     * @param sparePartMalfunction
     * @return
     */
    @Override
    public List<SparePartMalfunction> selectList(SparePartMalfunction sparePartMalfunction){
        return sparePartMalfunctionMapper.readAll(sparePartMalfunction);
    }

    @Override
    public IPage<SparePartMalfunction> pageList(Page<SparePartMalfunction> page, SparePartMalfunction sparePartMalfunction) {
        LambdaQueryWrapper<SparePartMalfunction> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<DeviceChangeSparePart> changeSparePartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 根据处置记录id查询故障更换附件表信息
        String scrapId = sparePartMalfunction.getScrapId();
        if (StrUtil.isNotBlank(scrapId)) {
            List<String> outIds = new ArrayList<>();
            changeSparePartLambdaQueryWrapper.eq(DeviceChangeSparePart::getScrapId, scrapId);
            List<DeviceChangeSparePart> deviceChangeSpareParts = deviceChangeSparePartService.list(changeSparePartLambdaQueryWrapper);
            // 两种情况的出库单id
            List<String> borrowIds = deviceChangeSpareParts.stream().map(DeviceChangeSparePart::getBorrowingOutOrderId).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            List<String> lendIds = deviceChangeSpareParts.stream().map(DeviceChangeSparePart::getLendOutOrderId).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(borrowIds)) {
                outIds.addAll(borrowIds);
            }
            if (CollUtil.isNotEmpty(lendIds)) {
                outIds.addAll(lendIds);
            }
            // 根据处置记录id查询备件故障记录
            if (CollUtil.isNotEmpty(outIds)) {
                queryWrapper.in(SparePartMalfunction::getOutOrderId, outIds);
            } else {
                return page;
            }
        }
        // 根据故障设备编号查询
        String malfunctionDeviceCode = sparePartMalfunction.getMalfunctionDeviceCode();
        if (StrUtil.isNotBlank(malfunctionDeviceCode)) {
            queryWrapper.eq(SparePartMalfunction::getMalfunctionDeviceCode, malfunctionDeviceCode);
        }
        if(ObjectUtils.isNotEmpty(sparePartMalfunction.getMaintainTimeBegin()) && ObjectUtils.isNotEmpty(sparePartMalfunction.getMaintainTimeEnd())){
            queryWrapper.ge(SparePartMalfunction::getMaintainTime,sparePartMalfunction.getMaintainTimeBegin()+" 00:00:00");
            queryWrapper.le(SparePartMalfunction::getMaintainTime,sparePartMalfunction.getMaintainTimeEnd()+" 23:59:59");
        }
        if(ObjectUtils.isNotEmpty(sparePartMalfunction.getOutOrderId())){
            queryWrapper.eq(SparePartMalfunction::getOutOrderId,sparePartMalfunction.getOutOrderId());
        }
        return this.page(page, queryWrapper);
    }
}
