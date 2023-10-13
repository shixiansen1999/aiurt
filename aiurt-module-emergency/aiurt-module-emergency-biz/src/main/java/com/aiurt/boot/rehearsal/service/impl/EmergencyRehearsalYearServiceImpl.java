package com.aiurt.boot.rehearsal.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.mapper.EmergencyPlanMapper;
import com.aiurt.boot.plan.service.impl.EmergencyPlanServiceImpl;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.dto.*;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalMonthMapper;
import com.aiurt.boot.rehearsal.mapper.EmergencyRehearsalYearMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalMonthService;
import com.aiurt.boot.rehearsal.service.IEmergencyRehearsalYearService;
import com.aiurt.boot.rehearsal.service.strategy.AuditContext;
import com.aiurt.boot.rehearsal.service.strategy.NodeFactory;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.ExcelSelectListUtil;
import com.aiurt.common.util.TimeUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
@Slf4j
public class EmergencyRehearsalYearServiceImpl extends ServiceImpl<EmergencyRehearsalYearMapper, EmergencyRehearsalYear> implements IEmergencyRehearsalYearService, IFlowableBaseUpdateStatusService {
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;
    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private IEmergencyRehearsalMonthService emergencyRehearsalMonthService;
    @Autowired
    private EmergencyRehearsalMonthMapper emergencyRehearsalMonthMapper;
    @Autowired
    private EmergencyRehearsalYearMapper emergencyRehearsalYearMapper;
    @Autowired
    private EmergencyPlanMapper emergencyPlanMapper;

    @Autowired
    private DataSourceTransactionManager transactionManager;



    @Override
    public IPage<EmergencyRehearsalYear> queryPageList(Page<EmergencyRehearsalYear> page, EmergencyRehearsalYearDTO emergencyRehearsalYearDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        if (ObjectUtil.isNotEmpty(emergencyRehearsalYearDTO) && ObjectUtil.isNotEmpty(emergencyRehearsalYearDTO.getOrgCode())) {
            List<String> orgCodes = iSysBaseApi.getSublevelOrgCodes(emergencyRehearsalYearDTO.getOrgCode());
            emergencyRehearsalYearDTO.setOrgCodes(orgCodes);
        }
        Page<EmergencyRehearsalYear> pageList = emergencyRehearsalYearMapper.queryPageList(page, emergencyRehearsalYearDTO, new EmergencyPlanStatusDTO(), loginUser.getUsername());
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        EmergencyRehearsalYear rehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, rehearsalYear);
        // 构造年计划编号
        String code = "NDYJ" + DateUtil.format(new Date(), "yyyyMMdd-");
        EmergencyRehearsalYear emergencyRehearsalYear = this.lambdaQuery().like(EmergencyRehearsalYear::getCode, code)
                .orderByDesc(EmergencyRehearsalYear::getCode)
                .last("limit 1")
                .one();
        if (ObjectUtil.isEmpty(emergencyRehearsalYear)) {
            code += String.format("%02d", 1);
        } else {
            String yearCode = emergencyRehearsalYear.getCode();
            Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.lastIndexOf("-") + 1));
            if (serialNo >= 99) {
                code += (serialNo + 1);
            } else {
                code += String.format("%02d", (serialNo + 1));
            }
        }
        rehearsalYear.setCode(code);
        this.save(rehearsalYear);

        String id = rehearsalYear.getId();
        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        if (CollectionUtil.isNotEmpty(monthList)) {
            for (EmergencyRehearsalMonth month : monthList) {
                String monthCode = emergencyRehearsalMonthService.getMonthCode();
                month.setPlanId(id);
                month.setCode(monthCode);
                month.setYearWithin(EmergencyConstant.WITHIN_1);
                emergencyRehearsalMonthService.save(month);
            }
        }
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 非待提审状态不允许删除
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("已提审的计划不允许删除！");
        }
        this.removeById(id);

        QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
        emergencyRehearsalMonthService.remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String edit(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        String id = emergencyRehearsalYearAddDTO.getId();
        Assert.notNull(id, "记录ID为空！");
        EmergencyRehearsalYear rehearsalYear = this.getById(id);
        Assert.notNull(rehearsalYear, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
            throw new AiurtBootException("已提审的计划不允许编辑！");
        }
        EmergencyRehearsalYear emergencyRehearsalYear = new EmergencyRehearsalYear();
        BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, emergencyRehearsalYear);
        this.updateById(emergencyRehearsalYear);

        QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
        emergencyRehearsalMonthService.remove(wrapper);
        List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
        if (CollectionUtil.isNotEmpty(monthList)) {
            for (EmergencyRehearsalMonth month : monthList) {
                String monthCode = emergencyRehearsalMonthService.getMonthCode();
                month.setPlanId(id);
                month.setCode(monthCode);
                month.setYearWithin(EmergencyConstant.WITHIN_1);
                emergencyRehearsalMonthService.save(month);
            }
        }
        return id;
    }

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, String ids ,String orgCode) {
        List<EmergencyRehearsalYear> rehearsalYears;
        List<String> orgCodes = null;
        if (ObjectUtil.isNotEmpty(orgCode)) {
           orgCodes = iSysBaseApi.getSublevelOrgCodes(orgCode);
        }
        if (StrUtil.isEmpty(ids)) {
            LambdaQueryWrapper<EmergencyRehearsalYear> wrapper = new LambdaQueryWrapper<>();
            if (CollectionUtil.isNotEmpty(orgCodes)){
                wrapper.in(EmergencyRehearsalYear::getOrgCode,orgCodes);
            }
            wrapper.eq(EmergencyRehearsalYear::getDelFlag, CommonConstant.DEL_FLAG_0);
            rehearsalYears = this.list(wrapper);
        } else {
            List<String> split = StrUtil.split(ids, ',');
            rehearsalYears = this.lambdaQuery()
                    .eq(EmergencyRehearsalYear::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(EmergencyRehearsalYear::getId, split)
                    .list();
        }
        if (CollectionUtil.isEmpty(rehearsalYears)) {
            throw new AiurtBootException("没有可以导出的数据！");
        }

        List<Workbook> workbooks = new LinkedList<>();
        List<String> titles = new LinkedList<>();
        for (EmergencyRehearsalYear rehearsalYear : rehearsalYears) {
//            List<EmergencyRehearsalMonth> rehearsalMonthList = emergencyRehearsalMonthService.lambdaQuery()
//                    .eq(EmergencyRehearsalMonth::getDelFlag, CommonConstant.DEL_FLAG_0)
//                    .eq(EmergencyRehearsalMonth::getPlanId, rehearsalYear.getId())
//                    .list();
            List<EmergencyRehearsalMonth> rehearsalMonthList = emergencyRehearsalMonthMapper.exportMonthList(rehearsalYear.getId());
            SysDepartModel dept = iSysBaseApi.getDepartByOrgCode(rehearsalYear.getOrgCode());
            String title = ObjectUtil.isEmpty(dept) ? "" : dept.getDepartName() + rehearsalYear.getYear() + "年" + rehearsalYear.getName();
            // excel数据
            ExportParams exportParams = new ExportParams(title, null);
            // 添加索引
            exportParams.setAddIndex(true);
            // 设置自定义样式
            exportParams.setStyle(CustomExcelExportStylerImpl.class);
            Workbook exportExcel = ExcelExportUtil.exportExcel(exportParams, EmergencyRehearsalMonth.class, rehearsalMonthList);
            workbooks.add(exportExcel);
            titles.add(title);
        }
        try {
            String fileName = "应急演练计划";
            String suffix = ".zip";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + suffix, "UTF-8"));
            OutputStream outputStream = response.getOutputStream();
            ByteArrayOutputStream byteArrayOutputStream = null;
            InputStream inputStream = null;
            // 创建临时文件
            File zipTempFile = File.createTempFile(fileName, suffix);
//            FileOutputStream fileOutputStream = new FileOutputStream(zipTempFile);
//            CheckedOutputStream checkedOutputStream = new CheckedOutputStream(fileOutputStream, new Adler32());
            // 压缩成zip格式
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            for (int i = 0; i < workbooks.size(); i++) {
                Workbook workbook = workbooks.get(i);
                byteArrayOutputStream = new ByteArrayOutputStream();
                // 将excel写入字节数组输出流
                workbook.write(byteArrayOutputStream);
                // 转化为字节数据
                byte[] content = byteArrayOutputStream.toByteArray();
                // 写入输入流
                inputStream = new ByteArrayInputStream(content);
                // 添加Excel表数据
                String filename = titles.get(i) + "_" + System.nanoTime() + ".xls";
                zipOutputStream.putNextEntry(new ZipEntry(filename));
                int flag = 0;
                while ((flag = inputStream.read()) != -1) {
                    zipOutputStream.write(flag);
                }
            }
            zipOutputStream.flush();
            outputStream.flush();
            zipOutputStream.closeEntry();
            inputStream.close();
            zipOutputStream.close();
            byteArrayOutputStream.close();
//            checkedOutputStream.close();
//            fileOutputStream.close();
        } catch (Exception e) {
            log.error("年演练计划导出异常！", e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 保存或者编辑年演练计划信息
     *
     * @param emergencyRehearsalYearAddDTO
     * @return
     */
    public String startProcess(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            String id = emergencyRehearsalYearAddDTO.getId();
            if (StrUtil.isEmpty(id)) {
                EmergencyRehearsalYear rehearsalYear = new EmergencyRehearsalYear();
                BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, rehearsalYear);
                // 构造年计划编号
                String code = "NDYJ" + DateUtil.format(new Date(), "yyyyMMdd-");
                EmergencyRehearsalYear emergencyRehearsalYear = this.lambdaQuery().like(EmergencyRehearsalYear::getCode, code)
                        .orderByDesc(EmergencyRehearsalYear::getCode)
                        .last("limit 1")
                        .one();
                if (ObjectUtil.isEmpty(emergencyRehearsalYear)) {
                    code += String.format("%02d", 1);
                } else {
                    String yearCode = emergencyRehearsalYear.getCode();
                    Integer serialNo = Integer.valueOf(yearCode.substring(yearCode.lastIndexOf("-") + 1));
                    if (serialNo >= 99) {
                        code += (serialNo + 1);
                    } else {
                        code += String.format("%02d", (serialNo + 1));
                    }
                }
                rehearsalYear.setCode(code);
                this.save(rehearsalYear);

                String planId = rehearsalYear.getId();
                List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
                if (CollectionUtil.isNotEmpty(monthList)) {
                    for (EmergencyRehearsalMonth month : monthList) {
                        String monthCode = emergencyRehearsalMonthService.getMonthCode();
                        month.setPlanId(planId);
                        month.setCode(monthCode);
                        month.setYearWithin(EmergencyConstant.WITHIN_1);
                        emergencyRehearsalMonthService.save(month);
                    }
                }
                transactionManager.commit(status);
                return planId;
            } else {
                EmergencyRehearsalYear rehearsalYear = this.getById(id);
                Assert.notNull(rehearsalYear, "未找到对应数据！");
                // 代提审才允许编辑
                if (!EmergencyConstant.YEAR_STATUS_1.equals(rehearsalYear.getStatus())) {
                    throw new AiurtBootException("已提审的计划不允许编辑！");
                }
                EmergencyRehearsalYear emergencyRehearsalYear = new EmergencyRehearsalYear();
                BeanUtils.copyProperties(emergencyRehearsalYearAddDTO, emergencyRehearsalYear);
                this.updateById(emergencyRehearsalYear);

                QueryWrapper<EmergencyRehearsalMonth> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(EmergencyRehearsalMonth::getPlanId, id);
                emergencyRehearsalMonthService.remove(wrapper);
                List<EmergencyRehearsalMonth> monthList = emergencyRehearsalYearAddDTO.getMonthList();
                if (CollectionUtil.isNotEmpty(monthList)) {
                    for (EmergencyRehearsalMonth month : monthList) {
                        String monthCode = emergencyRehearsalMonthService.getMonthCode();
                        month.setPlanId(id);
                        month.setCode(monthCode);
                        month.setYearWithin(EmergencyConstant.WITHIN_1);
                        emergencyRehearsalMonthService.save(month);
                    }
                }
                transactionManager.commit(status);
                return id;
            }
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new AiurtBootException(e.getMessage());
        }
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        EmergencyRehearsalYear rehearsalYear = this.getById(businessKey);
        if (ObjectUtil.isEmpty(rehearsalYear)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
//        switch (states) {
//            case 2:
//                // 演练计划负责人审批
//                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_2);
//                break;
//            case 3:
//                // 演练计划负责人驳回，更新状态为待提交状态
//                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_1);
//                break;
//            case 4:
//                // 已通过
//                rehearsalYear.setStatus(EmergencyConstant.YEAR_STATUS_3);
//                break;
//        }
//        this.updateById(rehearsalYear);
        AuditContext context = new AuditContext(NodeFactory.getNode(states));
        EmergencyRehearsalYear emergencyRehearsalYear = context.doAudit(rehearsalYear);
        this.updateById(emergencyRehearsalYear);

    }

    /**
     * @param updateStateEntity
     */
    @Override
    public void updateStates(UpdateStateEntity updateStateEntity) {

        String businessKey = updateStateEntity.getBusinessKey();
        EmergencyRehearsalYear rehearsalYear = this.getById(businessKey);
        if (ObjectUtil.isEmpty(rehearsalYear)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        Integer states = updateStateEntity.getStates();

        rehearsalYear.setStatus(states);
        this.updateById(rehearsalYear);
    }

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(2);
            params.setNeedSave(true);

            List<String> errorMessage = new ArrayList<>();
            int successLines = 0;
            // 错误信息
            int  errorLines = 0;

            try {
                String type = FilenameUtils.getExtension(file.getOriginalFilename());
                if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                    return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
                }

                List<EmergencyRehearsalYearImport> emergencyRehearsalYears = ExcelImportUtil.importExcel(file.getInputStream(), EmergencyRehearsalYearImport.class, params);
                Iterator<EmergencyRehearsalYearImport> iterator = emergencyRehearsalYears.iterator();
                while (iterator.hasNext()) {
                    EmergencyRehearsalYearImport model = iterator.next();
                    boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                    if (b) {
                        iterator.remove();
                    }
                }
                if (CollUtil.isEmpty(emergencyRehearsalYears)) {
                    return Result.error("文件导入失败:文件内容不能为空！");
                }

                for (EmergencyRehearsalYearImport emergencyRehearsalYear : emergencyRehearsalYears) {
                    //必填数据校验
                    errorLines = checkRequired(emergencyRehearsalYear,errorLines);
                }
                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, emergencyRehearsalYears, successLines, null, type);
                }

                //校验通过，添加数据
                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                for (EmergencyRehearsalYearImport emergencyRehearsalYear : emergencyRehearsalYears) {
                    List<EmergencyRehearsalMonthImport> monthList = emergencyRehearsalYear.getMonthList();
                    List<EmergencyRehearsalMonth> months = new ArrayList<>();
                    for (EmergencyRehearsalMonthImport emergencyRehearsalMonthImport : monthList) {
                        EmergencyRehearsalMonth month = new EmergencyRehearsalMonth();
                        BeanUtils.copyProperties(emergencyRehearsalMonthImport, month);
                        months.add(month);
                    }
                    EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO = new EmergencyRehearsalYearAddDTO();
                    BeanUtils.copyProperties(emergencyRehearsalYear, emergencyRehearsalYearAddDTO);
                    emergencyRehearsalYearAddDTO.setMonthList(months);
                    emergencyRehearsalYearAddDTO.setUserId(loginUser.getId());
                    emergencyRehearsalYearAddDTO.setOrgCode(loginUser.getOrgCode());
                    emergencyRehearsalYearAddDTO.setCompileDate(DateUtil.parse(DateUtil.today(),"yyyy-MM-dd"));
                    emergencyRehearsalYearAddDTO.setStatus(EmergencyConstant.YEAR_STATUS_3);
                    this.startProcess(emergencyRehearsalYearAddDTO);
                }
                return XlsUtil.importReturnRes(errorLines, emergencyRehearsalYears.size(), errorMessage, true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.ok("文件导入失败！");
    }


    private int checkRequired(EmergencyRehearsalYearImport emergencyRehearsalYear,int errorLines) {
        StringBuilder stringBuilder = new StringBuilder();
        Boolean haveMonthError = false;
        List<EmergencyRehearsalMonthImport> monthList = emergencyRehearsalYear.getMonthList();

        List<DictModel> emergencyRehearsalType = iSysBaseApi.queryDictItemsByCode("emergency_rehearsal_type");
        List<DictModel> emergencyRehearsalModality = iSysBaseApi.queryDictItemsByCode("emergency_rehearsal_modality");
        Map<String, String> emergencyRehearsalTypeMap = emergencyRehearsalType.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (key1, key2) -> key2));
        Map<String, String> emergencyRehearsalModalityMap = emergencyRehearsalModality.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (key1, key2) -> key2));

        if (StrUtil.isBlank(emergencyRehearsalYear.getName())) {
            stringBuilder.append("计划名称不能为空，");
        }
        if (StrUtil.isBlank(emergencyRehearsalYear.getYear())) {
            stringBuilder.append("所属年份不能为空，");
        } else {
            boolean legalYear = TimeUtil.isLegalDate(4, emergencyRehearsalYear.getYear(), "yyyy");
            if (!legalYear) {
                stringBuilder.append("所属年份格式填写不对，");
            }
        }
        if (CollUtil.isNotEmpty(monthList)) {
            for (EmergencyRehearsalMonthImport emergencyRehearsalMonth : monthList) {
                StringBuilder stringBuilderMonth = new StringBuilder();
                String typeName = emergencyRehearsalMonth.getTypeName();
                String subject = emergencyRehearsalMonth.getSubject();
                String schemeName = emergencyRehearsalMonth.getSchemeName();
                String schemeVersion = emergencyRehearsalMonth.getSchemeVersion();
                String modalityName = emergencyRehearsalMonth.getModalityName();
                String orgName = emergencyRehearsalMonth.getOrgName();
                String rehearsalTime = emergencyRehearsalMonth.getRehearsalTime();
                String step = emergencyRehearsalMonth.getStep();

                if (StrUtil.isNotBlank(typeName)) {
                    String type = emergencyRehearsalTypeMap.get(typeName);
                    if (StrUtil.isNotBlank(type)) {
                        emergencyRehearsalMonth.setType(Integer.valueOf(type));
                    } else {
                        stringBuilderMonth.append("系统不存在该演练类型，");
                    }
                }else {
                    stringBuilderMonth.append("演练类型不能为空，");
                }

                if (StrUtil.isBlank(subject)) {
                    stringBuilderMonth.append("演练科目不能为空，");
                }

                if (StrUtil.isNotBlank(schemeName) && StrUtil.isNotBlank(schemeVersion)) {
                    LambdaQueryWrapper<EmergencyPlan> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(EmergencyPlan::getEmergencyPlanName, schemeName)
                            .eq(EmergencyPlan::getEmergencyPlanVersion, schemeVersion)
                            .eq(EmergencyPlan::getDelFlag, CommonConstant.DEL_FLAG_0);
                    EmergencyPlan emergencyPlan = emergencyPlanMapper.selectOne(queryWrapper);
                    if (ObjectUtil.isNotNull(emergencyPlan)) {
                        emergencyRehearsalMonth.setSchemeId(emergencyPlan.getId());
                    } else {
                        stringBuilderMonth.append("系统不存在该对应版本的预案，");
                    }

                }else {
                    stringBuilderMonth.append("依托预案名称和预案版本不能为空，");
                }

                if (StrUtil.isNotBlank(modalityName)) {
                    String modality = emergencyRehearsalModalityMap.get(modalityName);
                    if (StrUtil.isNotBlank(modality)) {
                        emergencyRehearsalMonth.setModality(Integer.valueOf(modality));
                    } else {
                        stringBuilderMonth.append("系统不存在该演练形式，");
                    }
                }else {
                    stringBuilderMonth.append("演练形式不能为空，");
                }

                if (StrUtil.isNotBlank(orgName)) {
                    List<String> list = StrUtil.splitTrim(orgName, "/");
                    String id = null;
                    for (int i = 0; i < list.size(); i++) {
                        String s = list.get(i);
                        //根据部门名称和父id找部门
                        JSONObject depart = iSysBaseApi.getDepartByNameAndParentId(s, id);
                        if (ObjectUtil.isNotNull(depart)) {
                            id = depart.getString("id");
                            emergencyRehearsalMonth.setOrgCode(depart.getString("orgCode"));
                        }else {
                            stringBuilderMonth.append("系统不存在该组织部门，");
                            break;
                        }
                    }
                }else {
                    stringBuilderMonth.append("组织部门不能为空，");
                }

                if (StrUtil.isBlank(rehearsalTime)) {
                    stringBuilderMonth.append("演练时间不能为空，");
                } else {
                    boolean legalYear = TimeUtil.isLegalDate(7, rehearsalTime, "yyyy-MM");
                    if (!legalYear) {
                        stringBuilderMonth.append("演练时间格式填写不对，");
                    }
                }

                if (StrUtil.isBlank(step)) {
                    stringBuilderMonth.append("必须体现环节不能为空，");
                }

                if (stringBuilderMonth.length() > 0) {
                    // 截取字符
                    stringBuilderMonth.deleteCharAt(stringBuilderMonth.length() - 1);
                    emergencyRehearsalMonth.setMistake(stringBuilderMonth.toString());
                    haveMonthError = true;
                }

            }
        }else {
            stringBuilder.append("演练计划不能为空，");
        }

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            emergencyRehearsalYear.setMistake(stringBuilder.toString());
        }
        // 错误数据条数
        if (stringBuilder.length() > 0 || haveMonthError) {
            errorLines++;
        }
        return errorLines;
    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<EmergencyRehearsalYearImport> emergencyRehearsalYears, int successLines, String url, String type) {
        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("/templates/emergencyRehearsalYearError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>(16);
            List<Map<String, Object>> mapList = new ArrayList<>();

            for (EmergencyRehearsalYearImport emergencyRehearsalYearImport : emergencyRehearsalYears) {
                List<EmergencyRehearsalMonthImport> monthList = emergencyRehearsalYearImport.getMonthList();
                for (EmergencyRehearsalMonthImport emergencyRehearsalMonthImport : monthList) {
                    Map<String, Object> map = new HashMap<>(12);
                    // 获取年计划错误信息
                    map.put("name", emergencyRehearsalYearImport.getName());
                    map.put("year", emergencyRehearsalYearImport.getYear());
                    map.put("yearMistake", emergencyRehearsalYearImport.getMistake());
                    // 获取子表错误信息
                    map.put("typeName", emergencyRehearsalMonthImport.getTypeName());
                    map.put("subject", emergencyRehearsalMonthImport.getSubject());
                    map.put("schemeName", emergencyRehearsalMonthImport.getSchemeName());
                    map.put("schemeVersion", emergencyRehearsalMonthImport.getSchemeVersion());
                    map.put("modalityName", emergencyRehearsalMonthImport.getModalityName());
                    map.put("orgName", emergencyRehearsalMonthImport.getOrgName());
                    map.put("rehearsalTime", emergencyRehearsalMonthImport.getRehearsalTime());
                    map.put("step", emergencyRehearsalMonthImport.getStep());
                    map.put("mouthMistake", emergencyRehearsalMonthImport.getMistake());
                    mapList.add(map);
                }
            }
            errorMap.put("maplist", mapList);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(1);
            sheetsMap.put(0, errorMap);
            Workbook workbook =  cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            int size = 4;
            int length = 2;
            for (EmergencyRehearsalYearImport yearImport : emergencyRehearsalYears) {
                for (int i = 0; i <= length; i++) {
                    //合并单元格
                    PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + yearImport.getMonthList().size() - 1, i, i);
                }
                size = size + yearImport.getMonthList().size();
            }
            String fileName = "年度演练计划错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, true, url);

    }



    @Override
    public void exportTemplateXl(HttpServletResponse response) throws IOException {
        //获取输入流，原始模板位置
        Resource resource = new ClassPathResource("/templates/emergencyRehearsalYear.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emergencyRehearsalYear.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        Workbook workbook = cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);

        List<DictModel> emergencyRehearsalType = bean.queryDictItemsByCode("emergency_rehearsal_type");
        List<DictModel> emergencyRehearsalModality = bean.queryDictItemsByCode("emergency_rehearsal_modality");

        ExcelSelectListUtil.selectList(workbook, "演练类型", 4, 2, 2, emergencyRehearsalType);
        ExcelSelectListUtil.selectList(workbook, "演练形式", 4, 6, 6, emergencyRehearsalModality);

        String fileName = "年度演练计划导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "年度演练计划导入模板.xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
