package com.aiurt.boot.record.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
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
import com.aiurt.boot.record.vo.CheckResultTotalVO;
import com.aiurt.boot.record.vo.FixedAssetsCheckRecordVO;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
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
    @Autowired
    private IFixedAssetsService fixedAssetsService;
    @Autowired
    private IFixedAssetsCategoryService fixedAssetsCategoryService;
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
    public IPage<FixedAssetsCheckRecordVO> queryPageList(Page<FixedAssetsCheckRecordVO> page, FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO) {

        String checkId = fixedAssetsCheckRecordDTO.getCheckId();
        Page<FixedAssetsCheckRecordVO> pageList = null;
        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(checkId);
        if (FixedAssetsConstant.STATUS_0.equals(fixedAssetsCheck.getStatus())) {
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
        } else {
            LambdaQueryWrapper<FixedAssetsCheckRecord> wrapper = new LambdaQueryWrapper<>();
            if (ObjectUtils.isNotEmpty(fixedAssetsCheckRecordDTO)) {
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getCheckId()).ifPresent(checkid -> wrapper.eq(FixedAssetsCheckRecord::getCheckId, checkid));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetName()).ifPresent(assetName -> wrapper.like(FixedAssetsCheckRecord::getAssetName, assetName));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getResult()).ifPresent(result -> {
                    if (FixedAssetsConstant.CHECK_RESULT_0.equals(result)) {
                        wrapper.eq(FixedAssetsCheckRecord::getProfitLoss, 0);
                    } else if (FixedAssetsConstant.CHECK_RESULT_1.equals(result)) {
                        wrapper.gt(FixedAssetsCheckRecord::getProfitLoss, 0);
                    } else if (FixedAssetsConstant.CHECK_RESULT_2.equals(result)) {
                        wrapper.lt(FixedAssetsCheckRecord::getProfitLoss, 0);
                    }
                });
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getCategoryCode()).ifPresent(categoryCode -> wrapper.like(FixedAssetsCheckRecord::getCategoryCode, categoryCode));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetCode()).ifPresent(assetCode -> wrapper.like(FixedAssetsCheckRecord::getAssetCode, assetCode));
            }
            Page<FixedAssetsCheckRecord> recordPage = this.page(new Page<>(page.getCurrent(), page.getCurrent()), wrapper);
            List<FixedAssetsCheckRecord> records = recordPage.getRecords();
            List<FixedAssetsCheckRecordVO> recordVOs = new ArrayList<>();
            FixedAssetsCheckRecordVO checkRecordVO = null;
            for (FixedAssetsCheckRecord record : records) {
                checkRecordVO = new FixedAssetsCheckRecordVO();
                BeanUtils.copyProperties(record, checkRecordVO);
                recordVOs.add(checkRecordVO);
            }
            pageList = page.setRecords(recordVOs);
        }
        List<SysDepartModel> deptList = sysBaseApi.getAllSysDepart();
        Map<String, String> orgMap = deptList.stream()
                .collect(Collectors.toMap((k -> k.getOrgCode()), (v -> v.getDepartName()), (a, b) -> a));
        for (FixedAssetsCheckRecordVO record : pageList.getRecords()) {
            String position = sysBaseApi.getPosition(record.getLocation());
            FixedAssetsCategory category = fixedAssetsCategoryService.lambdaQuery()
                    .eq(FixedAssetsCategory::getCategoryCode, record.getCategoryCode())
                    .last("limit 1")
                    .one();
            String categoryName = Optional.ofNullable(category).orElseGet(FixedAssetsCategory::new).getCategoryName();
            record.setCategoryName(categoryName);
            record.setOrgName(orgMap.get(record.getOrgCode()));
            record.setLocationName(position);
        }
        return pageList;
    }

    @Override
    public List<FixedAssetsCheckRecordVO> nonsortList(FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO) {

        String checkId = fixedAssetsCheckRecordDTO.getCheckId();
        List<FixedAssetsCheckRecordVO> records = new ArrayList<>();
        FixedAssetsCheckRecordVO recordVO = null;
        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(checkId);
        if (FixedAssetsConstant.STATUS_0.equals(fixedAssetsCheck.getStatus())) {
            List<FixedAssetsCheckCategory> categoryList = fixedAssetsCheckCategoryService.lambdaQuery()
                    .eq(FixedAssetsCheckCategory::getCheckId, checkId).list();
            List<FixedAssetsCheckDept> deptList = fixedAssetsCheckDeptService.lambdaQuery()
                    .eq(FixedAssetsCheckDept::getCheckId, checkId).list();
            List<String> categoryCodes = categoryList.stream().map(FixedAssetsCheckCategory::getCategoryCode).collect(Collectors.toList());
            List<String> orgCodes = deptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());
            List<FixedAssets> fixedAssets = fixedAssetsService.lambdaQuery()
                    .eq(FixedAssets::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(FixedAssets::getOrgCode, orgCodes)
                    .in(FixedAssets::getCategoryCode, categoryCodes)
                    .list();
            for (FixedAssets fixedAsset : fixedAssets) {
                recordVO = new FixedAssetsCheckRecordVO();
                recordVO.setAssetCode(fixedAsset.getAssetCode());
                recordVO.setCategoryName(fixedAsset.getAssetName());
                recordVO.setLocation(fixedAsset.getLocation());
                recordVO.setCategoryCode(fixedAsset.getCategoryCode());
                recordVO.setNumber(fixedAsset.getNumber());
                recordVO.setAssetOriginal(fixedAsset.getAssetOriginal());
                if (StrUtil.isNotEmpty(fixedAsset.getCategoryCode())) {
                    FixedAssetsCategory category = fixedAssetsCategoryService.lambdaQuery()
                            .eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0)
                            .eq(FixedAssetsCategory::getCategoryCode, fixedAsset.getCategoryCode())
                            .last("limit 1")
                            .one();
                    if (ObjectUtils.isNotEmpty(category)) {
                        recordVO.setCategoryName(category.getCategoryName());
                    }
                }
                records.add(recordVO);
            }
        } else {
            LambdaQueryWrapper<FixedAssetsCheckRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FixedAssetsCheckRecord::getDelFlag, CommonConstant.DEL_FLAG_0);
            if (ObjectUtils.isNotEmpty(fixedAssetsCheckRecordDTO)) {
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getCheckId()).ifPresent(checkid -> wrapper.eq(FixedAssetsCheckRecord::getCheckId, checkid));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetName()).ifPresent(assetName -> wrapper.like(FixedAssetsCheckRecord::getAssetName, assetName));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getCategoryCode()).ifPresent(categoryCode -> wrapper.like(FixedAssetsCheckRecord::getCategoryCode, categoryCode));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getAssetCode()).ifPresent(assetCode -> wrapper.like(FixedAssetsCheckRecord::getAssetCode, assetCode));
                Optional.ofNullable(fixedAssetsCheckRecordDTO.getResult()).ifPresent(result -> {
                    if (FixedAssetsConstant.CHECK_RESULT_0.equals(result)) {
                        wrapper.eq(FixedAssetsCheckRecord::getProfitLoss, 0);
                    } else if (FixedAssetsConstant.CHECK_RESULT_1.equals(result)) {
                        wrapper.gt(FixedAssetsCheckRecord::getProfitLoss, 0);
                    } else if (FixedAssetsConstant.CHECK_RESULT_2.equals(result)) {
                        wrapper.lt(FixedAssetsCheckRecord::getProfitLoss, 0);
                    }
                });
            }
            List<FixedAssetsCheckRecord> list = this.list(wrapper);
            for (FixedAssetsCheckRecord record : list) {
                recordVO = new FixedAssetsCheckRecordVO();
                BeanUtils.copyProperties(record, recordVO);
                if (StrUtil.isNotEmpty(record.getCategoryCode())) {
                    FixedAssetsCategory category = fixedAssetsCategoryService.lambdaQuery()
                            .eq(FixedAssetsCategory::getDelFlag, CommonConstant.DEL_FLAG_0)
                            .eq(FixedAssetsCategory::getCategoryCode, record.getCategoryCode())
                            .last("limit 1")
                            .one();
                    if (ObjectUtils.isNotEmpty(category)) {
                        recordVO.setCategoryName(category.getCategoryName());
                    }
                }
                records.add(recordVO);
            }
        }
        List<SysDepartModel> deptList = sysBaseApi.getAllSysDepart();
        Map<String, String> orgMap = deptList.stream()
                .collect(Collectors.toMap((k -> k.getOrgCode()), (v -> v.getDepartName()), (a, b) -> a));
        for (FixedAssetsCheckRecordVO record : records) {
            String position = sysBaseApi.getPosition(record.getLocation());
            FixedAssetsCategory category = fixedAssetsCategoryService.lambdaQuery()
                    .eq(FixedAssetsCategory::getCategoryCode, record.getCategoryCode())
                    .last("limit 1")
                    .one();
            String categoryName = Optional.ofNullable(category).orElseGet(FixedAssetsCategory::new).getCategoryName();
            record.setCategoryName(categoryName);
            record.setOrgName(orgMap.get(record.getOrgCode()));
            record.setLocationName(position);
        }
        return records;
    }

    @Override
    public CheckResultTotalVO checkResultTotal(String id) {
        FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(id);
        Assert.notNull(fixedAssetsCheck, "不存在该盘点任务数据！");
        CheckResultTotalVO totalVO = new CheckResultTotalVO();
        if (!FixedAssetsConstant.STATUS_3.equals(fixedAssetsCheck.getStatus())) {
            return totalVO;
        }
        Long profit = this.lambdaQuery().eq(FixedAssetsCheckRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(FixedAssetsCheckRecord::getCheckId, id)
                .gt(FixedAssetsCheckRecord::getProfitLoss, 0)
                .count();
        Long loss = this.lambdaQuery().eq(FixedAssetsCheckRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(FixedAssetsCheckRecord::getCheckId, id)
                .lt(FixedAssetsCheckRecord::getProfitLoss, 0)
                .count();
        Long equality = this.lambdaQuery().eq(FixedAssetsCheckRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(FixedAssetsCheckRecord::getCheckId, id)
                .eq(FixedAssetsCheckRecord::getProfitLoss, 0)
                .count();
        totalVO.setProfit(profit);
        totalVO.setLoss(loss);
        totalVO.setEquality(equality);
        return totalVO;
    }
}
