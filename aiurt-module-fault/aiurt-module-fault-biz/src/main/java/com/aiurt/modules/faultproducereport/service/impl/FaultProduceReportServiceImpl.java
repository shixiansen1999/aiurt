package com.aiurt.modules.faultproducereport.service.impl;

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
import com.aiurt.modules.faultproducereportlinedetail.service.IFaultProduceReportLineDetailService;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskCompleteDTO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
                    faultProduceReport.setState(1);
                case 1:
                    faultProduceReport.setState(1);
                    faultProduceReport.setSubmitTime(new Date());
                    faultProduceReport.setSubmitUserName(sysUser.getUsername());
                    break;
                case 2:

                    faultProduceReport.setState(0);
                    faultProduceReport.setSubmitTime(null);
                    updateWrapper.set(FaultProduceReport::getSubmitTime,null);
                    faultProduceReport.setSubmitUserName("");
                    break;
                case 3:
                    faultProduceReport.setState(1);
                case 4:
                    faultProduceReport.setState(0);
                    faultProduceReport.setSubmitTime(null);
                    updateWrapper.set(FaultProduceReport::getSubmitTime,null);
                    faultProduceReport.setSubmitUserName("");
                    break;
                case 5:
                    faultProduceReport.setState(2);
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
        produceReport.setState(1);
        produceReport.setSubmitTime(new Date());
        produceReport.setSubmitUserName(sysUser.getUsername());
        produceReportMapper.updateById(produceReport);
    }

}
