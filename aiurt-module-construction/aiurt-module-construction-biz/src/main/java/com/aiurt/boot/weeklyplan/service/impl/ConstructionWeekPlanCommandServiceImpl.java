package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.ConstructionConstant;
import com.aiurt.boot.constant.ConstructionDictConstant;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanExportDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionCommandAssist;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionCommandAssistService;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.position.entity.CsStation;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Slf4j
@Service
public class ConstructionWeekPlanCommandServiceImpl extends ServiceImpl<ConstructionWeekPlanCommandMapper, ConstructionWeekPlanCommand> implements IConstructionWeekPlanCommandService, IFlowableBaseUpdateStatusService {
    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private ConstructionWeekPlanCommandMapper constructionWeekPlanCommandMapper;
    @Autowired
    private IConstructionCommandAssistService constructionCommandAssistService;

    @Override
    public IPage<ConstructionWeekPlanCommandVO> queryPageList(Page<ConstructionWeekPlanCommandVO> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandMapper.queryPageList(page, loginUser.getId(), constructionWeekPlanCommandDTO);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String declaration(ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand planCommand = null;
        QueryWrapper<ConstructionWeekPlanCommand> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(constructionWeekPlanCommand) && ObjectUtil.isNotEmpty(constructionWeekPlanCommand.getId())) {
            wrapper.lambda().eq(ConstructionWeekPlanCommand::getId, constructionWeekPlanCommand.getId());
            planCommand = this.getOne(wrapper);
        }

        if (ObjectUtil.isEmpty(planCommand)) {
            // 生成计划令编码
            StringBuilder code = new StringBuilder();
            List<DictModel> types = iSysBaseApi.getDictItems(ConstructionDictConstant.CATEGORY);
            String typeName = types.stream().filter(l -> l.getValue().equals(String.valueOf(constructionWeekPlanCommand.getType())))
                    .map(DictModel::getText).collect(Collectors.joining());

            // 临时修补计划和日计划
            if (ConstructionConstant.PLAN_TYPE_2.equals(constructionWeekPlanCommand.getPlanChange())
                    || ConstructionConstant.PLAN_TYPE_3.equals(constructionWeekPlanCommand.getPlanChange())) {
                code = new StringBuilder("L-");
            }

            // 获取施工的日期对应的日
            String day = DateUtil.format(constructionWeekPlanCommand.getTaskDate(), "dd");

            // 构建计划令编号
            // XXX fixme 此处得保证线路编号是数值，比如1号线对应01、2号线对应02这种，否则以线路编号拼接
            String lineCode = constructionWeekPlanCommand.getLineCode();
            try {
                lineCode = String.valueOf(Integer.valueOf(lineCode));
            } catch (Exception e) {
                log.info("获取线路编号生成计划令编码异常：", e.getMessage());
                e.printStackTrace();
            }

            String separator = "-";
            code.append(lineCode).append(typeName).append(separator).append(day).append(separator);

            // 计划令自增序号，如果是一位或两位数的则保留两位，三位则保留三位，即6->06、66->66,大于99小于1000则保留三位
            List<ConstructionWeekPlanCommand> codeNumbers = this.lambdaQuery().like(ConstructionWeekPlanCommand::getCode, code.toString())
                    .orderByDesc(ConstructionWeekPlanCommand::getCode)
                    .last("limit 1")
                    .list();

            if (CollectionUtil.isNotEmpty(codeNumbers) && ObjectUtil.isNotEmpty(codeNumbers.get(0).getCode())) {
                String planCode = codeNumbers.get(0).getCode();
                Integer serialNumber = Integer.valueOf(planCode.substring(planCode.lastIndexOf(separator) + 1));
                if (100 > serialNumber) {
                    code.append(String.format("%02d", serialNumber + 1));
                } else {
                    code.append(serialNumber + 1);
                }
            } else {
                code.append(String.format("%02d", 1));
            }

            constructionWeekPlanCommand.setCode(code.toString());
            constructionWeekPlanCommand.setApplyId(loginUser.getId());
            this.save(constructionWeekPlanCommand);

            List<ConstructionCommandAssist> constructionAssist = constructionWeekPlanCommand.getConstructionAssist();
            if (CollectionUtil.isNotEmpty(constructionAssist)) {
                constructionAssist.forEach(l -> l.setPlanId(constructionWeekPlanCommand.getId()));
                constructionCommandAssistService.saveBatch(constructionWeekPlanCommand.getConstructionAssist());
            }

        } else {
            // 驳回后再次提审时，更新为待提审状态
            constructionWeekPlanCommand.setApplyId(loginUser.getId());
            constructionWeekPlanCommand.setFormStatus(ConstructionConstant.FORM_STATUS_0);
            this.updateById(constructionWeekPlanCommand);

            List<ConstructionCommandAssist> constructionAssist = constructionWeekPlanCommand.getConstructionAssist();
            QueryWrapper<ConstructionCommandAssist> assistWrapper = new QueryWrapper<>();
            assistWrapper.lambda().eq(ConstructionCommandAssist::getPlanId, constructionWeekPlanCommand.getId());
            constructionCommandAssistService.remove(assistWrapper);
            if (CollectionUtil.isNotEmpty(constructionAssist)) {
                constructionAssist.forEach(l -> l.setPlanId(constructionWeekPlanCommand.getId()));
                constructionCommandAssistService.saveBatch(constructionWeekPlanCommand.getConstructionAssist());
            }
        }
        return constructionWeekPlanCommand.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        Integer formStatus = constructionWeekPlanCommand.getFormStatus();
        if (!ConstructionConstant.FORM_STATUS_0.equals(formStatus) || !ConstructionConstant.FORM_STATUS_3.equals(formStatus)) {
            throw new AiurtBootException("该记录在审核中，不能进行修改！");
        }
        this.updateById(constructionWeekPlanCommand);
        QueryWrapper<ConstructionCommandAssist> assistWrapper = new QueryWrapper<>();
        assistWrapper.lambda().eq(ConstructionCommandAssist::getPlanId, constructionWeekPlanCommand.getId());
        constructionCommandAssistService.remove(assistWrapper);
        List<ConstructionCommandAssist> assists = constructionWeekPlanCommand.getConstructionAssist();
        if (CollectionUtil.isNotEmpty(assists)) {
            constructionCommandAssistService.saveBatch(assists);
        }
    }

    @Override
    public void cancel(String id, String reason) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(id);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        if (ConstructionConstant.FORM_STATUS_5.equals(command.getFormStatus())) {
            throw new AiurtBootException("已通过的任务不能取消！");
        }
        command.setFormStatus(ConstructionConstant.FORM_STATUS_4);
        command.setCancelReason(reason);
        command.setCancelId(loginUser.getId());
        this.updateById(command);
    }

    @Override
    public void submit(String id) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(id);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        Integer formStatus = command.getFormStatus();
        // 已取消的计划不给提审
        if (ConstructionConstant.FORM_STATUS_4.equals(formStatus)) {
            throw new AiurtBootException("该周计划已经取消！");
        }
        // 只有为待提审状态或者已驳回状态的计划才可以提审
        if (!ConstructionConstant.FORM_STATUS_0.equals(formStatus)
                && !ConstructionConstant.FORM_STATUS_3.equals(formStatus)) {
            throw new AiurtBootException("该周计划已在审批中或已完成审批！");
        }
        command.setApplyId(loginUser.getId());
        // 修改状态为待审核
        command.setFormStatus(ConstructionConstant.FORM_STATUS_1);
        // 初始化各个角色的审批状态
        command.setLineStatus(ConstructionConstant.APPROVE_STATUS_0);
        command.setDirectorStatus(ConstructionConstant.APPROVE_STATUS_0);
        command.setDispatchStatus(ConstructionConstant.APPROVE_STATUS_0);
        command.setManagerStatus(ConstructionConstant.APPROVE_STATUS_0);
        this.updateById(command);
    }

    @Override
    public void audit(String id) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(id);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        // todo 审批逻辑

    }

    @Override
    public ConstructionWeekPlanCommand queryById(String id) {
        ConstructionWeekPlanCommand constructionWeekPlanCommand = this.getById(id);
        if (constructionWeekPlanCommand == null) {
            throw new AiurtBootException("未找到对应数据");
        }
        // 辅站信息
        List<ConstructionCommandAssist> assists = constructionCommandAssistService.lambdaQuery()
                .eq(ConstructionCommandAssist::getPlanId, id).list();
        if (CollectionUtil.isNotEmpty(assists)) {
            assists.stream().forEach(l -> {
                String userId = l.getUserId();
                String stationCode = l.getStationCode();
                if (StrUtil.isNotEmpty(userId)) {
                    LoginUser loginUser = iSysBaseApi.getUserById(userId);
                    l.setUserName(loginUser.getRealname());
                }
                if (StrUtil.isNotEmpty(stationCode)) {
                    Map<String, String> stationMap = iSysBaseApi.getStationNameByCode(Arrays.asList(stationCode));
                    l.setStationName(stationMap.get(stationCode));
                }
            });
            constructionWeekPlanCommand.setConstructionAssist(assists);
        }
        return constructionWeekPlanCommand;
    }

    /**
     * 驳回第一个节点
     *
     * @param entity
     */
    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        if (ObjectUtil.isEmpty(loginUser) || ObjectUtil.isEmpty(userId)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(entity.getId());
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        // 更新为已驳回状态，此时可以再次提审
        command.setFormStatus(ConstructionConstant.FORM_STATUS_3);
        command.setRejectId(loginUser.getId());
        command.setRejectReason(entity.getReason());
        this.updateById(command);
        log.info("流程ID为：【{}】的流程驳回成功！", entity.getProcessInstanceId());
    }

    /**
     * 更新状态
     *
     * @param updateStateEntity
     */
    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        log.info("更新状态参数：{}", JSONObject.toJSONString(updateStateEntity));
        String businessKey = updateStateEntity.getBusinessKey();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        if (ObjectUtil.isEmpty(loginUser) || ObjectUtil.isEmpty(userId)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        ConstructionWeekPlanCommand command = this.getById(businessKey);
        if (ObjectUtil.isEmpty(command)) {
            throw new AiurtBootException("未找到对应数据！");
        }
        int states = updateStateEntity.getStates();
        if (1 == states) {
            // 线路负责人审批
            command.setLineStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setLineOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (3 == states) {
            command.setLineUserId(loginUser.getId());
            // 生产调度审批
            command.setDispatchStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setDispatchOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (5 == states) {
            if (ConstructionConstant.PLAN_TYPE_1.equals(command.getPlanChange())) {
                command.setDispatchId(loginUser.getId());
            } else if (ConstructionConstant.PLAN_TYPE_2.equals(command.getPlanChange())
                    || ConstructionConstant.PLAN_TYPE_3.equals(command.getPlanChange())) {
                command.setManagerId(loginUser.getId());
            }
            // 已通过
            command.setFormStatus(ConstructionConstant.FORM_STATUS_5);
        } else if (6 == states) {
            if (ConstructionConstant.PLAN_TYPE_2.equals(command.getPlanChange())
                    || ConstructionConstant.PLAN_TYPE_3.equals(command.getPlanChange())) {
                command.setDispatchId(loginUser.getId());
            }
            // 分部主任审批
            command.setDirectorStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setDirectorOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (8 == states) {
            command.setDirectorId(loginUser.getId());
            // 中心经理审批
            command.setManagerStatus(ConstructionConstant.APPROVE_STATUS_1);
            command.setManagerOpinion(updateStateEntity.getReason());
            // 审核中
            command.setFormStatus(ConstructionConstant.FORM_STATUS_2);
        } else if (2 == states || 4 == states || 7 == states || 9 == states) {
            // 驳回
            command.setRejectId(loginUser.getId());
            command.setRejectReason(updateStateEntity.getReason());
            command.setFormStatus(ConstructionConstant.FORM_STATUS_3);
            // 清空之前的审批人信息
            command.setLineUserId(null);
            command.setDispatchId(null);
            command.setDirectorId(null);
            command.setManagerId(null);
        } else {
            throw new AiurtBootException("你没有权限审批或你不是节点的审批人！");
        }
        this.updateById(command);
    }

    /**
     * 查询待办
     *
     * @param page                           分页
     * @param constructionWeekPlanCommandDTO 请求参数
     * @return
     */
    @Override
    public IPage<ConstructionWeekPlanCommandVO> queryWorkToDo(Page<ConstructionWeekPlanCommandVO> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandMapper.queryWorkToDo(page, loginUser.getUsername(), constructionWeekPlanCommandDTO);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        ConstructionWeekPlanCommand command = this.getById(id);
        Assert.notNull(command, "未找到对应数据！");
        if (!ConstructionConstant.FORM_STATUS_0.equals(command.getFormStatus())) {
            throw new AiurtBootException("计划已经提审或者已经完成审批，不允许删除！");
        }
        this.removeById(command);
        // 同时删除对应的辅站信息
        List<ConstructionCommandAssist> commandAssists = constructionCommandAssistService.lambdaQuery()
                .eq(ConstructionCommandAssist::getPlanId, id).list();
        if (CollectionUtil.isNotEmpty(commandAssists)) {
            constructionCommandAssistService.removeBatchByIds(commandAssists);
        }
    }

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, String lineCode, Date startDate, Date endDate) {
        List<ConstructionWeekPlanExportDTO> dataList = constructionWeekPlanCommandMapper.getExportData(lineCode, startDate, endDate);
        List<CsStation> stations = iSysBaseApi.queryAllStation();
        Map<String, String> stationMap = stations.stream().collect(Collectors.toMap(k -> k.getStationCode(), v -> v.getStationName(), (a, b) -> a));
        dataList.stream().forEach(l -> {
            String stationCode = l.getAssistStationCode();
            if (StrUtil.isNotEmpty(stationCode)) {
                List<String> stationCodes = StrUtil.split(stationCode, ',');
                List<String> stationNames = new ArrayList<>();
                stationCodes.forEach(sc -> Optional.ofNullable(stationMap.get(sc)).ifPresent(stationName -> stationNames.add(stationName)));
                l.setAssistStationName(stationNames.stream().collect(Collectors.joining("、")));
            }
        });
        String title = "运营施工及行车计划申报表";
        if (StrUtil.isNotEmpty(lineCode)) {
            Map<String, String> lineNameMap = iSysBaseApi.getLineNameByCode(Arrays.asList(lineCode));
            String lineName = lineNameMap.get(lineCode);
            if (StrUtil.isNotEmpty(lineName)) {
                title = lineName + title;
            }
        }
        Map<Date, List<ConstructionWeekPlanExportDTO>> listMap = dataList.stream()
                .sorted((a, b) -> DateUtil.compare(a.getTaskDate(), b.getTaskDate()))
                .collect(Collectors.groupingBy(ConstructionWeekPlanExportDTO::getTaskDate));
        Map<String, String> weekMap = iSysBaseApi.getDictItems(ConstructionDictConstant.WEEK).stream()
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        ExportParams params = new ExportParams(title, null, "运营施工及行车计划申报表");
        // 设置自定义样式
        params.setStyle(CustomExcelExportStylerImpl.class);
        Workbook workbook = ExcelExportUtil.exportExcel(params, ConstructionWeekPlanExportDTO.class, new ArrayList<>());
        try {
            int flag = 0;
            for (Date date : listMap.keySet()) {
                String secondTitle = DateUtil.format(date, "yyyy年MM月dd日");
                int week = DateUtil.dayOfWeek(date) == 1 ? 7 : DateUtil.dayOfWeek(date) - 1;
                secondTitle += "(" + weekMap.get(String.valueOf(week)) + ")";
                String sheetName = "运营施工及行车计划申报表";
                ExportParams exportParams = new ExportParams(title, secondTitle, sheetName);
                // 设置自定义样式
                exportParams.setStyle(CustomExcelExportStylerImpl.class);
                Workbook sheets = ExcelExportUtil.exportExcel(exportParams, ConstructionWeekPlanExportDTO.class, listMap.get(date));
                CustomExcelExportStylerImpl excelExportStyler = new CustomExcelExportStylerImpl(workbook);
                if (flag == 0) {
                    workbook = sheets;
                    flag++;
                    // 调整格式
                    for (Sheet sheet : workbook) {
                        int lastRowNum = sheet.getLastRowNum();
                        for (int i = 1; i < lastRowNum; i++) {
                            Row row = sheet.getRow(i);
                            for (Cell cell : row) {
                                if (i == 1 || i == 2) {
                                    cell.getCellStyle().cloneStyleFrom(excelExportStyler.getTitleStyle((short) 40));
                                } else {
                                    cell.getCellStyle().cloneStyleFrom(excelExportStyler.stringNoneStyle(workbook, true));
                                }
                            }
                            row.setHeightInPoints(25);
                        }
                        sheet.createFreezePane(sheet.getRow(0).getLastCellNum(), 1);
                    }
                    continue;
                }
                // 实际就一个sheet
                for (Sheet sheet : workbook) {
                    int rowNum = sheet.getLastRowNum() + 1;
                    Sheet afterSheet = sheets.getSheet(sheetName);
                    for (int i = 1; i <= afterSheet.getLastRowNum(); i++) {
                        Row workbookRow = sheet.createRow((short) rowNum);
                        Row afterSheetRow = afterSheet.getRow(i);
                        short lastCellNum = afterSheetRow.getLastCellNum();
                        for (int j = 0; j < lastCellNum; j++) {
                            Cell cell = afterSheetRow.getCell(j);
                            Cell workRowCell = workbookRow.createCell(j);
                            workRowCell.setCellValue(cell.getStringCellValue());
                            if (i == 1 || i == 2) {
                                Workbook sheetWorkbook = sheet.getWorkbook();
                                CellStyle cellStyle = new CustomExcelExportStylerImpl(sheetWorkbook).getTitleStyle((short) 40);
                                workRowCell.setCellStyle(cellStyle);
                            } else {
                                workRowCell.getCellStyle().cloneStyleFrom(cell.getCellStyle());
                            }
                        }
                        if (i == 1) {
                            CellRangeAddress rangeAddress = new CellRangeAddress(rowNum, rowNum, 0, lastCellNum - 1);
                            sheet.addMergedRegion(rangeAddress);
                        }
                        workbookRow.setHeightInPoints(25);
                        rowNum++;
                    }
                }
            }
            String filename = title + ".xls";
            response.setHeader("content-disposition", "attachment;filename=" + new String(filename.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("周施工计划导出失败！", e.getMessage());
            e.printStackTrace();
        }
    }
}
