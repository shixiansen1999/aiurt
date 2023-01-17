package com.aiurt.boot.record.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.aiurt.boot.check.entity.FixedAssetsCheckDept;
import com.aiurt.boot.check.service.IFixedAssetsCheckCategoryService;
import com.aiurt.boot.check.service.IFixedAssetsCheckDeptService;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.boot.constant.FixedAssetsConstant;
import com.aiurt.boot.record.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.mapper.FixedAssetsCheckRecordMapper;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsCheckRecordServiceImpl extends ServiceImpl<FixedAssetsCheckRecordMapper, FixedAssetsCheckRecord> implements IFixedAssetsCheckRecordService {

    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Lazy
    @Autowired
    private IFixedAssetsCheckService fixedAssetsCheckService;
    @Lazy
    @Autowired
    private IFixedAssetsCheckCategoryService fixedAssetsCheckCategoryService;
    @Lazy
    @Autowired
    private IFixedAssetsCheckDeptService fixedAssetsCheckDeptService;
    @Autowired
    private FixedAssetsCheckRecordMapper fixedAssetsCheckRecordMapper;

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

        String checkId = fixedAssetsCheckRecordDTO.getCheckId();
        Page<FixedAssetsCheckRecord> pageList = null;
        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(checkId);
        if (FixedAssetsConstant.status_0.equals(fixedAssetsCheck.getStatus())) {
            List<FixedAssetsCheckCategory> categoryList = fixedAssetsCheckCategoryService.lambdaQuery()
                    .eq(FixedAssetsCheckCategory::getCheckId, checkId).list();
            List<String> categoryCodes = categoryList.stream().map(FixedAssetsCheckCategory::getCategoryCode).collect(Collectors.toList());
            List<FixedAssetsCheckDept> deptList = fixedAssetsCheckDeptService.lambdaQuery()
                    .eq(FixedAssetsCheckDept::getCheckId, checkId).list();
            List<String> orgCodes = deptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(categoryList) || CollectionUtil.isEmpty(orgCodes)) {
                return page;
            }
            pageList = fixedAssetsCheckRecordMapper.pageList(page, fixedAssetsCheckRecordDTO, categoryCodes, orgCodes);
            return pageList;
        }
        pageList = this.page(page, wrapper);

        return pageList;
    }

    @Override
    public List<FixedAssetsCheckRecord> nonsortList(FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO) {
        LambdaQueryWrapper<FixedAssetsCheckRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FixedAssetsCheckRecord::getDelFlag, CommonConstant.DEL_FLAG_0);
        Map<String, String> orgMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(fixedAssetsCheckRecordDTO)) {
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getCheckId()).ifPresent(checkId -> wrapper.eq(FixedAssetsCheckRecord::getCheckId, checkId));
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetName()).ifPresent(assetName -> wrapper.like(FixedAssetsCheckRecord::getAssetName, assetName));
//            Optional.ofNullable(fixedAssetsCheckRecordDTO.getResult()).ifPresent(result -> wrapper.like(FixedAssetsCheckRecord::getre, assetCode));
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getCategoryCode()).ifPresent(categoryCode -> wrapper.like(FixedAssetsCheckRecord::getCategoryCode, categoryCode));
            Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetCode()).ifPresent(assetCode -> wrapper.like(FixedAssetsCheckRecord::getAssetCode, assetCode));
        }
        List<FixedAssetsCheckRecord> list = this.list(wrapper);
        list.stream().forEach(l -> {
            String orgCode = l.getOrgCode();
            String orgName = orgMap.get(orgCode);
            if (StrUtil.isEmpty(orgName) && StrUtil.isNotEmpty(orgCode)) {
                orgName = sysBaseApi.getDepartNameByOrgCode(orgCode);
                Optional.ofNullable(orgName).ifPresent(name -> orgMap.put(orgCode, name));
            }
            // todo set组织机构名称
            // l.setOrgName(orgName);

        });
        return list;
    }
}
