package com.aiurt.boot.rehearsal.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.service.IEmergencyPlanService;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyDeptDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyLedgerDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalRegisterDTO;
import com.aiurt.boot.rehearsal.entity.*;
import com.aiurt.boot.rehearsal.mapper.EmergencyImplementationRecordMapper;
import com.aiurt.boot.rehearsal.service.*;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordMonthVO;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordReadOneVO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyImplementationRecordServiceImpl extends ServiceImpl<EmergencyImplementationRecordMapper, EmergencyImplementationRecord> implements IEmergencyImplementationRecordService {

    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private IEmergencyRecordDeptService emergencyRecordDeptService;
    @Autowired
    private IEmergencyRecordStepService emergencyRecordStepService;
    @Autowired
    private IEmergencyRecordQuestionService emergencyRecordQuestionService;
    @Autowired
    private EmergencyImplementationRecordMapper emergencyImplementationRecordMapper;
    @Autowired
    private IEmergencyRehearsalMonthService emergencyRehearsalMonthService;
    @Autowired
    private IEmergencyRehearsalYearService emergencyRehearsalYearService;
    @Autowired
    private IEmergencyPlanService emergencyPlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String rehearsalRegister(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        EmergencyImplementationRecord implementationRecord = new EmergencyImplementationRecord();
        BeanUtils.copyProperties(emergencyRehearsalRegisterDTO, implementationRecord);
        if (ObjectUtil.isNotEmpty(implementationRecord.getStatus())
                && EmergencyConstant.RECORD_STATUS_2.equals(implementationRecord.getStatus())) {
            implementationRecord.setStatus(EmergencyConstant.RECORD_STATUS_2);
        } else {
            implementationRecord.setStatus(EmergencyConstant.RECORD_STATUS_1);
        }
        this.save(implementationRecord);

        String id = implementationRecord.getId();
        // 添加记录的部门、问题、步骤等关联信息
        addAssociatedInfo(id, emergencyRehearsalRegisterDTO);
        return id;
    }

    @Override
    public IPage<EmergencyImplementationRecordVO> queryPageList(Page<EmergencyImplementationRecordVO> page, EmergencyRecordDTO emergencyRecordDTO) {
        // 根据当前登录人的部门权限和记录的组织部门以及参与部门过滤数据
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        List<CsUserDepartModel> deptModel = iSysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> orgCodes = deptModel.stream().filter(l -> StrUtil.isNotEmpty(l.getOrgCode())).map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orgCodes)) {
            return page;
        }
        IPage<EmergencyImplementationRecordVO> pageList = emergencyImplementationRecordMapper.queryPageList(page, emergencyRecordDTO, orgCodes);
        Map<String, String> orgMap = iSysBaseApi.getAllSysDepart().stream()
                .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
        pageList.getRecords().stream().forEach(l -> {
            List<EmergencyRecordDept> recordDepts = emergencyRecordDeptService.lambdaQuery()
                    .eq(EmergencyRecordDept::getRecordId, l.getId()).list();
            if (CollectionUtil.isNotEmpty(recordDepts)) {
                List<EmergencyDeptDTO> depts = new ArrayList<>();
                recordDepts.forEach(d -> depts.add(new EmergencyDeptDTO(d.getOrgCode(), orgMap.get(d.getOrgCode()))));
                String deptNames = depts.stream().map(EmergencyDeptDTO::getOrgName).collect(Collectors.joining(","));
                l.setDeptList(depts);
                l.setDeptNames(deptNames);
            }
        });
        return pageList;
    }

    @Override
    public boolean submit(String id, Integer status) {
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应数据！");
        if (EmergencyConstant.RECORD_STATUS_2.equals(record.getStatus())) {
            throw new AiurtBootException("记录已提交，无需重复提交！");
        } else if (EmergencyConstant.RECORD_STATUS_1.equals(record.getStatus())) {
            if (!EmergencyConstant.RECORD_STATUS_2.equals(status)) {
                throw new AiurtBootException("当前记录已经是待提交状态，不允许再变更为待提交状态！");
            }
            record.setStatus(status);
            boolean update = this.updateById(record);
            return update;
        } else {
            throw new AiurtBootException("数据存在异常！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应数据！");
        if (EmergencyConstant.RECORD_STATUS_2.equals(record.getStatus())) {
            throw new AiurtBootException("记录已提交，不允许删除！");
        }
        this.removeById(id);
        // 根据实施记录的ID删除记录的部门、问题、步骤等关联信息
        this.deleteAssociatedInfo(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        String id = emergencyRehearsalRegisterDTO.getId();
        Assert.notNull(emergencyRehearsalRegisterDTO.getId(), "记录ID为空！");
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应数据！");
        if (EmergencyConstant.RECORD_STATUS_2.equals(record.getStatus())) {
            throw new AiurtBootException("记录已经提交，不允许更改！");
        }
        BeanUtils.copyProperties(emergencyRehearsalRegisterDTO, record);
        if (ObjectUtil.isNotEmpty(emergencyRehearsalRegisterDTO.getStatus())
                && EmergencyConstant.RECORD_STATUS_2.equals(emergencyRehearsalRegisterDTO.getStatus())) {
            record.setStatus(EmergencyConstant.RECORD_STATUS_2);
        } else {
            record.setStatus(EmergencyConstant.RECORD_STATUS_1);
        }
        this.updateById(record);

        // 根据实施记录的ID删除记录的部门、问题、步骤等关联信息
        this.deleteAssociatedInfo(id);
        // 添加记录的部门、问题、步骤等关联信息
        this.addAssociatedInfo(id, emergencyRehearsalRegisterDTO);
    }

    @Override
    public EmergencyRecordReadOneVO queryById(String id) {
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应记录！");
        EmergencyRecordReadOneVO recordVO = new EmergencyRecordReadOneVO();
        BeanUtils.copyProperties(record, recordVO);

        // 获取关联月演练计划
        EmergencyRehearsalMonth rehearsalMonth = emergencyRehearsalMonthService.getById(record.getPlanId());
        EmergencyRecordMonthVO monthVO = new EmergencyRecordMonthVO();
        Optional.ofNullable(rehearsalMonth).ifPresent(month -> BeanUtils.copyProperties(rehearsalMonth, monthVO));
        // 月演练计划字典和组织机构信息转换翻译
        this.monthPlanDictTranslate(monthVO);

        // 查询对应的组织机构
        List<EmergencyRecordDept> deptList = emergencyRecordDeptService.lambdaQuery()
                .eq(EmergencyRecordDept::getRecordId, id).list();
        List<EmergencyDeptDTO> depts = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(deptList)) {
            Map<String, String> orgMap = iSysBaseApi.getAllSysDepart().stream()
                    .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
            deptList.forEach(l -> depts.add(new EmergencyDeptDTO(l.getOrgCode(), orgMap.get(l.getOrgCode()))));
            String deptNames = depts.stream().map(EmergencyDeptDTO::getOrgName).collect(Collectors.joining(";"));
            recordVO.setDeptNames(deptNames);
        }

        // 查询对应的问题
        List<EmergencyRecordQuestion> questionList = emergencyRecordQuestionService.lambdaQuery()
                .eq(EmergencyRecordQuestion::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(EmergencyRecordQuestion::getRecordId, id).list();
        // 问题列表的字典，组织机构名称转换
        this.questionTranslate(questionList);

        // 查询对应的步骤
        List<EmergencyRecordStep> stepList = emergencyRecordStepService.lambdaQuery()
                .eq(EmergencyRecordStep::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(EmergencyRecordStep::getRecordId, id).list();

        Optional.ofNullable(record.getStationCode()).ifPresent(code -> recordVO.setStationName(iSysBaseApi.getFullNameByPositionCode(code)));
        Optional.ofNullable(record.getPositionCode()).ifPresent(code -> recordVO.setPositionName(iSysBaseApi.getFullNameByPositionCode(code)));

        recordVO.setRehearsalMonth(monthVO);
        recordVO.setDepts(depts);
        recordVO.setSteps(stepList);
        recordVO.setQuestions(questionList);
        return recordVO;
    }

    @Override
    public List<SysDeptUserModel> getDeptUserGanged() {
        return iSysBaseApi.getDeptUserGanged();
    }

    @Override
    public List<LoginUser> getDutyUser() {
        // 责任人根据当前的用户部门筛选出当前部门的所有人
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        if (StrUtil.isEmpty(orgCode)) {
            return Collections.emptyList();
        }
        List<LoginUser> users = iSysBaseApi.getUserByDeptCode(orgCode);
        return users;
    }

    @Override
    public void exportLedger(EmergencyRecordDTO emergencyRecordDTO, Integer pageNo, Integer pageSize, HttpServletRequest request, HttpServletResponse response){
        // 需要的内容有: emergency_implementation_record表的rehearsal_time(实际演练时间)
        //             emergency_rehearsal_month表的subject(演练科目)
        //             emergency_record_question表的description(描述)和process_mode(处理方式)

        List<String> recodeIdList;
        // 如果有selections参数指定了id，就不用分页查询了
        String selections = request.getParameter("selections");
        if (StrUtil.isNotEmpty(selections)) {
            recodeIdList = Arrays.asList(selections.split(","));
        }else {
            Page<EmergencyImplementationRecordVO> page = new Page<>(pageNo, pageSize);
            IPage<EmergencyImplementationRecordVO> pageList = this.queryPageList(page, emergencyRecordDTO);
            recodeIdList = pageList.getRecords().stream().map(EmergencyImplementationRecordVO::getId).collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(recodeIdList)) {
            throw new AiurtBootException("数据选择错误，请重新选择导出数据");
        }
        // 根据recodeIdList，查询所需的数据
        List<EmergencyLedgerDTO> emergencyLedgerDTOList = this.baseMapper.queryLedger(recodeIdList);

        // excel表中每行数据
        List<Map<String, Object>> rowDataList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < emergencyLedgerDTOList.size(); i++) {
            EmergencyLedgerDTO ledgerDTO = emergencyLedgerDTOList.get(i);
            // excel中每行的数据
            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("index", i + 1);
            String question = String.format("在%s%s中发现%s", dateFormat.format(ledgerDTO.getRehearsalTime()), ledgerDTO.getSubject(), ledgerDTO.getDescription());
            rowMap.put("question", question);
            rowMap.put("measure", ledgerDTO.getProcessMode());
            rowDataList.add(rowMap);
        }

        // excel表的设置
        ExportParams exportParams = new ExportParams("演练问题闭环台账", "Sheet1");
        // 表头的设置
        List<ExcelExportEntity> keyList = new ArrayList<>();
        ExcelExportEntity indexEntity = new ExcelExportEntity("序号", "index", 10);
        ExcelExportEntity questionEntity = new ExcelExportEntity("演练时间及发现问题", "question", 80);
        ExcelExportEntity measureEntity = new ExcelExportEntity("闭环措施", "measure", 50);
        // 设置自动换行
        questionEntity.setWrap(true);
        measureEntity.setWrap(true);

        keyList.add(indexEntity);
        keyList.add(questionEntity);
        keyList.add(measureEntity);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, keyList, rowDataList);
        // 将workbook表写入response中
        try {
            String attachName = new String("演练问题闭环台账.xls".getBytes(), StandardCharsets.UTF_8);
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + attachName);
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EmergencyRecordReadOneVO> printEmergency(String ids) {
        List<EmergencyRecordReadOneVO> printEmergency = new ArrayList<>();
        List<String> idList = StrUtil.splitTrim(ids, ",");
        idList.forEach(id->{
            EmergencyRecordReadOneVO emergencyRecordReadOneVO = queryById(id);
            LoginUser userById = iSysBaseApi.getUserById(emergencyRecordReadOneVO.getRecorderId());
            emergencyRecordReadOneVO.setUserName(userById.getRealname());
            printEmergency.add(emergencyRecordReadOneVO);
        });
        return printEmergency;
    }

    /**
     * 关联的问题列表的字典，组织机构名称转换
     *
     * @param questionList
     */
    private void questionTranslate(List<EmergencyRecordQuestion> questionList) {
        if (CollectionUtil.isNotEmpty(questionList)) {
            Map<String, String> categoryMap = iSysBaseApi.getDictItems(EmergencyDictConstant.QUESTION_CATEGORY).stream()
                    .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
            Map<String, String> statusMap = iSysBaseApi.getDictItems(EmergencyDictConstant.QUESTION_STATUS).stream()
                    .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
            Map<String, String> orgMap = iSysBaseApi.getAllSysDepart().stream()
                    .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
            questionList.forEach(l -> {
                l.setCategoryName(categoryMap.get(String.valueOf(l.getCategory())));
                l.setStatusName(statusMap.get(String.valueOf(l.getStatus())));
                l.setOrgName(orgMap.get(l.getOrgCode()));
                Optional.ofNullable(l.getOrgUserId()).ifPresent(userId -> {
                    LoginUser loginUser = iSysBaseApi.getUserById(userId);
                    Optional.ofNullable(loginUser).ifPresent(user -> l.setOrgUserName(user.getRealname()));
                });
                Optional.ofNullable(l.getUserId()).ifPresent(userId -> {
                    LoginUser loginUser = iSysBaseApi.getUserById(userId);
                    Optional.ofNullable(loginUser).ifPresent(user -> l.setUserName(user.getRealname()));
                });
            });
        }
    }

    /**
     * 月演练计划字典信息转换翻译
     *
     * @param monthVO
     */
    private void monthPlanDictTranslate(EmergencyRecordMonthVO monthVO) {
        if (ObjectUtil.isNotEmpty(monthVO)) {
            // 字典翻译
            Optional.ofNullable(monthVO.getType()).ifPresent(type -> {
                String typeName = iSysBaseApi.getDictItems(EmergencyDictConstant.TYPE)
                        .stream()
                        .filter(l -> String.valueOf(type).equals(l.getValue()))
                        .map(DictModel::getText).collect(Collectors.joining());
                monthVO.setTypeName(typeName);
            });
            Optional.ofNullable(monthVO.getYearWithin()).ifPresent(within -> {
                String withinName = iSysBaseApi.getDictItems(EmergencyDictConstant.WITHIN)
                        .stream()
                        .filter(l -> String.valueOf(within).equals(l.getValue()))
                        .map(DictModel::getText).collect(Collectors.joining());
                monthVO.setYearWithinName(withinName);
            });
            Optional.ofNullable(monthVO.getModality()).ifPresent(modality -> {
                String modalityName = iSysBaseApi.getDictItems(EmergencyDictConstant.MODALITY)
                        .stream()
                        .filter(l -> String.valueOf(modality).equals(l.getValue()))
                        .map(DictModel::getText).collect(Collectors.joining());
                monthVO.setModalityName(modalityName);
            });
            // 获取组织机构名称
            Optional.ofNullable(monthVO.getOrgCode()).ifPresent(orgCode -> {
                SysDepartModel deptModel = iSysBaseApi.getDepartByOrgCode(orgCode);
                Optional.ofNullable(deptModel).ifPresent(deptObj -> monthVO.setOrgCodeName(deptObj.getDepartName()));
            });
            // 查询月计划关联的年计划获取年度信息(因为月计划中没有存储)
            Optional.ofNullable(monthVO.getPlanId()).ifPresent(planId -> {
                EmergencyRehearsalYear yearPlan = emergencyRehearsalYearService.getById(planId);
                Optional.ofNullable(yearPlan).ifPresent(plan -> monthVO.setYear(plan.getYear()));
            });
            // 依托预案名称
            Optional.ofNullable(monthVO.getSchemeId()).ifPresent(schemeId -> {
                EmergencyPlan emergencyPlan = emergencyPlanService.getById(schemeId);
                Optional.ofNullable(emergencyPlan).ifPresent(ep -> monthVO.setSchemeName(ep.getEmergencyPlanName()));
            });
        }
    }

    /**
     * 添加记录的部门、问题、步骤等关联信息
     *
     * @param id
     * @param emergencyRehearsalRegisterDTO
     */
    private void addAssociatedInfo(String id, EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        // 参与部门
        List<EmergencyRecordDept> depts = new ArrayList<>();
        List<String> orgCodes = emergencyRehearsalRegisterDTO.getOrgCodes();
        if (CollectionUtil.isNotEmpty(orgCodes)) {
            orgCodes.forEach(orgCode -> {
                EmergencyRecordDept dept = new EmergencyRecordDept();
                dept.setRecordId(id);
                dept.setOrgCode(orgCode);
                depts.add(dept);
            });
            emergencyRecordDeptService.saveBatch(depts);
        }
        // 演练步骤
        List<EmergencyRecordStep> steps = emergencyRehearsalRegisterDTO.getSteps();
        if (CollectionUtil.isNotEmpty(steps)) {
            steps.forEach(l -> l.setRecordId(id));
            emergencyRecordStepService.saveBatch(steps);
        }
        // 登记问题
        List<EmergencyRecordQuestion> questions = emergencyRehearsalRegisterDTO.getQuestions();
        if (CollectionUtil.isNotEmpty(questions)) {
            questions.forEach(l -> l.setRecordId(id));
            emergencyRecordQuestionService.saveBatch(questions);
        }
    }

    /**
     * 根据实施记录的ID删除记录的部门、问题、步骤等关联信息
     *
     * @param recordId
     */
    private void deleteAssociatedInfo(String recordId) {
        // 删除关联的部门
        emergencyRecordDeptService.remove(new LambdaQueryWrapper<EmergencyRecordDept>().eq(EmergencyRecordDept::getRecordId, recordId));
        // 删除关联的问题
        emergencyRecordQuestionService.remove(new LambdaQueryWrapper<EmergencyRecordQuestion>().eq(EmergencyRecordQuestion::getRecordId, recordId));
        // 删除关联的步骤
        emergencyRecordStepService.remove(new LambdaQueryWrapper<EmergencyRecordStep>().eq(EmergencyRecordStep::getRecordId, recordId));

    }
}
