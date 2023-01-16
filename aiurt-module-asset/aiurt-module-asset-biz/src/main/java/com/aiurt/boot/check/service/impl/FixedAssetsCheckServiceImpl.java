package com.aiurt.boot.check.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
import com.aiurt.boot.check.dto.AssetsResultDTO;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.mapper.FixedAssetsCheckMapper;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.boot.constant.FixedAssetsConstant;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Service
public class FixedAssetsCheckServiceImpl extends ServiceImpl<FixedAssetsCheckMapper, FixedAssetsCheck> implements IFixedAssetsCheckService, IFlowableBaseUpdateStatusService {
    @Autowired
    private IFixedAssetsCheckRecordService fixedAssetsCheckRecordService;
    @Autowired
    private IFixedAssetsService fixedAssetsService;
    @Autowired
    private IFixedAssetsCategoryService fixedAssetsCategoryService;

    @Override
    public IPage<FixedAssetsCheck> queryPageList(Page<FixedAssetsCheck> page, FixedAssetsCheck fixedAssetsCheck) {
        Page<FixedAssetsCheck> fixedAssetsCheckPage = baseMapper.selectPageList(page, fixedAssetsCheck);
        fixedAssetsCheckPage.getRecords().forEach(f -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            f.setTime(format.format(f.getPlanStartDate()) + "至" + format.format(f.getPlanEndDate()));
            List<String> orgName = baseMapper.selectOrgName(Arrays.asList(f.getOrgCode().split(",")));
            f.setOrgName(String.join(",", orgName));
            List<String> categoryName = baseMapper.selectCategoryName(Arrays.asList(f.getCategoryCode().split(",")));
            f.setCategoryName(String.join(",", categoryName));
            if (f.getStatus() > 2) {
                List<FixedAssetsCheckRecord> list = fixedAssetsCheckRecordService.lambdaQuery().eq(FixedAssetsCheckRecord::getCheckId, f.getId())
                        .eq(FixedAssetsCheckRecord::getDelFlag, 0).list();
                f.setNumber(list.stream().map(e -> e.getActualNumber()).reduce(Integer::sum).get());
            }
        });
        return fixedAssetsCheckPage;
    }

    @Override
    public List<FixedAssets> queryInventoryResults(String orgCodes, String categoryCodes, String id) {
        List<FixedAssetsCheckRecord> fixedAssetsCheckRecords = fixedAssetsCheckRecordService.lambdaQuery()
                .eq(FixedAssetsCheckRecord::getCheckId, id).eq(FixedAssetsCheckRecord::getDelFlag, 0).list();
        List<FixedAssets> fixedAssets = new ArrayList<>();
        //
        if (CollectionUtil.isNotEmpty(fixedAssetsCheckRecords)) {
            fixedAssetsCheckRecords.forEach(t -> {
                FixedAssets fixedAssets1 = fixedAssetsService.lambdaQuery().eq(FixedAssets::getAssetCode, t.getAssetCode())
                        .eq(FixedAssets::getStatus, 1).one();
                fixedAssets1.setActualNumber(t.getActualNumber());
                fixedAssets1.setNum(fixedAssets1.getNumber() - (fixedAssets1.getActualNumber() == null ? 0 : fixedAssets1.getActualNumber()));
                fixedAssets.add(fixedAssets1);
            });
        } else {
            fixedAssets.addAll(fixedAssetsService.lambdaQuery()
                    .in(FixedAssets::getOrgCode, Arrays.asList(orgCodes.split(",")))
                    .in(FixedAssets::getCategoryCode, Arrays.asList(categoryCodes.split(",")))
                    .eq(FixedAssets::getStatus, 1).list());
        }
        return fixedAssets;
    }

    @Override
    public List<FixedAssetsCategory> queryBySpinner(String orgCodes) {
        List<FixedAssets> fixedAssets = fixedAssetsService.lambdaQuery()
                .in(FixedAssets::getOrgCode, Arrays.asList(orgCodes.split(",")))
                .eq(FixedAssets::getStatus, 1).list();
        List<String> collect = fixedAssets.stream().map(f -> f.getCategoryCode()).distinct().collect(Collectors.toList());
        List<FixedAssetsCategory> fixedAssetsCategories = fixedAssetsCategoryService.lambdaQuery()
                .in(FixedAssetsCategory::getCategoryCode, collect).eq(FixedAssetsCategory::getDelFlag, 0).list();
        return fixedAssetsCategories;
    }

    @Override
    public void updateStatus(String id, Integer status, Integer num) {
        //修改状态判断是否为执行中
        if (ObjectUtils.isNotEmpty(num)) {
            //判断为提交的时候修改状态为已完成
            if (num == 1) {
                FixedAssetsCheck fixedAssetsCheck = new FixedAssetsCheck().setId(id).setStatus(status + 1);
                baseMapper.updateById(fixedAssetsCheck);
            } else {
                FixedAssetsCheck fixedAssetsCheck = new FixedAssetsCheck().setId(id).setStatus(2);
                baseMapper.updateById(fixedAssetsCheck);
            }
        } else {
            FixedAssetsCheck fixedAssetsCheck = new FixedAssetsCheck().setId(id).setStatus(status + 1);
            baseMapper.updateById(fixedAssetsCheck);
        }
    }

    @Override
    public void addInventoryResults(FixedAssetsCheck fixedAssetsCheck) {
        fixedAssetsCheck.getFixedAssetsList().forEach(f -> {
            FixedAssetsCheckRecord fixedAssetsCheckRecord = fixedAssetsCheckRecordService.lambdaQuery()
                    .eq(FixedAssetsCheckRecord::getAssetCode, f.getAssetCode()).eq(FixedAssetsCheckRecord::getDelFlag, 0).one();
            //为空是还未做过保存做添加 保存过做更新
            if (ObjectUtils.isNotEmpty(fixedAssetsCheckRecord)) {
                BeanUtils.copyProperties(fixedAssetsCheckRecord, f);
                fixedAssetsCheckRecordService.updateById(fixedAssetsCheckRecord);
            } else {
                BeanUtils.copyProperties(fixedAssetsCheckRecord, f);
                fixedAssetsCheckRecord.setCheckId(fixedAssetsCheck.getId());
                fixedAssetsCheckRecordService.save(fixedAssetsCheckRecord);
            }
        });

    }

    @Override
    public void addInventoryResultsBySubmit(FixedAssetsCheck fixedAssetsCheck) {
        //为一是已提交 前端不继续做保存
        if (fixedAssetsCheck.getIsSubmit() == 1) {
            baseMapper.updateById(fixedAssetsCheck);
        }
        fixedAssetsCheck.getFixedAssetsList().forEach(f -> {
            FixedAssetsCheckRecord fixedAssetsCheckRecord = fixedAssetsCheckRecordService.lambdaQuery()
                    .eq(FixedAssetsCheckRecord::getAssetCode, f.getAssetCode()).eq(FixedAssetsCheckRecord::getDelFlag, 0).one();
            BeanUtils.copyProperties(fixedAssetsCheckRecord, f);
            fixedAssetsCheckRecord.setCheckId(fixedAssetsCheck.getId());
            fixedAssetsCheckRecordService.save(fixedAssetsCheckRecord);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issued(String id) {
        FixedAssetsCheck fixedAssetsCheck = this.getById(id);
        Assert.notNull(fixedAssetsCheck, "未找到对应数据！");
        // 更新为待执行状态
        if (ObjectUtils.isEmpty(fixedAssetsCheck.getStatus())
                || FixedAssetsConstant.status_0.equals(fixedAssetsCheck.getStatus())) {
            throw new AiurtBootException("请检查任务状态，待下发状态才允许下发！");
        }
        fixedAssetsCheck.setStatus(FixedAssetsConstant.status_1);
        this.updateById(fixedAssetsCheck);

        // 生成盘点结果
        String orgCode = fixedAssetsCheck.getOrgCode();
        String categoryCode = fixedAssetsCheck.getCategoryCode();
        if (StrUtil.isNotBlank(orgCode) && StrUtil.isNotEmpty(categoryCode)) {
            List<String> orgCodes = StrUtil.split(orgCode, ',');
            List<FixedAssets> fixedAssets = fixedAssetsService.lambdaQuery()
                    .eq(FixedAssets::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(FixedAssets::getCategoryCode, categoryCode)
                    .in(FixedAssets::getOrgCode, orgCodes)
                    .list();
            List<FixedAssetsCheckRecord> records = new ArrayList<>();
            FixedAssetsCheckRecord checkRecord = null;
            for (FixedAssets assets : fixedAssets) {
                checkRecord = new FixedAssetsCheckRecord();
                checkRecord.setCheckId(id);
                checkRecord.setAssetCode(assets.getAssetCode());
                checkRecord.setAssetName(assets.getAssetName());
                checkRecord.setLocation(assets.getLocation());
                checkRecord.setOrgCode(assets.getOrgCode());
                checkRecord.setCategoryCode(assets.getCategoryCode());
                checkRecord.setNumber(assets.getNumber());
                records.add(checkRecord);
            }
            if (CollectionUtil.isNotEmpty(records)) {
                fixedAssetsCheckRecordService.saveBatch(records);
            }
        }
    }

    /**
     * 固定资产盘点管理-更新盘点结果数据记录(保存/提交)
     *
     * @param assetsResultDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startProcess(AssetsResultDTO assetsResultDTO) {
        String id = assetsResultDTO.getId();
        List<FixedAssetsCheckRecord> records = assetsResultDTO.getRecords();
        if (ObjectUtils.isEmpty(assetsResultDTO) || CollectionUtil.isEmpty(records)) {
            throw new AiurtBootException("盘点数据为空！");
        }
        if (StrUtil.isEmpty(assetsResultDTO.getId())) {
            throw new AiurtBootException("盘点任务的记录主键为空！");
        }
        FixedAssetsCheck fixedAssetsCheck = this.getById(id);
        Assert.notNull(fixedAssetsCheck, "未找到ID为:" + id + "盘点任务数据！");
        fixedAssetsCheck.setActualStartTime(assetsResultDTO.getActualStartTime());
        fixedAssetsCheck.setActualEndTime(assetsResultDTO.getActualEndTime());
        this.updateById(fixedAssetsCheck);

        fixedAssetsCheckRecordService.updateBatchById(records);
        return id;
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {

    }
}
