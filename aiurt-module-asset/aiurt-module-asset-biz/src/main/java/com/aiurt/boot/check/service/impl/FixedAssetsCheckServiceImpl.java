package com.aiurt.boot.check.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.boot.asset.service.IFixedAssetsService;
import com.aiurt.boot.category.entity.FixedAssetsCategory;
import com.aiurt.boot.category.service.IFixedAssetsCategoryService;
import com.aiurt.boot.check.dto.AssetsResultDTO;
import com.aiurt.boot.check.dto.FixedAssetsCheckDTO;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.aiurt.boot.check.entity.FixedAssetsCheckDept;
import com.aiurt.boot.check.entity.FixedAssetsCheckDetail;
import com.aiurt.boot.check.mapper.FixedAssetsCheckCategoryMapper;
import com.aiurt.boot.check.mapper.FixedAssetsCheckMapper;
import com.aiurt.boot.check.service.IFixedAssetsCheckCategoryService;
import com.aiurt.boot.check.service.IFixedAssetsCheckDeptService;
import com.aiurt.boot.check.service.IFixedAssetsCheckDetailService;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.boot.check.vo.CheckUserVO;
import com.aiurt.boot.check.vo.FixedAssetsCheckVO;
import com.aiurt.boot.constant.FixedAssetsConstant;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
@Slf4j
@Service
public class FixedAssetsCheckServiceImpl extends ServiceImpl<FixedAssetsCheckMapper, FixedAssetsCheck> implements IFixedAssetsCheckService, IFlowableBaseUpdateStatusService {

    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IFixedAssetsCheckRecordService fixedAssetsCheckRecordService;
    @Autowired
    private IFixedAssetsService fixedAssetsService;
    @Autowired
    private IFixedAssetsCategoryService fixedAssetsCategoryService;
    @Autowired
    private IFixedAssetsCheckCategoryService fixedAssetsCheckCategoryService;
    @Autowired
    private IFixedAssetsCheckDeptService fixedAssetsCheckDeptService;
    @Lazy
    @Autowired
    private IFixedAssetsCheckDetailService fixedAssetsCheckDetailService;
    @Autowired
    private FixedAssetsCheckCategoryMapper fixedAssetsCheckCategoryMapper;
    @Autowired
    private FixedAssetsCheckMapper fixedAssetsCheckMapper;

    @Override
    public IPage<FixedAssetsCheck> queryPageList(Page<FixedAssetsCheck> page, FixedAssetsCheck fixedAssetsCheck) {
        Page<FixedAssetsCheck> fixedAssetsCheckPage = baseMapper.selectPageList(page, fixedAssetsCheck);
//        fixedAssetsCheckPage.getRecords().forEach(f -> {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            f.setTime(format.format(f.getPlanStartDate()) + "至" + format.format(f.getPlanEndDate()));
//            List<String> orgName = baseMapper.selectOrgName(Arrays.asList(f.getOrgCode().split(",")));
//            f.setOrgName(String.join(",", orgName));
//            List<String> categoryName = baseMapper.selectCategoryName(Arrays.asList(f.getCategoryCode().split(",")));
//            f.setCategoryName(String.join(",", categoryName));
//            if (f.getStatus() > 2) {
//                List<FixedAssetsCheckRecord> list = fixedAssetsCheckRecordService.lambdaQuery().eq(FixedAssetsCheckRecord::getCheckId, f.getId())
//                        .eq(FixedAssetsCheckRecord::getDelFlag, 0).list();
//                f.setNumber(list.stream().map(e -> e.getActualNumber()).reduce(Integer::sum).get());
//            }
//        });
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
                || !FixedAssetsConstant.STATUS_0.equals(fixedAssetsCheck.getStatus())) {
            throw new AiurtBootException("请检查任务状态，待下发状态才允许下发！");
        }
        fixedAssetsCheck.setStatus(FixedAssetsConstant.STATUS_1);
        this.updateById(fixedAssetsCheck);

        // 生成盘点结果
        List<FixedAssetsCheckCategory> categoryList = fixedAssetsCheckCategoryService.lambdaQuery()
                .eq(FixedAssetsCheckCategory::getCheckId, id)
                .list();
        List<FixedAssetsCheckDept> deptList = fixedAssetsCheckDeptService.lambdaQuery()
                .eq(FixedAssetsCheckDept::getCheckId, id).list();
        List<String> categoryCodes = categoryList.stream().map(FixedAssetsCheckCategory::getCategoryCode).collect(Collectors.toList());
        List<String> orgCodes = deptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(categoryCodes) || CollectionUtil.isEmpty(orgCodes)) {
            throw new AiurtBootException("所属的物资分类和组织机构中暂无需要盘点的数据！");
        }
        List<FixedAssets> fixedAssets = fixedAssetsService.lambdaQuery()
                .eq(FixedAssets::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(FixedAssets::getStatus,FixedAssetsConstant.STATUS_1)
                .in(FixedAssets::getCategoryCode, categoryCodes)
                .in(FixedAssets::getOrgCode, orgCodes)
                .list();
        List<FixedAssetsCheckRecord> records = new ArrayList<>();
        List<FixedAssetsCheckDetail> details = new ArrayList<>();
        FixedAssetsCheckRecord checkRecord = null;
        FixedAssetsCheckDetail detail = null;
        for (FixedAssets assets : fixedAssets) {
            checkRecord = new FixedAssetsCheckRecord();
            checkRecord.setCheckId(id);
            checkRecord.setAssetCode(assets.getAssetCode());
            checkRecord.setAssetName(assets.getAssetName());
            checkRecord.setLocation(assets.getLocation());
            checkRecord.setOrgCode(assets.getOrgCode());
            checkRecord.setCategoryCode(assets.getCategoryCode());
            checkRecord.setNumber(assets.getNumber());
            checkRecord.setAssetOriginal(assets.getAssetOriginal());
            records.add(checkRecord);

            detail = new FixedAssetsCheckDetail();
            detail.setCheckId(id);
            detail.setAssetCode(assets.getAssetCode());
            detail.setAssetName(assets.getAssetName());
            detail.setLocation(assets.getLocation());
            detail.setCategoryCode(assets.getCategoryCode());
            detail.setBeforeNumber(assets.getNumber());
            details.add(detail);
        }
        if (CollectionUtil.isEmpty(records)) {
            throw new AiurtBootException("所属的物资分类和组织机构中暂无需要盘点的数据！");
        }
        fixedAssetsCheckRecordService.saveBatch(records);
        fixedAssetsCheckDetailService.saveBatch(details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveCheckInfo(FixedAssetsCheck fixedAssetsCheck) {
        this.save(fixedAssetsCheck);
        this.saveOrgAndCategoryCode(fixedAssetsCheck);
        return fixedAssetsCheck.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String editCheckInfo(FixedAssetsCheck fixedAssetsCheck) {
        String id = fixedAssetsCheck.getId();
        Assert.notNull(id, "记录ID不能为空！");
        FixedAssetsCheck check = this.getById(id);
        Assert.notNull(check, "未找到对应数据！");
        this.updateById(fixedAssetsCheck);

        fixedAssetsCheckCategoryService.remove(new LambdaQueryWrapper<FixedAssetsCheckCategory>().eq(FixedAssetsCheckCategory::getCheckId, id));
        fixedAssetsCheckDeptService.remove(new LambdaQueryWrapper<FixedAssetsCheckDept>().eq(FixedAssetsCheckDept::getCheckId, id));
        this.saveOrgAndCategoryCode(fixedAssetsCheck);
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCheckInfo(String id) {
        FixedAssetsCheck fixedAssetsCheck = this.getById(id);
        Assert.notNull(fixedAssetsCheck, "未找到对应数据！");
        if (!FixedAssetsConstant.STATUS_0.equals(fixedAssetsCheck.getStatus())) {
            throw new AiurtBootException("待下发状态的记录才允许删除！");
        }
        this.removeById(id);
        fixedAssetsCheckCategoryService.remove(new LambdaQueryWrapper<FixedAssetsCheckCategory>().eq(FixedAssetsCheckCategory::getCheckId, id));
        fixedAssetsCheckDeptService.remove(new LambdaQueryWrapper<FixedAssetsCheckDept>().eq(FixedAssetsCheckDept::getCheckId, id));

    }

    @Override
    public FixedAssetsCheckVO getCheckInfo(String id) {
        FixedAssetsCheck fixedAssetsCheck = this.getById(id);
        Assert.notNull(fixedAssetsCheck, "未找到对应数据！");
        FixedAssetsCheckVO checkVO = new FixedAssetsCheckVO();
        BeanUtils.copyProperties(fixedAssetsCheck, checkVO);

        List<FixedAssetsCheckCategory> categorys = fixedAssetsCheckCategoryMapper.getCategoryList(id);
        List<FixedAssetsCheckDept> depts = fixedAssetsCheckDeptService.lambdaQuery()
                .eq(FixedAssetsCheckDept::getCheckId, id).list();
        depts.forEach(dept -> {
            if (StrUtil.isNotEmpty(dept.getOrgCode())) {
                dept.setOrgName(sysBaseApi.getDepartNameByOrgCode(dept.getOrgCode()));
            }
        });

        checkVO.setCategorys(categorys);
        checkVO.setDepts(depts);
        return checkVO;
    }

    @Override
    public IPage<FixedAssetsCheckVO> pageList(Page<FixedAssetsCheckVO> page, FixedAssetsCheckDTO fixedAssetsCheckDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        fixedAssetsCheckDTO = Optional.ofNullable(fixedAssetsCheckDTO).orElseGet(FixedAssetsCheckDTO::new);
        fixedAssetsCheckDTO.setAuditStatus(FixedAssetsConstant.STATUS_2);
        fixedAssetsCheckDTO.setUserName(loginUser.getUsername());
        IPage<FixedAssetsCheckVO> pageList = fixedAssetsCheckMapper.pageList(page, fixedAssetsCheckDTO);
        if (CollectionUtil.isNotEmpty(pageList.getRecords())) {
            Map<String, String> orgMap = sysBaseApi.getAllSysDepart().stream()
                    .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
            pageList.getRecords().forEach(l -> {
                String id = l.getId();
                List<FixedAssetsCheckCategory> categorys = fixedAssetsCheckCategoryMapper.getCategoryList(id);
                List<FixedAssetsCheckDept> depts = fixedAssetsCheckDeptService.lambdaQuery()
                        .eq(FixedAssetsCheckDept::getCheckId, id).list();
                depts.forEach(dept -> {
                    if (StrUtil.isNotEmpty(dept.getOrgCode())) {
                        dept.setOrgName(orgMap.get(dept.getOrgCode()));
                    }
                });
                l.setCategorys(categorys);
                l.setDepts(depts);
            });
        }
        return pageList;

    }

    @Override
    public List<CheckUserVO> checkUserInfo() {
        List<CheckUserVO> checkUserInfo = fixedAssetsCheckMapper.checkUserInfo();
        checkUserInfo.forEach(check -> {
            LoginUser user = sysBaseApi.getUserById(check.getCheckId());
            Optional.ofNullable(user).ifPresent(u -> check.setCheckName(u.getRealname()));
        });
        return checkUserInfo;
    }

    /**
     * 保存任务的组织机构编码和物资分类编码
     *
     * @param fixedAssetsCheck
     */
    private void saveOrgAndCategoryCode(FixedAssetsCheck fixedAssetsCheck) {
        String id = fixedAssetsCheck.getId();
        String categoryCode = fixedAssetsCheck.getCategoryCode();
        String orgCode = fixedAssetsCheck.getOrgCode();
        if (StrUtil.isNotEmpty(categoryCode)) {
            List<String> codes = StrUtil.split(categoryCode, ',');
            List<FixedAssetsCheckCategory> list = new ArrayList<>();
            FixedAssetsCheckCategory category = null;
            for (String code : codes) {
                category = new FixedAssetsCheckCategory();
                category.setCheckId(id);
                category.setCategoryCode(code);
                list.add(category);
            }
            if (CollectionUtil.isNotEmpty(list)) {
                fixedAssetsCheckCategoryService.saveBatch(list);
            }
        }
        if (StrUtil.isNotEmpty(orgCode)) {
            List<String> codes = StrUtil.split(orgCode, ',');
            List<FixedAssetsCheckDept> list = new ArrayList<>();
            FixedAssetsCheckDept dept = null;
            for (String code : codes) {
                dept = new FixedAssetsCheckDept();
                dept.setCheckId(id);
                dept.setOrgCode(code);
                list.add(dept);
            }
            if (CollectionUtil.isNotEmpty(list)) {
                fixedAssetsCheckDeptService.saveBatch(list);
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
        // 执行中
        fixedAssetsCheck.setStatus(FixedAssetsConstant.STATUS_1);
        this.updateById(fixedAssetsCheck);
        fixedAssetsCheckRecordService.updateBatchById(records);
        return id;
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        log.info("固定资产模块更新状态参数：{}", JSONObject.toJSONString(updateStateEntity));
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        if (ObjectUtil.isEmpty(loginUser) || ObjectUtil.isEmpty(userId)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        String businessKey = updateStateEntity.getBusinessKey();
        FixedAssetsCheck assetsCheck = this.getById(businessKey);
        Assert.notNull(assetsCheck, "未找到对应数据！");

        int states = updateStateEntity.getStates();
        switch (states) {
            case 2:
                // 盘点结果审核
                assetsCheck.setStatus(FixedAssetsConstant.STATUS_2);
                assetsCheck.setAuditResult(null);
                break;
            case 3:
                // 盘点结果驳回
                assetsCheck.setStatus(FixedAssetsConstant.STATUS_1);
                assetsCheck.setAuditResult(FixedAssetsConstant.AUDIT_RESULT_0);
                break;
            case 4:
                // 审核通过
                assetsCheck.setStatus(FixedAssetsConstant.STATUS_3);
                assetsCheck.setAuditTime(new Date());
                assetsCheck.setAuditId(userId);
                assetsCheck.setAuditReason(updateStateEntity.getReason());
                assetsCheck.setAuditResult(FixedAssetsConstant.AUDIT_RESULT_1);
                List<FixedAssetsCheckRecord> fixedAssetsCheckRecord = fixedAssetsCheckRecordService.lambdaQuery()
                        .eq(FixedAssetsCheckRecord::getDelFlag,FixedAssetsConstant.STATUS_0)
                        .eq(FixedAssetsCheckRecord::getCheckId,businessKey).list();
                List<FixedAssetsCheckDetail> fixedAssetsCheckDetail =fixedAssetsCheckDetailService.lambdaQuery()
                        .eq(FixedAssetsCheckDetail::getDelFlag,FixedAssetsConstant.STATUS_0)
                        .eq(FixedAssetsCheckDetail::getCheckId,businessKey).list();
                fixedAssetsCheckDetail.forEach(f->{
                    FixedAssetsCheckRecord fixedAssetsCheckRecordList = fixedAssetsCheckRecord.stream().filter(fix-> fix.getAssetCode().equals(f.getAssetCode())).findFirst().get();
                    f.setAfterNumber(fixedAssetsCheckRecordList.getActualNumber());
                });
                fixedAssetsCheckDetailService.updateBatchById(fixedAssetsCheckDetail);
                break;
        }
        this.updateById(assetsCheck);
    }
}
