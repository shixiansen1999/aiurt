package com.aiurt.modules.worklog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.InspectionApi;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.enums.WorkLogCheckStatusEnum;
import com.aiurt.common.enums.WorkLogConfirmStatusEnum;
import com.aiurt.common.enums.WorkLogStatusEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.*;
import com.aiurt.common.util.RoleAdditionalUtils;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.api.DailyFaultApi;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.worklog.constans.WorkLogConstans;
import com.aiurt.modules.worklog.dto.WorkLogDTO;
import com.aiurt.modules.worklog.dto.WorkLogUserTaskDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.entity.WorkLogEnclosure;
import com.aiurt.modules.worklog.mapper.WorkLogEnclosureMapper;
import com.aiurt.modules.worklog.mapper.WorkLogMapper;
import com.aiurt.modules.worklog.param.LogCountParam;
import com.aiurt.modules.worklog.param.WorkLogParam;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Service
public class WorkLogServiceImpl extends ServiceImpl<WorkLogMapper, WorkLog> implements IWorkLogService {

    private final static String morningTime = "09:29:59";

    private final static String nightTime = "17:30:00";

    @Resource
    private WorkLogMapper depotMapper;

    @Resource
    private WorkLogEnclosureMapper enclosureMapper;
    @Resource
    private PatrolApi patrolApi;
    @Resource
    private InspectionApi inspectionApi;
    @Resource
    private DailyFaultApi dailyFaultApi;

    @Resource
    private RoleAdditionalUtils roleAdditionalUtils;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    private String schedule = "1.落实防疫规定，2.工区进行消毒，3.人员测温，4.对工区及材料库进行卫生清洁，5.对各站设备进行检修" ;
    /**
     * 新增工作日志
     * @param dto
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(WorkLogDTO dto, HttpServletRequest req) {
        WorkLog depot = new WorkLog();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        String logCode = generateLogCode();
        depot.setCode(logCode);
        depot.setOrgId(loginUser.getOrgId());
        depot.setSubmitId(userId);
        depot.setCreateBy(userId);

        depot.setFaultContent(dto.getFaultContent());

        //根据当前登录人id获取巡视待办消息
        depot.setPatrolContent(dto.getPatrolContent());
        //根据用户id和所在周的时间获取检修池内容

        dto.setRepairContent(dto.getRepairContent());
        depot.setRepairContent(dto.getRepairContent());
        depot.setStatus(dto.getStatus());
        depot.setConfirmStatus(0);
        depot.setCheckStatus(0);
        if (depot.getStatus()==1){
            depot.setSubmitTime(new Date());
        }
        depot.setWorkContent(dto.getWorkContent());
        depot.setContent(dto.getContent());

        //工作内容赋值
        depot.setIsDisinfect(dto.getIsDisinfect());
        depot.setIsClean(dto.getIsClean());
        depot.setIsAbnormal(dto.getIsAbnormal());
        depot.setIsEmergencyDisposal(dto.getIsEmergencyDisposal());
        depot.setIsDocumentPublicity(dto.getIsDocumentPublicity());
        if (dto.getIsEmergencyDisposal().equals(WorkLogConstans.IS)) {
            depot.setEmergencyDisposalContent(dto.getEmergencyDisposalContent());
        }
        if (dto.getIsDocumentPublicity().equals(WorkLogConstans.IS)) {
            depot.setDocumentPublicityContent(dto.getDocumentPublicityContent());
        }
        depot.setOtherWorkContent(dto.getOtherWorkContent());
        depot.setNote(dto.getNote());
        depot.setHandoverId(dto.getHandoverId());

        depot.setSucceedId(dto.getSucceedId());
        depot.setApproverId(dto.getApproverId());
        if (StringUtils.isNotBlank(dto.getApproverId())) {
            depot.setApprovalTime(new Date());
        }else {
            depot.setApprovalTime(null);
        }
        depot.setLogTime(dto.getLogTime());
        depot.setDelFlag(0);
        depot.setAssortTime(dto.getAssortTime());
        depot.setAssortLocation(dto.getAssortLocation());
        depot.setAssortUnit(dto.getAssortUnit());
        depot.setAssortIds(dto.getAssortIds());
        depot.setAssortNum(dto.getAssortNum());
        depot.setAssortContent(dto.getAssortContent());
        depot.setPatrolRepairContent(dto.getPatrolRepairContent());
        depotMapper.insert(depot);
        //插入附件列表
        if (ObjectUtil.isNotEmpty(dto.getUrlList())) {
            String[] urlList = dto.getUrlList().split(",");
            for (String s : urlList) {
                WorkLogEnclosure enclosure = new WorkLogEnclosure();
                enclosure.setCreateBy(depot.getCreateBy());
                enclosure.setParentId(depot.getId());
                enclosure.setType(0);
                enclosure.setUrl(s);
                enclosure.setDelFlag(0);
                enclosureMapper.insert(enclosure);
            }
        }
        //插入签名
        if (StringUtils.isNotBlank(dto.getSignature())) {
            WorkLogEnclosure enclosure = new WorkLogEnclosure();
            enclosure.setCreateBy(depot.getCreateBy());
            enclosure.setParentId(depot.getId());
            enclosure.setType(1);
            enclosure.setUrl(dto.getSignature());
            enclosure.setDelFlag(0);
            enclosureMapper.insert(enclosure);
        }
        //完成任务
        //不存在userTaskService
        //userTaskService.completeWork(userId, DateUtils.date2Str(depot.getSubmitTime(), new SimpleDateFormat("yyyy-MM-dd")));
        //发送待办消息
        if (StringUtils.isNotBlank(dto.getSucceedId())) {
            LoginUser userById = iSysBaseAPI.getUserById(dto.getSucceedId());
            dto.setSucceedUserName(userById.getUsername());
            sendMessage(dto);
        }
        return Result.ok("新增成功");
    }

    /**
     * 如果接班人不为空 发送待办消息
     * @param dto
     */
    private void sendMessage(WorkLogDTO dto) {
//            UserTaskAddParam addParam = new UserTaskAddParam();
            //List<String> list = new ArrayList<>();
          //  list.add(dto.getSucceedUserName());
//            addParam.setUserIds(list);
//            addParam.setType(UserTaskConstant.USER_TASK_TYPE_4);
//            addParam.setTitle("工作日志");
//            addParam.setLevel(9);
//            userTaskService.add(addParam);

            //todo 待处理
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            // 发消息
            BusMessageDTO messageDTO = new BusMessageDTO();
            messageDTO.setFromUser(sysUser.getUsername());
            messageDTO.setToUser(dto.getSucceedUserName());
            messageDTO.setToAll(false);
            messageDTO.setContent(dto.getContent()==null?"您有一条待接班日志":dto.getContent());
            messageDTO.setCategory("2");
            messageDTO.setTitle("您有一条待接班日志");
            messageDTO.setBusType(SysAnnmentTypeEnum.WORKLOG.getType());
            iSysBaseAPI.sendBusAnnouncement(messageDTO);



    }

    /**
     * 工作日志上报-分页列表查询
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<WorkLogResult> pageList(IPage<WorkLogResult> page, WorkLogParam param, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        param.setSubmitId(user.getId());
        param.setSuccessorId(user.getId());
        param.setDepartId(user.getOrgId());
        return getWorkLogResultIPage(page, param);
    }

    /**
     * 工作日志导出
     * @param param
     * @param req
     * @return
     */
    @Override
    public List<WorkLogResult> exportXls(WorkLogParam param, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> departIdsByUserId = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
        if (CollectionUtils.isNotEmpty(departIdsByUserId)) {
            param.setDepartList(departIdsByUserId);
        }
        List<WorkLogResult> workLogResults = depotMapper.exportXls(param);
        for (WorkLogResult record : workLogResults) {
            //通过站点和班组的关联获取线路
            /*SysDepart sysDepart = departService.getOne(new QueryWrapper<SysDepart>().eq(SysDepart.DEPART_NAME, record.getSubmitOrgName()), false);
            Station station = stationMapper.selectNameById(sysDepart.getId());
            if (ObjectUtil.isNotEmpty(station)) {
                String lineName = station.getLineName();
                record.setLineName(lineName);
            }*/
            if (StringUtils.isNotBlank(record.getAssortLocation())){
                List<String> codes = Arrays.asList(record.getAssortLocation().split(","));
                List<String> positions = new ArrayList<>();
                for (String code : codes) {
                    String position = iSysBaseAPI.getPosition(code);
                    positions.add(position);
                }
                record.setAssortLocationName(StringUtils.join(positions,","));
            }
            //提交状态
            record.setStatusDesc(WorkLogStatusEnum.findMessage(record.getStatus()));
            //确认状态
            record.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(record.getConfirmStatus()));
            //审核状态
            record.setCheckStatusDesc(WorkLogCheckStatusEnum.findMessage(record.getCheckStatus()));
            //交班人名称
            String handoverIds = record.getHandoverId();
            if (StrUtil.isNotEmpty(handoverIds)) {
                List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(handoverIds);
                String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
                record.setHandoverName(realNames);

            }
            //防疫相关工作
            StringBuffer stringBuffer = new StringBuffer();
            if (WorkLogConstans.IS.equals(record.getIsDisinfect())) {
                stringBuffer.append("完成工区消毒；");
            }else {stringBuffer.append("未完成工区消毒；");}

            if (WorkLogConstans.IS.equals(record.getIsClean())) {
                stringBuffer.append("完成工区卫生打扫；");
            }else { stringBuffer.append("未完成工区卫生打扫；");}

            if (WorkLogConstans.NORMAL.equals(record.getIsAbnormal())) {
                stringBuffer.append("班组上岗人员体温正常。");
            }else { stringBuffer.append("班组上岗人员体温异常。");}

            record.setAntiepidemicWork(stringBuffer.toString());

            record.setSchedule(schedule);

            StringBuffer stringBuffer2 = new StringBuffer();
            if (WorkLogConstans.IS.equals(record.getIsEmergencyDisposal())) {
                stringBuffer2.append("应急处理情况：");
                stringBuffer2.append(record.getEmergencyDisposalContent()+";");
            }
            stringBuffer2.append("防疫相关工作：");
            stringBuffer2.append(stringBuffer);
            if (WorkLogConstans.IS.equals(record.getIsDocumentPublicity())) {
                stringBuffer2.append("文件宣贯概况：");
                stringBuffer2.append(record.getDocumentPublicityContent()+";");
            }
            record.setWorkContent(stringBuffer2.toString());
        }
        return workLogResults;
    }

    /**
     * 工作日志确认-分页列表查询
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<WorkLogResult> queryConfirmList(IPage<WorkLogResult> page, WorkLogParam param, HttpServletRequest req) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        List<String> departIdsByUserId = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
        if (!admin) {
            param.setSubmitId(user.getId());
            param.setSuccessorId(user.getId());
            param.setDepartList(departIdsByUserId);
        }
        return getWorkLogResultIPage(page, param);
    }

    /**
     * 获取日志列表详情
     * @param page
     * @param param
     * @return
     */
    private IPage<WorkLogResult> getWorkLogResultIPage(IPage<WorkLogResult> page, WorkLogParam param) {
        IPage<WorkLogResult> result = depotMapper.queryWorkLog(page, param);
        List<WorkLogResult> records = result.getRecords();
        //todo 待处理
       List<CsStation> stationList = iSysBaseAPI.queryAllStation();
       //todo 后期修改
        List<SysDepartModel> departList = iSysBaseAPI.getAllSysDepart();
        Map<String, CsStation> stationIdMap = null;
        Map<String, List<CsStation>> stationTeamIdMap =null;
        Map<String, String> departMap = null;
       List<String> a = null;
        //todo 待处理
       if (CollectionUtils.isNotEmpty(stationList)){
           stationIdMap = stationList.stream().collect(Collectors.toMap(CsStation::getId, s -> s));
           stationTeamIdMap = stationList.stream().filter(f->f.getSysOrgCode()!=null).collect(Collectors.groupingBy(CsStation::getSysOrgCode));
       }
        if (CollectionUtils.isNotEmpty(departList)){
             a = departList.stream().map(SysDepartModel::getId).collect(Collectors.toList());
            departMap = departList.stream().collect(Collectors.toMap(SysDepartModel::getId, SysDepartModel::getDepartName));
        }

        Map<String,List<WorkLogEnclosure>> map0 = null;
        Map<String,List<WorkLogEnclosure>> map1 = null;

        if (CollectionUtils.isNotEmpty(records)){
            List<String> list = records.stream().map(WorkLogResult::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                //附件
                List<WorkLogEnclosure> list0 = enclosureMapper.selectList(new LambdaQueryWrapper<WorkLogEnclosure>()
                        .eq(WorkLogEnclosure::getType, 0)
                        .in(WorkLogEnclosure::getParentId, list));
                //签名
                List<WorkLogEnclosure> list1 = enclosureMapper.selectList(new LambdaQueryWrapper<WorkLogEnclosure>()
                        .eq(WorkLogEnclosure::getType, 1)
                        .in(WorkLogEnclosure::getParentId, list));
                if (CollectionUtils.isNotEmpty(list0)){
                    map0 = list0.stream().collect(Collectors.groupingBy(WorkLogEnclosure::getParentId));
                }
                if (CollectionUtils.isNotEmpty(list1)){
                    map1 = list1.stream().collect(Collectors.groupingBy(WorkLogEnclosure::getParentId));
                }
            }
        }

        for (WorkLogResult record : records) {

            if (departMap!=null && stationTeamIdMap!=null){
                String id = departMap.get(record.getSubmitOrgId());
                if (StringUtils.isNotBlank(id)){
                    List<CsStation> station = stationTeamIdMap.get(id);
                    if (CollectionUtils.isNotEmpty(station)){
                        record.setLineName(station.get(0).getLineName());
                    }
                }
            }

            if (StringUtils.isNotBlank(record.getAssortLocation()) && stationIdMap!=null){
                String[] list = record.getAssortLocation().split(",");
                List<String> strName = new ArrayList<>();
                for (String id : list) {
                    try {
                        CsStation station = stationIdMap.get(Integer.parseInt(id));
                        if (station!=null && StringUtils.isNotBlank(station.getStationName())) {
                            strName.add(station.getStationName());
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (CollectionUtils.isNotEmpty(strName)) {
                    record.setAssortLocationName(StringUtils.join(strName, ","));
                }
            }
            if (map0!=null){
                 List<WorkLogEnclosure> workLogEnclosures = map0.get(record.getId());
                if (CollectionUtils.isNotEmpty(workLogEnclosures)) {
                    record.setUrlList(workLogEnclosures.stream().map(WorkLogEnclosure::getUrl).collect(Collectors.joining(",")));
                }
            }
            if (map1!=null){
                List<WorkLogEnclosure> enclosures = map1.get(record.getId());
                if (CollectionUtils.isNotEmpty(enclosures)) {
                    record.setSignature(enclosures.stream().map(WorkLogEnclosure::getUrl).collect(Collectors.joining(",")));
                }
            }
            //提交状态
            record.setStatusDesc(WorkLogStatusEnum.findMessage(record.getStatus()));
            //确认状态
            record.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(record.getConfirmStatus()));
            //审核状态
            record.setCheckStatusDesc(WorkLogCheckStatusEnum.findMessage(record.getCheckStatus()));
            //配合施工时间
            String assortTime = record.getAssortTime();
            if (StrUtil.isNotBlank(assortTime)) {
                record.setAssortTimes(assortTime.split(","));
            }

            //获取时间年月日星期几
            Date logTime = record.getLogTime();
            String format = DateUtil.format(logTime, "yyyy年MM月dd日");
            String format2 = DateUtil.format(logTime, "yyyy-MM-dd");
            Week week = DateUtil.dayOfWeekEnum(DateUtil.date());
            record.setTime(format + week.toChinese());
            //获取是早班会17.30 还是晚班会9.30
            String am = format2+" " + morningTime;
            String pm = format2+" " + nightTime;
            if (record.getSubmitTime().after(DateUtil.parse(am)) && record.getSubmitTime().before(DateUtil.parse(pm))) {
                record.setClassTime("17时30分");
                record.setClassName("晚班会");
            } else {
                record.setClassTime("9时30分");
                record.setClassName("早班会");
            }
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String orgId = user.getOrgId();
            //查询该部门下的人员
            List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(orgId);
            //获取负责人
            SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(orgId);
            LoginUser userById = iSysBaseAPI.getUserById(sysDepartModel.getManagerId());
            if (ObjectUtil.isNotEmpty(userById)) {
                record.setForeman(userById.getRealname());
            }
            //获取参与人员
            List<String> nameList = sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList());
            String str = StringUtils.join(nameList, ",");
            record.setUserList(str);

            //交班人名称
            String handoverIds = record.getHandoverId();
            if (StrUtil.isNotEmpty(handoverIds)) {
                List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(handoverIds);
                String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
                record.setHandoverName(realNames);

            }
            //防疫相关工作
            StringBuffer stringBuffer = new StringBuffer();
            if (WorkLogConstans.IS.equals(record.getIsDisinfect())) {
                stringBuffer.append("完成工区消毒；");
            }else {stringBuffer.append("未完成工区消毒；");}

            if (WorkLogConstans.IS.equals(record.getIsClean())) {
                stringBuffer.append("完成工区卫生打扫；");
            }else { stringBuffer.append("未完成工区卫生打扫；");}

            if (WorkLogConstans.NORMAL.equals(record.getIsAbnormal())) {
                stringBuffer.append("班组上岗人员体温正常。");
            }else { stringBuffer.append("班组上岗人员体温异常。");}

            record.setAntiepidemicWork(stringBuffer.toString());

            record.setSchedule(schedule);

            StringBuffer stringBuffer2 = new StringBuffer();
            if (WorkLogConstans.IS.equals(record.getIsEmergencyDisposal())) {
                stringBuffer2.append("应急处理情况：");
                stringBuffer2.append(record.getEmergencyDisposalContent()+";");
            }
            stringBuffer2.append("防疫相关工作：");
            stringBuffer2.append(stringBuffer);
            if (WorkLogConstans.IS.equals(record.getIsDocumentPublicity())) {
                stringBuffer2.append("文件宣贯概况：");
                stringBuffer2.append(record.getDocumentPublicityContent()+";");
            }
            record.setWorkContent(stringBuffer2.toString());

            //
            String faultContent = record.getFaultContent();
            if (StrUtil.isBlank(faultContent)) {
                record.setFaultContent("无");
            }

            String repairContent = record.getRepairContent();
            if (StrUtil.isBlank(repairContent)) {
                record.setRepairContent("无");
            }

            String patrolContent = record.getPatrolContent();
            if (StrUtil.isBlank(patrolContent)) {
                record.setPatrolContent("无");
            }

            Object otherWorkContent = record.getOtherWorkContent();
            if (Objects.isNull(otherWorkContent)) {
                record.setOtherWorkContent("无");
            }

            Object content = record.getContent();
            if (Objects.isNull(content)) {
                record.setContent("无");
            }
        }
        return result;
    }

    /**
     * 根据id假删除
     * @param id
     * @return
     */
    @Override
    public Result<?> deleteById(String id) {
        WorkLog workLog = this.getOne(new QueryWrapper<WorkLog>().eq(WorkLog.ID, id), false);
        if (workLog.getConfirmStatus().equals(WorkLogConfirmStatusEnum.YQR.getCode())) {
            throw new AiurtBootException("已确认状态不能删除");
        }
        depotMapper.deleteOne(id);
        return Result.ok();
    }

    /**
     * 查询日志详情
     * @param id
     * @return
     */
    @Override
    public WorkLogDTO getDetailById(String id) {
        WorkLogResult workLog = depotMapper.queryById(id);
        WorkLogDTO workLogDTO = new WorkLogDTO();
        BeanUtil.copyProperties(workLog,workLogDTO);
        if(ObjectUtil.isNotEmpty(workLog.getAssortLocation()))
        {
            List<String> codes = Arrays.asList(workLog.getAssortLocation().split(","));
            String assortLocationName = null;
            for (String code : codes) {
                String position = iSysBaseAPI.getPosition(code);
                if (assortLocationName == null) {
                    assortLocationName = position;
                } else {
                    assortLocationName = assortLocationName + position;
                }
            }
            workLogDTO.setAssortLocationName(assortLocationName);

        }
        if(ObjectUtil.isNotEmpty(workLog.getSucceedId()))
        {
            LoginUser successor = iSysBaseAPI.getUserById(workLog.getSucceedId());
            workLog.setSucceedName(successor.getRealname());
            workLogDTO.setSucceedUserName(successor.getUsername());
        }
        //提交状态
        workLogDTO.setStatusDesc(WorkLogStatusEnum.findMessage(workLog.getStatus()));
        //确认状态
        workLogDTO.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(workLog.getConfirmStatus()));
        //审核状态
        workLogDTO.setCheckStatusDesc(WorkLogCheckStatusEnum.findMessage(workLog.getCheckStatus()));

        //配合施工参与人姓名
        if(ObjectUtil.isNotEmpty(workLog.getAssortIds()))
        {

            String[] split1 = workLog.getAssortIds().split(",");
            List<LoginUser> assortNames = iSysBaseAPI.queryAllUserByIds(split1);
            String collect1 = assortNames.stream().map(s -> s.getRealname()).collect(Collectors.joining(","));
            String s = assortNames.stream().map(u -> u.getUsername()).collect(Collectors.joining(","));
            workLogDTO.setAssortNames(collect1);
            workLogDTO.setAssortUserNames(s);
        }
        //附件列表
        List<String> query = enclosureMapper.query(id,0);
        String collect = query.stream().collect(Collectors.joining(","));
        //签名列表
        List<String> query1 = enclosureMapper.query(id,1);
        String signUrl = query1.stream().collect(Collectors.joining(","));
        workLogDTO.setUrlList(collect);
        workLogDTO.setSignature(signUrl);

        //交班人名称
        String handoverIds = workLog.getHandoverId();
        if (StrUtil.isNotEmpty(handoverIds)) {
            List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(handoverIds);
            String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
            workLogDTO.setHandoverName(realNames);

        }
        return workLogDTO;
    }

    /**
     * 通过id确认
     * @param id
     * @return
     */
    @Override
    public Result<?> confirm(Integer id) {
        depotMapper.confirm(id);
        return Result.ok();
    }

    /**
     * 批量确认
     * @param ids
     * @return
     */
    @Override
    public Result<?> checkByIds(String ids) {
        String[] split = ids.split(",");
        for (String s : split) {
            depotMapper.check(s);
        }
        return Result.ok();
    }

    /**
     * 根据当前登录人id获取巡检检修故障待办消息
     * @param nowday
     * @return
     */
    @Override
    public Result<LogResult>  getWaitMessage(String nowday, HttpServletRequest req) {

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        LogResult logResult = new LogResult();
        //根据当前登录人id获取故障待办消息
        //todo 待处理
//        List<FaultRepairRecordResult> message = recordMapper.getWaitMessage(userId,nowday+" 00:00:00",nowday+" 23:59:59");
//        if (CollectionUtils.isNotEmpty(message)) {
//            List<String> faultCodes = new ArrayList<String>();
//            List<String> faultContent = new ArrayList<String>();
//            for (FaultRepairRecordResult result : message) {
//                faultCodes.add(result.getFaultCode());
//                faultContent.add(result.getFaultPhenomenon());
//            }
//            String str = StringUtils.join(faultCodes, ",");
//            String str1 = StringUtils.join(faultContent, ",");
//            if (StringUtils.isNotBlank(str)) {
//                logResult.setFaultCodes(str);
//            }
//            if (StringUtils.isNotBlank(str1)) {
//                logResult.setFaultContent(str1);
//            }
//        }
        // todo 待处理

//        //根据当前登录人id获取巡检待办消息
//        List<PatrolTask> patrolTasks = patrolTaskMapper.selectList(new LambdaQueryWrapper<PatrolTask>()
//                .select(PatrolTask::getCode, PatrolTask::getPatrolPoolId)
//                .between(PatrolTask::getCreateTime, nowday+" "+"00:00:00", nowday+" " +"23:59:59")
//                .like(PatrolTask::getStaffIds, userId)
//        );
//        if (CollUtil.isNotEmpty(patrolTasks)) {
//            List<Long> patrolList = patrolTasks.stream().map(PatrolTask::getPatrolPoolId).collect(Collectors.toList());
//
//            List<PatrolPool> patrolPools = patrolPoolMapper.selectList(new LambdaQueryWrapper<PatrolPool>()
//                    .select(PatrolPool::getId, PatrolPool::getPatrolName)
//                    .in(PatrolPool::getId, patrolList));
//
//            String ids = StringUtils.join(patrolPools.stream().filter(p -> {
//                if (p.getId()!=null) {
//                    return true;
//                }
//                return false;
//            }).map(PatrolPool::getId).collect(Collectors.toList()), ",");
//
//            String title = StringUtils.join(patrolPools.stream().filter(p -> {
//                if (StringUtils.isNotBlank(p.getPatrolName())) {
//                    return true;
//                }
//                return false;
//            }).map(PatrolPool::getPatrolName).collect(Collectors.toList()), ",");
//
//            if (StringUtils.isNotBlank(ids)) {
//                logResult.setPatrolIds(ids);
//            }
//            if (StringUtils.isNotBlank(title)) {
//                logResult.setPatrolContent(title);
//            }
//        }
        //todo 待处理
        //获取检修待办消息
//        Result task = repairTaskService.getRepairTaskByUserIdAndTime(userId, nowday);
//        if (ObjectUtil.isNotEmpty(task.getResult())) {
//            RepairRecordVO result = (RepairRecordVO) task.getResult();
//
//            logResult.setRepairCode(result.getRepairTaskCode());
//            List<String> content = result.getRepairPoolContent();
//            List<String> contents = new ArrayList<String>();
//            for (String s : content) {
//                contents.add(s);
//            }
//            String str2 = StringUtils.join(contents, ",");
//            logResult.setRepairContent(str2);
//        }
        return Result.ok(logResult);
    }

    /**
     * 编辑工作日志
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editWorkLog(WorkLogDTO dto) {
        WorkLog workLog = this.getOne(new QueryWrapper<WorkLog>().eq(WorkLog.ID, dto.getId()), false);
        workLog.setLogTime(dto.getLogTime());
        workLog.setWorkContent(dto.getWorkContent());
        workLog.setContent(dto.getContent());
        workLog.setSucceedId(dto.getSucceedId());
        workLog.setAssortTime(dto.getAssortTime());
        workLog.setAssortLocation(dto.getAssortLocation());
        workLog.setAssortIds(dto.getAssortIds());
        workLog.setAssortNum(dto.getAssortNum());
        workLog.setAssortUnit(dto.getAssortUnit());
        workLog.setFaultContent(dto.getFaultContent());
        workLog.setPatrolRepairContent(dto.getPatrolRepairContent());
        workLog.setAssortContent(dto.getAssortContent());
        workLog.setStatus(dto.getStatus());

        //工作内容赋值
        workLog.setIsDisinfect(dto.getIsDisinfect());
        workLog.setIsClean(dto.getIsClean());
        workLog.setIsAbnormal(dto.getIsAbnormal());
        workLog.setIsEmergencyDisposal(dto.getIsEmergencyDisposal());
        workLog.setIsDocumentPublicity(dto.getIsDocumentPublicity());
        if (dto.getIsEmergencyDisposal().equals(WorkLogConstans.IS)) {
            workLog.setEmergencyDisposalContent(dto.getEmergencyDisposalContent());
        }else {
            workLog.setEmergencyDisposalContent(null);
        }
        if (dto.getIsDocumentPublicity().equals(WorkLogConstans.IS)) {
            workLog.setDocumentPublicityContent(dto.getDocumentPublicityContent());
        }else {
            workLog.setDocumentPublicityContent(null);
        }
        workLog.setOtherWorkContent(dto.getOtherWorkContent());
        workLog.setNote(dto.getNote());
        workLog.setHandoverId(dto.getHandoverId());

        this.updateById(workLog);
        //删除原附件列表
        enclosureMapper.deleteByName(workLog.getId());
        //重新插入附件列表
        String[] urlList = dto.getUrlList().split(",");
        if (ObjectUtil.isNotEmpty(urlList)) {
            for (String s : urlList) {
                WorkLogEnclosure enclosure = new WorkLogEnclosure();
                enclosure.setParentId(workLog.getId());
                enclosure.setUrl(s);
                enclosure.setType(0);
                enclosure.setCreateBy(workLog.getCreateBy());
                enclosure.setDelFlag(0);
                enclosureMapper.insert(enclosure);
            }
        }
        //插入签名
        if (StringUtils.isNotBlank(dto.getSignature())) {
            WorkLogEnclosure enclosure = new WorkLogEnclosure();
            enclosure.setCreateBy(workLog.getCreateBy());
            enclosure.setParentId(workLog.getId());
            enclosure.setType(1);
            enclosure.setUrl(dto.getSignature());
            enclosure.setDelFlag(0);
            enclosureMapper.insert(enclosure);
        }
        //如果接班人不为空 发送待办消息
        if(ObjectUtil.isNotEmpty(dto.getSucceedId()))
        {
            List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(dto.getSucceedId());
            String usernames = jsonObjects.stream().map(js -> js.getString("username")).collect(Collectors.joining(","));
            dto.setSucceedUserName(usernames);
            sendMessage(dto);
        }
    }

    /**
     * 日志统计
     * @param param
     * @return
     */
    @Override
    public List<LogCountResult> getLogCount(LogCountParam param) {
        int defaultDayNum = 2;
        if (param.getDayStart() == null || param.getDayEnd() == null) {
            LocalDate now = LocalDate.now();
            LocalDateTime dayStart = now.atTime(00, 00, 00);
            LocalDateTime dayEnd = now.atTime(23, 59, 59);
            param.setDayStart(dayStart);
            param.setDayEnd(dayEnd);
        }
        List<LogCountResult> logCountResults = this.baseMapper.selectOrgLogCount(param);
        for (LogCountResult logCountResult : logCountResults) {
            int unSubmitNum = defaultDayNum - logCountResult.getSubmitNum();
            logCountResult.setUnSubmitNum(unSubmitNum>0?unSubmitNum:0);
        }
        return logCountResults;
    }

    /**
     * 首页工作日志提交数量
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Result<LogSubmitCount> getLogSubmitNum(String startTime, String endTime) {
        /*LogSubmitCount logSubmitCount = new LogSubmitCount();
        Long num = (long)depotMapper.selectCount(new LambdaQueryWrapper<WorkLog>()
                .between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), WorkLog::getSubmitTime, startTime, endTime));
        logSubmitCount.setSubmitNum(num);
        return Result.ok(logSubmitCount);*/
        //巡视数量根据用户权限查询
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getId();
        List<String> orgIds = roleAdditionalUtils.getListDepartIdsByUserId(userId);
        LogSubmitCount logSubmitCount = new LogSubmitCount();
        Long num = depotMapper.selectCount(new LambdaQueryWrapper<WorkLog>()
                .between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), WorkLog::getSubmitTime, startTime, endTime).in(WorkLog::getOrgId, orgIds));
        logSubmitCount.setSubmitNum(num);
        return Result.ok(logSubmitCount);
    }

    @Override
    public WorkLogUserTaskDTO getUseTask() {
        WorkLogUserTaskDTO patrolWorkLogDTO = new WorkLogUserTaskDTO();
        /*//获取巡检内容
        String userPatrolTask = patrolApi.getUserTask();
        //获取检修内容
        String inspectionTaskDevice = inspectionApi.getInspectionTaskDevice();
        //获取故障内容
        String faultContent = dailyFaultApi.getFaultTask();
        patrolWorkLogDTO.setPatrolContent(userPatrolTask);
        patrolWorkLogDTO.setRepairContent(inspectionTaskDevice);
        patrolWorkLogDTO.setFaultContent(faultContent);*/
        return  patrolWorkLogDTO;
    }

    /**
     * 生成日志编号
     * @return
     */
    private String generateLogCode() {
        String code = "L";
        LocalDate now = LocalDate.now();
        int dayOfYear = now.getYear();
        String s = String.valueOf(dayOfYear);
        //年份
        String substring = s.substring(s.length() - 2);
        //月份
        int monthValue = now.getMonthValue();
        String s1 = String.valueOf(monthValue);
        if (monthValue< CommonConstant.MONTH_VALUE) {
            s1="0"+s1;
        }
        int dayOfMonth = now.getDayOfMonth();
        String s2 = String.valueOf(dayOfMonth);
        if (dayOfMonth < CommonConstant.MONTH_VALUE) {
            s2="0"+s2;
        }
        Integer integer = depotMapper.selectWorkLogCount();
        String num = null;
        if (integer<CommonConstant.VALUE_1) {
            num ="00" + String.valueOf(integer+1);
        }else if (integer > CommonConstant.VALUE_2 && integer< CommonConstant.VALUE_3) {
            num ="0" + String.valueOf(integer+1);
        }else {
            num = String.valueOf(integer+1);
        }
        return code+substring+s1+s2+"."+num;
    }

    @Override
    public Map getTodayJobContent(String nowday) {

        Date date = DateUtil.date();
        DateTime startTime;
        DateTime endTime;
        if (StrUtil.isEmpty(nowday)) {
            nowday = DateUtil.today();
        }

        //获取是晚班会17.30 还是早班会9.30
        String am = nowday+" 09:29:59";
        String pm = nowday+" 17:30:00";

        //昨天时间
        DateTime lastDay = DateUtil.offsetDay(date, -1);
        String lastPM = DateUtil.format(lastDay, "yyyy-MM-dd")+ " 17:30:00";

        if (date.after(DateUtil.parse(am)) && date.before(DateUtil.parse(pm))) {
            //晚班
            startTime = DateUtil.parse(lastPM);
            endTime = DateUtil.parse(am);
        } else {
            //早班
            startTime = DateUtil.parse(nowday+" 09:30:00");
            endTime = DateUtil.parse(nowday+" 17:29:59");
        }

        HashMap<String, String> map = new HashMap<>(16);

        //获取巡检内容
        HashMap<String, String> userTask = patrolApi.getUserTask(startTime, endTime);
        //获取检修内容
        HashMap<String, String> inspectionTaskDevice = inspectionApi.getInspectionTaskDevice(startTime, endTime);
        //获取故障内容
        HashMap<String, String> faultTask = dailyFaultApi.getFaultTask(startTime, endTime);
        map.put("patrolContent", userTask.get("content"));
        map.put("repairContent", inspectionTaskDevice.get("content"));
        map.put("faultContent", faultTask.get("content"));
        map.put("patrolCode", userTask.get("code"));
        map.put("repairCode", inspectionTaskDevice.get("code"));
        map.put("faultCode", faultTask.get("code"));
        return map;
    }


    @Override
    public WorkLogDetailResult queryWorkLogDetail(String id) {
        WorkLogDetailResult workLog = depotMapper.queryWorkLogById(id);
        //签名列表
        List<String> query1 = enclosureMapper.query(id,1);

        workLog.setSignature(query1);
        //获取时间年月日星期几
        Date logTime = workLog.getLogTime();
        String format = DateUtil.format(logTime, "yyyy年MM月dd日");
        String format2 = DateUtil.format(logTime, "yyyy-MM-dd");
        Week week = DateUtil.dayOfWeekEnum(DateUtil.parse(format2));
        workLog.setTime(format + week.toChinese());
        //获取是早班会17.30 还是晚班会9.30
        String am = format2+" "+ morningTime;
        String pm = format2+" "+ nightTime;
        if (workLog.getSubmitTime().after(DateUtil.parse(am)) && workLog.getSubmitTime().before(DateUtil.parse(pm))) {
            workLog.setClassTime("17时30分");
            workLog.setClassName("晚班会");
        } else {
            workLog.setClassTime("9时30分");
            workLog.setClassName("早班会");
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgId = user.getOrgId();
        List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(orgId);
        //获取负责人
        SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(orgId);
        LoginUser userById = iSysBaseAPI.getUserById(sysDepartModel.getManagerId());
        if (ObjectUtil.isNotEmpty(userById)) {
            workLog.setForeman(userById.getRealname());
        }

        //获取参与人员
        List<String> nameList = sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList());
        String str = StringUtils.join(nameList, ",");
        workLog.setUserList(str);

        //交班人姓名
        String handoverId = workLog.getHandoverId();
        if (StrUtil.isNotEmpty(handoverId)) {
            List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(handoverId);
            String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
            workLog.setHandoverName(realNames);
        }

        //防疫相关工作
        StringBuffer stringBuffer = new StringBuffer();
        if (WorkLogConstans.IS.equals(workLog.getIsDisinfect())) {
            stringBuffer.append("完成工区消毒；");
        }else {stringBuffer.append("未完成工区消毒；");}

        if (WorkLogConstans.IS.equals(workLog.getIsClean())) {
            stringBuffer.append("完成工区卫生打扫；");
        }else { stringBuffer.append("未完成工区卫生打扫；");}

        if (WorkLogConstans.NORMAL.equals(workLog.getIsAbnormal())) {
            stringBuffer.append("班组上岗人员体温正常。");
        }else {
            stringBuffer.append("班组上岗人员体温异常。");
        }
        String faultContent = workLog.getFaultContent();
        if (StrUtil.isBlank(faultContent)) {
            workLog.setFaultContent("无");
        }

        String repairContent = workLog.getRepairContent();
        if (StrUtil.isBlank(repairContent)) {
            workLog.setRepairContent("无");
        }

        String patrolContent = workLog.getPatrolContent();
        if (StrUtil.isBlank(patrolContent)) {
            workLog.setPatrolContent("无");
        }

        Object otherWorkContent = workLog.getOtherWorkContent();
        if (Objects.isNull(otherWorkContent)) {
            workLog.setOtherWorkContent("无");
        }

        Object content = workLog.getContent();
        if (Objects.isNull(content)) {
            workLog.setContent("无");
        }
        Object content1 = workLog.getContent();
        if (ObjectUtil.isEmpty(content1)) {
            workLog.setContent("无");
        }
        workLog.setAntiepidemicWork(stringBuffer.toString());
        workLog.setSchedule(schedule);
        return workLog;
    }
}
