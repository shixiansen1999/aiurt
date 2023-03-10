package com.aiurt.modules.faultproducereport.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.mapper.FaultProduceReportMapper;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportService;
import com.aiurt.modules.faultproducereport.util.ExcelStylesUtil;
import com.aiurt.modules.faultproducereportlinedetail.entity.FaultProduceReportLineDetail;
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskCompleteDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.position.entity.CsLine;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date: 2023-02-23
 * @Version: V1.0
 */
@Service
@Slf4j
public class FaultProduceReportServiceImpl extends ServiceImpl<FaultProduceReportMapper, FaultProduceReport> implements IFaultProduceReportService, IFlowableBaseUpdateStatusService {

    @Autowired
    private FaultProduceReportMapper produceReportMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private FlowBaseApi flowBaseApi;
    @Autowired
    private IFaultProduceReportLineDetailService iFaultProduceReportLineDetailService;

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String businessKey = updateStateEntity.getBusinessKey();
        FaultProduceReport faultProduceReport = this.getById(businessKey);
        LambdaUpdateWrapper<FaultProduceReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FaultProduceReport::getId,faultProduceReport.getId());
        if (ObjectUtil.isEmpty(faultProduceReport)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        } else {
            int states = updateStateEntity.getStates();
                switch (states) {
                //提交，更新状态，及提交人更新
                case 0:
                    faultProduceReport.setState(2);
                    faultProduceReport.setSubmitTime(new Date());
                    faultProduceReport.setSubmitUserName(sysUser.getUsername());
                    break;
                case 1:
                    faultProduceReport.setState(2);
                    break;
                case 2:
                    faultProduceReport.setState(1);
//                    faultProduceReport.setSubmitTime(null);
//                    updateWrapper.set(FaultProduceReport::getSubmitTime,null);
//                    faultProduceReport.setSubmitUserName("");
                    break;
                case 3:
                    faultProduceReport.setState(2);
                    break;
                case 4:
                    faultProduceReport.setState(1);
//                    faultProduceReport.setSubmitTime(null);
//                    updateWrapper.set(FaultProduceReport::getSubmitTime,null);
//                    faultProduceReport.setSubmitUserName("");
                    break;
                case 5:
                    faultProduceReport.setState(3);
                    break;
                default:
            }
            produceReportMapper.update(faultProduceReport,updateWrapper);
        }
    }

    /**
     * 保存或者编辑年演练计划信息
     *
     * @param faultProduceReport
     * @return
     */
    public String startProcess(FaultProduceReportDTO faultProduceReport) {
        String id = faultProduceReport.getId();
        if(CollUtil.isNotEmpty(faultProduceReport.getReportLineDetailDTOList())){
            iFaultProduceReportLineDetailService.updateListByIds(faultProduceReport.getReportLineDetailDTOList());
        }
        return id;
    }
    /**
     * 分页列表查询
     * @param pageList
     * @param faultProduceReport
     * @param beginDay
     * @param endDay
     * @return
     */
    @Override
    public Result<IPage<FaultProduceReportDTO>> queryPageList(Page<FaultProduceReportDTO> pageList, FaultProduceReport faultProduceReport, String beginDay, String endDay) {
        // 获取到当前登录的用户的专业(majorCode、可能有多个，使用List存储)
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        List<String> majorCodeList = new ArrayList<>();
        for (CsUserMajorModel csUserMajorModel : CsUserMajorModelList) {
            majorCodeList.add(csUserMajorModel.getMajorCode());
        }
        // 如果查询参数有majorCode，这个majorCode在当前登录的用户的专业内，查询的专业只查询这个majorCode，不然查询的空
        if (faultProduceReport.getMajorCode() != null) {
            if (majorCodeList.contains(faultProduceReport.getMajorCode())) {
                majorCodeList.clear();
                majorCodeList.add(faultProduceReport.getMajorCode());
            } else {
                // 查询参数有majorCode，但是当前登录的用户的专业不包含majorCode，返回空数据
                return Result.ok(pageList);
            }
        }

        // 不传时间参数，默认查询所有
        // 传有时间参数的话，统计时间大于等于开始时间，小于等于结束时间
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 时间是否是指定格式(日期格式：yyyy-MM-dd)， 不是指定格式的话，舍弃
        if (beginDay != null) {
            try {
                DateUtils.parseDate(beginDay + " 00:00:00", pattern);
            } catch (Exception ignored) {
                beginDay = null;
            }
        }
        if (endDay != null) {
            try {
                DateUtils.parseDate(endDay + " 23:59:59", pattern);
            } catch (Exception ignored) {
                endDay = null;
            }
        }
        List<FaultProduceReportDTO> reportDTOList = produceReportMapper.queryPageList(pageList, majorCodeList, beginDay, endDay);
        for (FaultProduceReportDTO reportDTO: reportDTOList) {
            List<String> csMajorNamesByCodes = iSysBaseAPI.getCsMajorNamesByCodes(Collections.singletonList(reportDTO.getMajorCode()));
            String majorName = null;
            if (csMajorNamesByCodes.size() > 0) {
                majorName = csMajorNamesByCodes.get(0);
            }
            reportDTO.setMajorName(majorName);  // 设置专业名称
            // 设置提交人的realname
            if (reportDTO.getSubmitUserName() != null) {
                LoginUser submitUser = iSysBaseAPI.queryUser(reportDTO.getSubmitUserName());
                reportDTO.setSubmitUserRealname(submitUser.getRealname());
            }

            if (reportDTO.getTaskId() != null && (reportDTO.getState() == 0 || reportDTO.getState() == 1)) {
                TaskInfoDTO taskInfoDTO = flowBaseApi.viewRuntimeTaskInfo(reportDTO.getProcessInstanceId(), reportDTO.getTaskId());
                List<ActOperationEntity> operationList = taskInfoDTO.getOperationList();
                if (operationList != null && !operationList.isEmpty()) {
                    reportDTO.setIsOpen(true);
                }else {
                    reportDTO.setIsOpen(false);
                }
//                Task task = flowBaseApi.getProcessInstanceActiveTask(, );
//                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//                String username = loginUser.getUsername();
//                if (StrUtil.isNotBlank(task.getAssignee())) {
//                    boolean isOpen = StrUtil.equals(username, task.getAssignee());
//                    reportDTO.setIsOpen(isOpen);
//                }
            }
            if (reportDTO.getTaskId() == null && reportDTO.getState() == 0) {
                reportDTO.setIsOpen(true);
            }
            if (reportDTO.getState() != 0 && reportDTO.getState() != 1) {
                reportDTO.setIsOpen(false);
            }

        }
        pageList.setRecords(reportDTOList);
        return Result.ok(pageList);
    }

    /**
     * 生产日报审核分页列表查询
     * @param pageList
     * @param faultProduceReport
     * @param beginDay
     * @param endDay
     * @return
     */
    @Override
    public Result<IPage<FaultProduceReportDTO>> queryPageAuditList(Page<FaultProduceReportDTO> pageList, FaultProduceReport faultProduceReport, String beginDay, String endDay) {
        // 获取到当前登录的用户的专业(majorCode、可能有多个，使用List存储)
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        List<String> majorCodeList = new ArrayList<>();
        for (CsUserMajorModel csUserMajorModel : CsUserMajorModelList) {
            majorCodeList.add(csUserMajorModel.getMajorCode());
        }
        // 如果查询参数有majorCode，这个majorCode在当前登录的用户的专业内，查询的专业只查询这个majorCode，不然查询的空
        if (faultProduceReport.getMajorCode() != null) {
            if (majorCodeList.contains(faultProduceReport.getMajorCode())) {
                majorCodeList.clear();
                majorCodeList.add(faultProduceReport.getMajorCode());
            } else {
                // 查询参数有majorCode，但是当前登录的用户的专业不包含majorCode，返回空数据
                return Result.ok(pageList);
            }
        }

        // 不传时间参数，默认查询所有
        // 传有时间参数的话，统计时间大于等于开始时间，小于等于结束时间
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 时间是否是指定格式(日期格式：yyyy-MM-dd)， 不是指定格式的话，舍弃
        if (beginDay != null) {
            try {
                DateUtils.parseDate(beginDay + " 00:00:00", pattern);
            } catch (Exception ignored) {
                beginDay = null;
            }
        }
        if (endDay != null) {
            try {
                DateUtils.parseDate(endDay + " 23:59:59", pattern);
            } catch (Exception ignored) {
                endDay = null;
            }
        }
        //获取当前用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(sysUser)) {
            throw  new AiurtBootException("请重新登录!");
        }
        List<FaultProduceReportDTO> reportDTOList = produceReportMapper.queryPageAuditList(pageList, sysUser.getUsername(), majorCodeList, beginDay, endDay);
        for (FaultProduceReportDTO reportDTO: reportDTOList) {
            List<String> csMajorNamesByCodes = iSysBaseAPI.getCsMajorNamesByCodes(Collections.singletonList(reportDTO.getMajorCode()));
            String majorName = null;
            if (csMajorNamesByCodes.size() > 0) {
                majorName = csMajorNamesByCodes.get(0);
            }
            reportDTO.setMajorName(majorName);  // 设置专业名称
            // 设置提交人的realname
            if (reportDTO.getSubmitUserName() != null) {
                LoginUser submitUser = iSysBaseAPI.queryUser(reportDTO.getSubmitUserName());
                reportDTO.setSubmitUserRealname(submitUser.getRealname());
            }
        }
        pageList.setRecords(reportDTOList);
        return Result.ok(pageList);
    }

    @Override
    public void workSubmit(FaultProduceReport faultProduceReport) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultProduceReport produceReport = produceReportMapper.getDetail(faultProduceReport.getId());
        if(ObjectUtil.isEmpty(produceReport.getProcessInstanceId())){
            //引用流程开始接口
            StartBpmnDTO startBpmnDto  = new StartBpmnDTO();
            startBpmnDto.setModelKey("fault_produce_report");
            Map<String,Object> map = new HashMap<>(32);
            map.put("id",faultProduceReport.getId());
            map.put("majorCode",faultProduceReport.getMajorCode());
            map.put("statisticsDate",faultProduceReport.getStatisticsDate());
            map.put("startTime",faultProduceReport.getStartTime());
            map.put("endTime",faultProduceReport.getEndTime());
            map.put("submitUserName",faultProduceReport.getSubmitUserName());
            map.put("submitTime",faultProduceReport.getSubmitTime());
            map.put("totalNum",faultProduceReport.getTotalNum());
            map.put("delayNum",faultProduceReport.getDelayNum());
            map.put("state",faultProduceReport.getState());
            startBpmnDto.setBusData(map);
            FlowTaskCompleteCommentDTO flowTaskCompleteCommentDTO = new FlowTaskCompleteCommentDTO();
            startBpmnDto.setFlowTaskCompleteDTO(flowTaskCompleteCommentDTO);
            flowTaskCompleteCommentDTO.setApprovalType("save");
            flowBaseApi.startAndTakeFirst(startBpmnDto);
            FaultProduceReport detail = produceReportMapper.getDetail(faultProduceReport.getId());
            TaskCompleteDTO taskCompleteDTO = new TaskCompleteDTO();
            Map<String,Object> detailMap = new HashMap<>(32);
            detailMap.put("id",detail.getId());
            taskCompleteDTO.setTaskId(detail.getTaskId());
            taskCompleteDTO.setProcessInstanceId(detail.getProcessInstanceId());
            taskCompleteDTO.setBusData(detailMap);
            FlowTaskCompleteCommentDTO commentDTO = new FlowTaskCompleteCommentDTO();
            commentDTO.setApprovalType("agree");
            taskCompleteDTO.setFlowTaskCompleteDTO(commentDTO);
            flowBaseApi.completeTask(taskCompleteDTO);
        }
        else {
            TaskCompleteDTO taskCompleteDTO = new TaskCompleteDTO();
            Map<String,Object> map = new HashMap<>(32);
            map.put("id",faultProduceReport.getId());
            map.put("reportLineDetailDTOList",new ArrayList<>());
            taskCompleteDTO.setTaskId(produceReport.getTaskId());
            taskCompleteDTO.setProcessInstanceId(produceReport.getProcessInstanceId());
            taskCompleteDTO.setBusData(map);
            FlowTaskCompleteCommentDTO flowTaskCompleteCommentDTO = new FlowTaskCompleteCommentDTO();
            flowTaskCompleteCommentDTO.setApprovalType("agree");
            taskCompleteDTO.setFlowTaskCompleteDTO(flowTaskCompleteCommentDTO);
            flowBaseApi.completeTask(taskCompleteDTO);
        }
    }

    /**
     * 导出生产日报多个excel，导出成zip压缩包
     * @param faultProduceReportDTO
     * @param request
     * @param response
     */
    @Override
    public void exportZip(FaultProduceReportDTO faultProduceReportDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 根据条件，查询出reportId
        String beginDay = faultProduceReportDTO.getBeginDay();
        String endDay = faultProduceReportDTO.getEndDay();
        List<String> selections = faultProduceReportDTO.getSelections();

        // 获取到当前登录的用户的专业(majorCode、可能有多个，使用List存储)
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        List<String> majorCodeList = new ArrayList<>();
        for (CsUserMajorModel csUserMajorModel : CsUserMajorModelList) {
            majorCodeList.add(csUserMajorModel.getMajorCode());
        }
        // 如果查询参数有majorCode，这个majorCode在当前登录的用户的专业内，查询的专业只查询这个majorCode，不然查询的空
        if (faultProduceReportDTO.getMajorCode() != null) {
            if (majorCodeList.contains(faultProduceReportDTO.getMajorCode())) {
                majorCodeList.clear();
                majorCodeList.add(faultProduceReportDTO.getMajorCode());
            } else {
                // 查询参数有majorCode，但是当前登录的用户的专业不包含majorCode，返回空数据
                throw new AiurtBootException("没有数据");
            }
        }

        // 不传时间参数，默认查询所有
        // 传有时间参数的话，统计时间大于等于开始时间，小于等于结束时间
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 时间是否是指定格式(日期格式：yyyy-MM-dd)， 不是指定格式的话，舍弃
        if (beginDay != null) {
            try {
                DateUtils.parseDate(beginDay + " 00:00:00", pattern);
            } catch (Exception ignored) {
                beginDay = null;
            }
        }
        if (endDay != null) {
            try {
                DateUtils.parseDate(endDay + " 23:59:59", pattern);
            } catch (Exception ignored) {
                endDay = null;
            }
        }
//        List<FaultProduceReportDTO> reportDTOList = produceReportMapper.queryPageList(new Page<>(), majorCodeList, beginDay, endDay);
        List<String> reportDTOIdList = produceReportMapper.selectIdList(majorCodeList, beginDay, endDay);
        // 没查询出数据，抛出异常
        if (reportDTOIdList.isEmpty()) {
            throw new AiurtBootException("未找到数据");
        }
        // 如果 selections 是空的话，默认导出查询的全部,不然就是 selections 和 reportDTOList的id列表的并集
        List<String> reportIdList;
//        List<String> reportDTOIdList = reportDTOList.stream().map(FaultProduceReport::getId).collect(Collectors.toList());
        if (selections == null || selections.isEmpty()) {
            reportIdList = reportDTOIdList;
        }else {
            reportIdList = selections.stream().filter(reportDTOIdList::contains).collect(Collectors.toList());
        }

        // 设置返回的是附件，设置附件名称
        String attachName = new String("生产日报表.zip".getBytes(), StandardCharsets.UTF_8);
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + attachName);

        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

        for (int i = 0; i < reportIdList.size(); i++) {
            String reportId = reportIdList.get(i);
            // 压缩包里面的excel表名
            String filename = "生产日报_" + (i + 1) + ".xls";
            //创新新的 ZipEntry
            ZipEntry zipEntry = new ZipEntry(filename);
            zipOut.putNextEntry(zipEntry);
            // 获得文件内容
            byte[] bytes = getOutPutBytesByReportId(reportId);
            // 写入文件内容
            assert bytes != null;
            zipOut.write(bytes, 0, bytes.length);
        }
        zipOut.close();
    }

    @Override
    public void exportExcel(FaultProduceReportDTO faultProduceReportDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 根据条件，查询出reportId
        String beginDay = faultProduceReportDTO.getBeginDay();
        String endDay = faultProduceReportDTO.getEndDay();
        List<String> selections = faultProduceReportDTO.getSelections();

        // 导出的是excel，selections只有一条数据
        if (selections == null || !(selections.size() == 1)) {
            throw new AiurtBootException("导出多条数据请使用导出zip的接口");
        }
        // 获取生产日报id
        String reportId = selections.get(0);

        // 获取到当前登录的用户的专业(majorCode、可能有多个，使用List存储)
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserMajorModel> CsUserMajorModelList = iSysBaseAPI.getMajorByUserId(user.getId());
        List<String> majorCodeList = new ArrayList<>();
        for (CsUserMajorModel csUserMajorModel : CsUserMajorModelList) {
            majorCodeList.add(csUserMajorModel.getMajorCode());
        }
        // 如果查询参数有majorCode，这个majorCode在当前登录的用户的专业内，查询的专业只查询这个majorCode，不然查询的空
        if (faultProduceReportDTO.getMajorCode() != null) {
            if (majorCodeList.contains(faultProduceReportDTO.getMajorCode())) {
                majorCodeList.clear();
                majorCodeList.add(faultProduceReportDTO.getMajorCode());
            } else {
                // 查询参数有majorCode，但是当前登录的用户的专业不包含majorCode，返回空数据
                throw new AiurtBootException("没有数据");
            }
        }

        // 不传时间参数，默认查询所有
        // 传有时间参数的话，统计时间大于等于开始时间，小于等于结束时间
        String[] pattern = new String[]{"yyyy-MM-dd HH:mm:ss"};
        // 时间是否是指定格式(日期格式：yyyy-MM-dd)， 不是指定格式的话，舍弃
        if (beginDay != null) {
            try {
                DateUtils.parseDate(beginDay + " 00:00:00", pattern);
            } catch (Exception ignored) {
                beginDay = null;
            }
        }
        if (endDay != null) {
            try {
                DateUtils.parseDate(endDay + " 23:59:59", pattern);
            } catch (Exception ignored) {
                endDay = null;
            }
        }
//        List<FaultProduceReportDTO> reportDTOList = produceReportMapper.queryPageList(new Page<>(), majorCodeList, beginDay, endDay);
//        List<String> reportDTOIdList = reportDTOList.stream().map(FaultProduceReport::getId).collect(Collectors.toList());
        List<String> reportDTOIdList = produceReportMapper.selectIdList(majorCodeList, beginDay, endDay);
        // 如果 reportId不在 reportDTOIdList 里面，抛出没有数据异常
        if (!reportDTOIdList.contains(reportId)) {
            throw new AiurtBootException("查询条件没有数据");
        }

//        // 没查询出数据，抛出异常
//        if (reportDTOList.isEmpty()) {
//            throw new AiurtBootException("未找到数据");
//        }

        // 获取生产日报excel的byte字节
        byte[] fileByte = getOutPutBytesByReportId(reportId);

        // 设置返回的是附件，设置附件名称
        String attachName = new String("生产日报.xls".getBytes(), StandardCharsets.UTF_8);
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + attachName);

        ServletOutputStream out = response.getOutputStream();
        assert fileByte != null;
        out.write(fileByte,0, fileByte.length);  // 写入文件
        out.close();
    }

    /**
     * 根据reportId,获取生产日报导出excel的输出流的bytes
     * @param reportId
     * @return
     */
    private byte[] getOutPutBytesByReportId(String reportId) {
        // 根据生产日报id，获取专业和统计日期
        FaultProduceReport report = this.getById(reportId);
        if (report == null){
            log.info("没有找到生产日报数据");
            return null;
        }
        Date statisticsDate = report.getStatisticsDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String statisticsString = dateFormat.format(statisticsDate);  // 统计日期，字符串格式
        List<String> majorNameList = iSysBaseAPI.getCsMajorNamesByCodes(Collections.singletonList(report.getMajorCode()));
        String majorName = majorNameList.get(0);  // 专业名称

        // Map作为每一行的数据容器，List作为行的容器
        List<Map> rowDataList = new ArrayList<>();

        // 获取所有线路
        List<CsLine> allLine = iSysBaseAPI.getAllLine();
        // 使用一个map存储线路与其对应的故障总数和延误次数，lineMap->{lineCode:故障总数/延误次数}
        // 使用一个map存储lineCode和lineName的对应关系，lineCodeNameMap->{lineCode:lineName}
        // 使用一个map来存储lineCode对应的故障清单，lineCodeDetailMap->{lineCode:[reportLineDetail]}
        Map<String, String> lineMap = new HashMap<>();
        Map<String, String> lineCodeNameMap = new HashMap<>();
        Map<String, List<FaultProduceReportLineDetail>> lineCodeDetailMap = new HashMap<>();
        allLine.forEach(line -> {
            lineMap.put(line.getLineCode(), "0/0");
            lineCodeNameMap.put(line.getLineCode(), line.getLineName());
            lineCodeDetailMap.put(line.getLineCode(), new ArrayList<>());
        });

        // 根据生产日报id获取故障清单
        LambdaQueryWrapper<FaultProduceReportLineDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultProduceReportLineDetail::getFaultProduceReportId, reportId);
        List<FaultProduceReportLineDetail> reportLineDetailList = iFaultProduceReportLineDetailService.list(queryWrapper);

        reportLineDetailList.forEach(reportLineDetail->{
            String lineCode = reportLineDetail.getLineCode();
            // 添加reportLineDetail到lineCodeDetailMap
            // del_flag = 1的线路不统计
            if (lineCodeDetailMap.get(lineCode) != null){
                lineCodeDetailMap.get(lineCode).add(reportLineDetail);
                // 计算故障总数和延误次数
                String totalNumAndDelayNum = lineMap.get(lineCode);
                String[] totalNumAndDelayNumSplit = totalNumAndDelayNum.split("/");
                int totalNum = Integer.parseInt(totalNumAndDelayNumSplit[0]);  // 故障总数
                int delayNum = Integer.parseInt(totalNumAndDelayNumSplit[1]);  // 延误次数
                totalNum = totalNum + 1;
                if (reportLineDetail.getAffectDrive() == 1 || reportLineDetail.getAffectPassengerService() == 1 || reportLineDetail.getIsStopService() == 1) {
                    delayNum = delayNum + 1;
                }
                lineMap.put(lineCode, totalNum + "/" + delayNum);
            }
        });
        // 往头部加入导致延误的故障次数/总故障次数，还有统计名称
        lineMap.put("statisticsName", "导致延误的故障次数/总故障次数");
        rowDataList.add(0, lineMap);
        // 循环，加入每行的【故障修复情况及管控措施】
        for (int i = 0; i < reportLineDetailList.size(); i++) {
            boolean emptyFlag = true;
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("statisticsName", "故障修复情况及管控措施");
            for (String lineCode: lineCodeDetailMap.keySet()){
                if (!lineCodeDetailMap.get(lineCode).isEmpty()) {
                    emptyFlag = false;
                    FaultProduceReportLineDetail reportLineDetail = lineCodeDetailMap.get(lineCode).remove(0);
                    rowMap.put(lineCode, reportLineDetail.getMaintenanceMeasures());
                } else {
                    rowMap.put(lineCode, "");
                }
            }
            if (emptyFlag) {
                break;
            }
            rowDataList.add(rowMap);
        }

        // 向尾部加入备注
        rowDataList.add(Collections.singletonMap("statisticsName", "备注：行车类设备设施故障情况（以上数据未经安技部分析，对故障情况及原因进行简单解释说明，非本中心原因造成的内容填写无）"));

        List<ExcelExportEntity> keyList = new ArrayList<>();
        keyList.add(new ExcelExportEntity("统计名称", "statisticsName", 30));
        // 插入表头
        for (String csLineCode: lineCodeNameMap.keySet()){
            keyList.add(new ExcelExportEntity(lineCodeNameMap.get(csLineCode), csLineCode, 15));
        }
        int lastRow = rowDataList.size() + 2;  // 最后一行，这行要合并单元格
        ExportParams exportParams = new ExportParams(majorName + "-运营生产日报-" + statisticsString, "行车类设备设施故障情况", "Sheet1");
        exportParams.setStyle(ExcelStylesUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, keyList, rowDataList);
        //CellRangeAddress(第几行开始，第几行结束，第几列开始，第几列结束)
        if (lastRow > 5) {
            // 两个单元格以上才能合并，不然报错
            workbook.getSheetAt(0).addMergedRegion(new CellRangeAddress(4, lastRow - 1, 0, 0));
        }
        if (allLine.size() > 1) {
            // 两个单元格以上才能合并，不然报错
            workbook.getSheetAt(0).addMergedRegion(new CellRangeAddress(lastRow, lastRow, 0, allLine.size()));
        }

        // 设置第二行和最后一行的样式
        CellStyle cellStyle = workbook.createCellStyle();
        // 上下居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 水平居左
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        //下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        //设置自动换行
        cellStyle.setWrapText(true);
        Cell cell = workbook.getSheetAt(0).getRow(1).getCell(0);
        Cell cell2 = workbook.getSheetAt(0).getRow(lastRow).getCell(0);
        cell.setCellStyle(cellStyle);
        cell2.setCellStyle(cellStyle);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception e) {
            log.error("读取输入流异常！", e);
            throw new AiurtBootException("读取生产日报数据异常");
        }
    }
}
