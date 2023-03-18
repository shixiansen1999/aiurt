package com.aiurt.boot.check.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
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
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.aiurt.boot.record.service.IFixedAssetsCheckRecordService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
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
    @Resource
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;

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
        /*// 发消息
        BusMessageDTO messageDTO = new BusMessageDTO();
        //设置消息属性
        messageDTO.setStartTime(new Date());
        messageDTO.setEndTime(new Date());
        messageDTO.setTitle("固定资产消息通知");
        messageDTO.setBusType(SysAnnmentTypeEnum.ASSET_CHECKER.getType());
        messageDTO.setToAll(false);
        LoginUser userById = sysBaseApi.getUserById(fixedAssetsCheck.getCheckId());
        messageDTO.setContent(String.format("%s你好，您作为[%s]盘点任务的盘点人,尽快完成!", userById.getRealname(), fixedAssetsCheck.getInventoryList()));
        //设置接收人
        messageDTO.setToUser(userById.getUsername());
        sysBaseApi.sendBusAnnouncement(messageDTO);*/

        // 发消息
        try {

            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, fixedAssetsCheck.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.ASSET_CHECKER.getType());

            /*MessageDTO messageDTO = new MessageDTO();
            messageDTO.setData(map);
            messageDTO.setTitle("固定资产盘点"+DateUtil.today());
            messageDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("固定资产盘点");
            messageDTO.setPublishingContent("请在计划开始时间内盘点，并填写盘点记录结果");
            sendMessage(messageDTO,fixedAssetsCheck,orgCodes);*/

            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setData(map);
            todoDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE);
            todoDTO.setTitle("固定资产盘点"+DateUtil.today());
            todoDTO.setMsgAbstract("固定资产盘点");
            todoDTO.setPublishingContent("请在计划开始时间内盘点，并填写盘点记录结果");
            todoDTO.setProcessDefinitionName("固定资产盘点");
            todoDTO.setTaskName("固定资产盘点");
            sendTodo(todoDTO, fixedAssetsCheck, orgCodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        FixedAssetsCheckVO fixedAssetsCheckVO = fixedAssetsCheckMapper.queryById(id);
        String processInstanceId = fixedAssetsCheckVO.getProcessInstanceId();

        checkVO.setCategorys(categorys);
        checkVO.setDepts(depts);
        checkVO.setModelKey("fixed_assets_check");
        checkVO.setProcessInstanceId(processInstanceId);
        return checkVO;
    }

    @Override
    public IPage<FixedAssetsCheckVO> pageList(Page<FixedAssetsCheckVO> page, FixedAssetsCheckDTO fixedAssetsCheckDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        fixedAssetsCheckDTO = Optional.ofNullable(fixedAssetsCheckDTO).orElseGet(FixedAssetsCheckDTO::new);
        fixedAssetsCheckDTO.setAuditStatus(FixedAssetsConstant.STATUS_2);
        fixedAssetsCheckDTO.setUserName(loginUser.getUsername());
        if (StrUtil.isNotEmpty(fixedAssetsCheckDTO.getCategoryCode())){
            fixedAssetsCheckDTO.setCategoryCodes(fixedAssetsCheckMapper.selectCategoryCodeByPid(fixedAssetsCheckDTO.getCategoryCode()));
        }
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
                String categorysList = l.getCategorys().stream().map(FixedAssetsCheckCategory::getCategoryName).collect(Collectors.joining(","));
                l.setCategorysList(categorysList);
                l.setDepts(depts);
                String deptsList = l.getDepts().stream().map(FixedAssetsCheckDept::getOrgName).collect(Collectors.joining(","));
                l.setDeptsList(deptsList);
                l.setPlanDate(DateUtil.format(l.getPlanStartDate(), "YYYY-MM-dd")+"至"+DateUtil.format(l.getPlanEndDate(), "YYYY-MM-dd"));
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

        try {
            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, assetsCheck.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.ASSET_AUDIT.getType());

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setData(map);

            List<FixedAssetsCheckDept> deptList = fixedAssetsCheckDeptService.lambdaQuery()
                    .eq(FixedAssetsCheckDept::getCheckId, assetsCheck.getId()).list();
            List<String> orgCodes = deptList.stream().map(FixedAssetsCheckDept::getOrgCode).collect(Collectors.toList());

            int states = updateStateEntity.getStates();
            TodoDTO todoDTO = new TodoDTO();
            switch (states) {
                case 2:
                    // 盘点结果审核
                    assetsCheck.setStatus(FixedAssetsConstant.STATUS_2);
                    assetsCheck.setAuditResult(null);
                    //发送通知和待办
                    /*messageDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE);
                    messageDTO.setTitle("固定资产盘点审核"+DateUtil.today());
                    messageDTO.setMsgAbstract("固定资产盘点审核");
                    messageDTO.setPublishingContent("固定资产盘点审核");
                    sendMessage(messageDTO,assetsCheck,orgCodes);

                    todoDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE);
                    todoDTO.setTitle("固定资产盘点审核"+DateUtil.today());
                    todoDTO.setMsgAbstract("固定资产盘点审核");
                    todoDTO.setPublishingContent("固定资产盘点审核");
                    todoDTO.setProcessDefinitionName("固定资产盘点审核");
                    todoDTO.setTaskName("固定资产盘点审核");
                    sendTodo(todoDTO, assetsCheck, orgCodes);*/
                    break;
                case 3:
                    // 盘点结果驳回
                    assetsCheck.setStatus(FixedAssetsConstant.STATUS_1);
                    assetsCheck.setAuditResult(FixedAssetsConstant.AUDIT_RESULT_0);

                   /* map.put("", DateUtil.format(assetsCheck.getActualStartTime(), "yyyy-MM-dd HH:mm") + "-" + DateUtil.format(assetsCheck.getActualEndTime(), "yyyy-MM-dd HH:mm"));
                    //发送通知
                    messageDTO.setData(map);
                    messageDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE_REJECT);
                    messageDTO.setTitle("固定资产盘点审核"+DateUtil.today());
                    messageDTO.setMsgAbstract("固定资产盘点审核驳回");
                    messageDTO.setPublishingContent("固定资产盘点审核驳回");
                    sendMessage(messageDTO,assetsCheck,orgCodes);
                    todoDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE);
                    todoDTO.setTitle("固定资产盘点审核"+DateUtil.today());
                    todoDTO.setMsgAbstract("固定资产盘点审核驳回");
                    todoDTO.setPublishingContent("固定资产盘点审核驳回");
                    todoDTO.setProcessDefinitionName("固定资产盘点审核");
                    todoDTO.setTaskName("固定资产盘点审核");
                    sendTodo(todoDTO, assetsCheck, orgCodes);*/
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
                    List<FixedAssets> fixedAssetsArrayList = new ArrayList<>();
                    fixedAssetsCheckRecord.forEach(f->{
                        if (ObjectUtils.isNotEmpty(f.getActualNumber())){
                            FixedAssets fixedAssets = fixedAssetsService.lambdaQuery()
                                    .eq(FixedAssets::getDelFlag,FixedAssetsConstant.STATUS_0)
                                    .eq(FixedAssets::getAssetCode,f.getAssetCode()).one();
                            fixedAssets.setNumber(f.getActualNumber());
                            fixedAssetsArrayList.add(fixedAssets);
                        }
                    });
                    fixedAssetsService.updateBatchById(fixedAssetsArrayList);
                    //发送通知
                    /*messageDTO.setTemplateCode(CommonConstant.FIXED_ASSETS_SERVICE_NOTICE);
                    messageDTO.setTitle("固定资产盘点审核"+DateUtil.today());
                    messageDTO.setMsgAbstract("固定资产盘点审核");
                    messageDTO.setPublishingContent("固定资产盘点审核");
                    sendMessage(messageDTO,assetsCheck,orgCodes);*/
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.updateById(assetsCheck);
    }

    /**
     * 发送通知消息
     */
    private void sendMessage(MessageDTO messageDTO, FixedAssetsCheck fixedAssetsCheck, List<String> orgCodes) {
        LoginUser userById = sysBaseApi.getUserById(fixedAssetsCheck.getCheckId());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //发送通知
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(messageDTO.getData())) {
            map.putAll(messageDTO.getData());
        }
        map.put("inventoryList",fixedAssetsCheck.getInventoryList());
        List<String> names = sysBaseApi.queryOrgNamesByOrgCodes(orgCodes);
        map.put("departName", StrUtil.join(",", names));
        map.put("checkName", userById.getRealname());
        map.put("time", DateUtil.format(fixedAssetsCheck.getPlanStartDate(), "yyyy-MM-dd")+"-"+DateUtil.format(fixedAssetsCheck.getPlanEndDate(), "yyyy-MM-dd"));
        messageDTO.setData(map);

        messageDTO.setStartTime(new Date());
        messageDTO.setEndTime(new Date());
        messageDTO.setFromUser(sysUser.getUsername());
        messageDTO.setToUser(userById.getUsername());
        messageDTO.setToAll(false);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_12);

        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FIXED_ASSETS_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        sysBaseApi.sendTemplateMessage(messageDTO);
    }

    /**发送待办消息*/
    private void sendTodo(TodoDTO todoDTO, FixedAssetsCheck fixedAssetsCheck,List<String> orgCodes) {
        LoginUser userById = sysBaseApi.getUserById(fixedAssetsCheck.getCheckId());
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(todoDTO.getData())) {
            map.putAll(todoDTO.getData());
        }
        map.put("inventoryList",fixedAssetsCheck.getInventoryList());
        List<String> names = sysBaseApi.queryOrgNamesByOrgCodes(orgCodes);
        map.put("departName", StrUtil.join(",", names));
        map.put("checkName", userById.getRealname());
        map.put("time", DateUtil.format(fixedAssetsCheck.getPlanStartDate(), "yyyy-MM-dd")+"-"+DateUtil.format(fixedAssetsCheck.getPlanEndDate(), "yyyy-MM-dd"));
        todoDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FIXED_ASSETS_MESSAGE_PROCESS);
        todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");

        todoDTO.setBusinessKey(fixedAssetsCheck.getId());
        todoDTO.setBusinessType(TodoBusinessTypeEnum.FIXED_ASSETS.getType());
        todoDTO.setCurrentUserName(userById.getUsername());
        todoDTO.setTaskType(TodoTaskTypeEnum.FIXED_ASSETS.getType());
        todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
        todoDTO.setUrl(null);
        todoDTO.setAppUrl(null);

        isTodoBaseAPI.createTodoTask(todoDTO);
    }
}
