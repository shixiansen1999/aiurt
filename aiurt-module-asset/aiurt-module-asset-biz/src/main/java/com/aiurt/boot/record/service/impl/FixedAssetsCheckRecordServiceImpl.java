package com.aiurt.boot.record.service.impl;

import com.aiurt.boot.record.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.mapper.FixedAssetsCheckRecordMapper;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Optional;

/**
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsCheckRecordServiceImpl extends ServiceImpl<FixedAssetsCheckRecordMapper, FixedAssetsCheckRecord> implements IFixedAssetsCheckRecordService {

    @Override
    public IPage<FixedAssetsCheckRecord> queryPageList(Page<FixedAssetsCheckRecord> page, FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO) {
        LambdaQueryWrapper<FixedAssetsCheckRecord> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotEmpty(fixedAssetsCheckRecordDTO)) {
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getCheckId()).ifPresent(checkId -> wrapper.eq(FixedAssetsCheckRecord::getCheckId, checkId));
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetName()).ifPresent(assetName -> wrapper.like(FixedAssetsCheckRecord::getAssetName, assetName));
//            Optional.ofNullable(fixedAssetsCheckRecordDTO.getResult()).ifPresent(result -> wrapper.like(FixedAssetsCheckRecord::getre, assetCode));
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getCategoryCode()).ifPresent(categoryCode -> wrapper.like(FixedAssetsCheckRecord::getCategoryCode, categoryCode));
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetCode()).ifPresent(assetCode -> wrapper.like(FixedAssetsCheckRecord::getAssetCode, assetCode));
        }
        Page<FixedAssetsCheckRecord> pageList = this.page(page, wrapper);
        return pageList;
    }
}
