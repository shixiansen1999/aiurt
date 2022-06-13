package com.aiurt.boot.modules.worklog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.appMessage.constant.MessageConstant;
import com.aiurt.boot.modules.appMessage.entity.Message;
import com.aiurt.boot.modules.appMessage.param.MessageAddParam;
import com.aiurt.boot.modules.appMessage.service.IMessageService;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.mapper.StationMapper;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.mapper.PatrolPoolMapper;
import com.aiurt.boot.modules.patrol.mapper.PatrolTaskMapper;
import com.aiurt.boot.modules.repairManage.service.IRepairTaskService;
import com.aiurt.boot.modules.repairManage.vo.RepairRecordVO;
import com.aiurt.boot.modules.worklog.dto.WorkLogDTO;
import com.aiurt.boot.modules.worklog.entity.WorkLog;
import com.aiurt.boot.modules.worklog.entity.WorkLogEnclosure;
import com.aiurt.boot.modules.worklog.mapper.WorkLogEnclosureMapper;
import com.aiurt.boot.modules.worklog.mapper.WorkLogMapper;
import com.aiurt.boot.modules.worklog.param.LogCountParam;
import com.aiurt.boot.modules.worklog.param.WorkLogParam;
import com.aiurt.boot.modules.worklog.service.IWorkLogService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.enums.WorkLogCheckStatusEnum;
import com.aiurt.common.enums.WorkLogConfirmStatusEnum;
import com.aiurt.common.enums.WorkLogStatusEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.*;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.LocalDateUtil;
import com.aiurt.common.util.RoleAdditionalUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class WorkLogServiceImpl extends ServiceImpl<WorkLogMapper, WorkLog> implements IWorkLogService {

    @Resource
    private WorkLogMapper depotMapper;

    @Resource
    private WorkLogEnclosureMapper enclosureMapper;

    @Resource
    private FaultRepairRecordMapper recordMapper;

    @Resource
    private PatrolTaskMapper patrolTaskMapper;

    @Resource
    private PatrolPoolMapper patrolPoolMapper;

    @Resource
    private IRepairTaskService repairTaskService;

    @Resource
    private StationMapper stationMapper;

//    @Resource
//    private ISysDepartService departService;
//
//    @Resource
//    private ISysUserService sysUserService;

    @Resource
    private UserTaskService userTaskService;

    @Resource
    private RoleAdditionalUtils roleAdditionalUtils;

    @Resource
    private IMessageService messageService;


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
        // todo 后期修改
        LoginUser one = new LoginUser();
//        LoginUser one = sysUserService.getOne(new QueryWrapper<SysUser>().eq(SysUser.ID, userId), false);
        depot.setOrgId(one.getOrgId());
        depot.setSubmitId(userId);
        depot.setCreateBy(userId);
        //根据当前登录人id获取故障待办消息
        String nowday = new SimpleDateFormat("yyyy-MM-dd").format(dto.getLogTime());
        List<FaultRepairRecordResult> message = recordMapper.getWaitMessage(depot.getCreateBy(),nowday+" 00:00:00",nowday+" 23:59:59");
        if (ObjectUtil.isNotEmpty(message)) {
            List<String> faultCodes = new ArrayList<String>();
            List<String> faultContent = new ArrayList<String>();
            for (FaultRepairRecordResult result : message) {
                faultCodes.add(result.getFaultCode());
                faultContent.add(result.getFaultPhenomenon());
            }
            String str = StringUtils.join(faultCodes, ",");
            String str1 = StringUtils.join(faultContent, ",");
            if (StringUtils.isNotBlank(str)) {
                depot.setFaultCode(str);
            }
            if (StringUtils.isNotBlank(str1)) {
                depot.setFaultContent(str1);
            }
        }

        //根据当前登录人id获取巡检待办消息
        LocalDate localDate = LocalDateUtil.dateToLocalDateTime(dto.getLogTime()).toLocalDate();
        List<PatrolTask> patrolTasks = patrolTaskMapper.selectList(new LambdaQueryWrapper<PatrolTask>()
                .select(PatrolTask::getCode, PatrolTask::getPatrolPoolId)
                .between(PatrolTask::getCreateTime, localDate.atTime(0, 0, 0), localDate.atTime(23, 59, 59))
                .like(PatrolTask::getStaffIds, depot.getCreateBy())
        );
        if (CollUtil.isNotEmpty(patrolTasks)){
            List<Long> patrolList = patrolTasks.stream().map(PatrolTask::getPatrolPoolId).collect(Collectors.toList());
            List<PatrolPool> patrolPools = patrolPoolMapper.selectList(new LambdaQueryWrapper<PatrolPool>()
                    .select(PatrolPool::getId, PatrolPool::getPatrolName)
                    .in(PatrolPool::getId, patrolList));
            String ids = StringUtils.join(patrolPools.stream().filter(p -> {
                if (p.getId()!=null) {
                    return true;
                }else {
                    return false;
                }

            }).map(PatrolPool::getId).collect(Collectors.toList()), ",");
            String title = StringUtils.join(patrolPools.stream().filter(p -> {
                if (StringUtils.isNotBlank(p.getPatrolName())) {
                    return true;
                }else {
                    return false;
                }
            }).map(PatrolPool::getPatrolName).collect(Collectors.toList()), ",");
            if (StringUtils.isNotBlank(ids)) {
                depot.setPatrolIds(ids);
            }
            if (StringUtils.isNotBlank(title)) {
                depot.setPatrolContent(title);
            }
        }
        //根据用户id和所在周的时间获取检修池内容
        Result task = repairTaskService.getRepairTaskByUserIdAndTime(depot.getCreateBy(), nowday);
        if (ObjectUtil.isNotEmpty(task.getResult())) {
            RepairRecordVO result = (RepairRecordVO) task.getResult();
            depot.setRepairCode(result.getRepairTaskCode());
            List<String> content = result.getRepairPoolContent();
            List<String> contents = new ArrayList<String>();
            for (String s : content) {
                contents.add(s);
            }
            String str2 = StringUtils.join(contents, ",");
            depot.setRepairContent(str2);
        }
        depot.setStatus(1);
        depot.setConfirmStatus(0);
        depot.setCheckStatus(0);
        if (dto.getSubmitTime() == null){
            depot.setSubmitTime(new Date());
        }
        depot.setSubmitTime(dto.getSubmitTime());
        depot.setWorkContent(dto.getWorkContent());
            depot.setContent(dto.getContent());
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
        depotMapper.insert(depot);
        //插入附件列表
        if (CollectionUtils.isNotEmpty(dto.getUrlList())) {
            WorkLogEnclosure enclosure = new WorkLogEnclosure();
            List<String> urlList = dto.getUrlList();
            for (String s : urlList) {
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
        userTaskService.completeWork(userId, DateUtils.date2Str(depot.getSubmitTime(), new SimpleDateFormat("yyyy-MM-dd")));
        //发送待办消息
        if (StringUtils.isNotBlank(dto.getSucceedId())) {
            sendMessage(dto);
        }
        return Result.ok("新增成功");
    }

    /**
     * 如果接班人不为空 发送待办消息
     * @param dto
     */
    private void sendMessage(WorkLogDTO dto) {
        if (StringUtils.isNotBlank(dto.getSucceedId())) {
//            UserTaskAddParam addParam = new UserTaskAddParam();
            List<String> list = new ArrayList<>();
            list.add(dto.getSucceedId());
//            addParam.setUserIds(list);
//            addParam.setType(UserTaskConstant.USER_TASK_TYPE_4);
//            addParam.setTitle("工作日志");
//            addParam.setLevel(9);
//            userTaskService.add(addParam);

            //发送app消息
            Message message = new Message();
            message.setTitle("消息通知").setContent("您有一条待接班日志!").setType(MessageConstant.MESSAGE_TYPE_0);
            messageService.addMessage(MessageAddParam.builder().message(message).userIds(list).build());
        }
    }

    /**
     * 工作日志上报-分页列表查询
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<WorkLogResult> pageList(IPage<WorkLogResult> page, WorkLogParam param, HttpServletRequest req) {
        //String userId = TokenUtils.getUserId(req, iSysBaseAPI);
        //param.setSubmitId(userId);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
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
            // todo 后期修改
            SysDepartModel sysDepartModel = new SysDepartModel();
//            SysDepart sysDepart = departService.getOne(new QueryWrapper<SysDepart>().eq(SysDepart.DEPART_NAME, record.getSubmitOrgName()), false);
            Station station = stationMapper.selectNameById(sysDepartModel.getId());
            if (ObjectUtil.isNotEmpty(station)) {
                String lineName = station.getLineName();
                record.setLineName(lineName);
            }
            if (StringUtils.isNotBlank(record.getAssortLocation())){
                List<Station> stations = stationMapper.selectBatchIds(Arrays.asList(record.getAssortLocation().split(",")));
                record.setAssortLocationName(StringUtils.join(stations.stream().map(Station::getStationName).collect(Collectors.toList()),","));
            }
            //提交状态
            record.setStatusDesc(WorkLogStatusEnum.findMessage(record.getStatus()));
            //确认状态
            record.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(record.getConfirmStatus()));
            //审核状态
            record.setCheckStatusDesc(WorkLogCheckStatusEnum.findMessage(record.getCheckStatus()));
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
        List<String> departIdsByUserId = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
        if (CollectionUtils.isNotEmpty(departIdsByUserId)) {
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

        List<Station> stationList = stationMapper.selectList(new LambdaQueryWrapper<Station>()
                .eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0).select(Station::getId,Station::getTeamName,Station::getLineName,Station::getTeamId));
//       todo 后期修改
        List<SysDepartModel> departList = new ArrayList<>();
//        List<SysDepartModel> departList = departService.lambdaQuery().eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0).list();

        Map<Integer, Station> stationIdMap = null;
        Map<String, List<Station>> stationTeamIdMap =null;
        Map<String, String> departMap = null;
        if (CollectionUtils.isNotEmpty(stationList)){
            stationIdMap = stationList.stream().collect(Collectors.toMap(Station::getId, s -> s));
            stationTeamIdMap = stationList.stream().filter(f->f.getTeamId()!=null).collect(Collectors.groupingBy(Station::getTeamId));
        }
        if (CollectionUtils.isNotEmpty(departList)){
            departMap = departList.stream().collect(Collectors.toMap(SysDepartModel::getDepartName, SysDepartModel::getId));
        }

        Map<Long,List<WorkLogEnclosure>> map0 = null;
        Map<Long,List<WorkLogEnclosure>> map1 = null;

        if (CollectionUtils.isNotEmpty(records)){
            List<Long> list = records.stream().map(WorkLogResult::getId).collect(Collectors.toList());
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
                String id = departMap.get(record.getSubmitOrgName());
                if (StringUtils.isNotBlank(id)){
                    List<Station> station = stationTeamIdMap.get(id);
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
                        Station station = stationIdMap.get(Integer.parseInt(id));
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
                List<WorkLogEnclosure> enclosures = map0.get(record.getId());
                if (CollectionUtils.isNotEmpty(enclosures)) {
                    record.setUrlList(enclosures.stream().map(WorkLogEnclosure::getUrl).collect(Collectors.toList()));
                }
            }
            if (map1!=null){
                List<WorkLogEnclosure> enclosures = map1.get(record.getId());
                if (CollectionUtils.isNotEmpty(enclosures)) {
                    record.setSignature(enclosures.stream().map(WorkLogEnclosure::getUrl).collect(Collectors.toList()));
                }
            }
            //提交状态
            record.setStatusDesc(WorkLogStatusEnum.findMessage(record.getStatus()));
            //确认状态
            record.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(record.getConfirmStatus()));
            //审核状态
            record.setCheckStatusDesc(WorkLogCheckStatusEnum.findMessage(record.getCheckStatus()));
        }
        return result;
    }

    /**
     * 根据id假删除
     * @param id
     * @return
     */
    @Override
    public Result<?> deleteById(Integer id) {
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
    public WorkLogResult getDetailById(Integer id) {
        WorkLogResult workLog = depotMapper.queryById(id);
        //提交状态
        workLog.setStatusDesc(WorkLogStatusEnum.findMessage(workLog.getStatus()));
        //确认状态
        workLog.setConfirmStatusDesc(WorkLogConfirmStatusEnum.findMessage(workLog.getConfirmStatus()));
        //审核状态
        workLog.setCheckStatusDesc(WorkLogCheckStatusEnum.findMessage(workLog.getCheckStatus()));
        //附件列表
        List<String> query = enclosureMapper.query(id,0);
        //签名列表
        List<String> query1 = enclosureMapper.query(id,1);
        //配合施工参与人姓名
        if (StringUtils.isNotBlank(workLog.getAssortIds())){
            String[] split = workLog.getAssortIds().split(",");
            List<String> names = new ArrayList<>();
            for (String string : split) {
                // todo 后期修改
                LoginUser user = new LoginUser();
//                LoginUser user = sysUserService.getById(string);
                if (user!=null) {
                    names.add(user.getRealname());
                }
            }
            String str = StringUtils.join(names, ",");
            workLog.setAssortNames(str) ;
        }
        workLog.setUrlList(query);
        workLog.setSignature(query1);
        return workLog;
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
        List<FaultRepairRecordResult> message = recordMapper.getWaitMessage(userId,nowday+" 00:00:00",nowday+" 23:59:59");
        if (CollectionUtils.isNotEmpty(message)) {
            List<String> faultCodes = new ArrayList<String>();
            List<String> faultContent = new ArrayList<String>();
            for (FaultRepairRecordResult result : message) {
                faultCodes.add(result.getFaultCode());
                faultContent.add(result.getFaultPhenomenon());
            }
            String str = StringUtils.join(faultCodes, ",");
            String str1 = StringUtils.join(faultContent, ",");
            if (StringUtils.isNotBlank(str)) {
                logResult.setFaultCodes(str);
            }
            if (StringUtils.isNotBlank(str1)) {
                logResult.setFaultContent(str1);
            }
        }

        //根据当前登录人id获取巡检待办消息
        List<PatrolTask> patrolTasks = patrolTaskMapper.selectList(new LambdaQueryWrapper<PatrolTask>()
                .select(PatrolTask::getCode, PatrolTask::getPatrolPoolId)
                .between(PatrolTask::getCreateTime, nowday+" "+"00:00:00", nowday+" " +"23:59:59")
                .like(PatrolTask::getStaffIds, userId)
        );
        if (CollUtil.isNotEmpty(patrolTasks)) {
            List<Long> patrolList = patrolTasks.stream().map(PatrolTask::getPatrolPoolId).collect(Collectors.toList());

            List<PatrolPool> patrolPools = patrolPoolMapper.selectList(new LambdaQueryWrapper<PatrolPool>()
                    .select(PatrolPool::getId, PatrolPool::getPatrolName)
                    .in(PatrolPool::getId, patrolList));

            String ids = StringUtils.join(patrolPools.stream().filter(p -> {
                if (p.getId()!=null) {
                    return true;
                }
                return false;
            }).map(PatrolPool::getId).collect(Collectors.toList()), ",");

            String title = StringUtils.join(patrolPools.stream().filter(p -> {
                if (StringUtils.isNotBlank(p.getPatrolName())) {
                    return true;
                }
                return false;
            }).map(PatrolPool::getPatrolName).collect(Collectors.toList()), ",");

            if (StringUtils.isNotBlank(ids)) {
                logResult.setPatrolIds(ids);
            }
            if (StringUtils.isNotBlank(title)) {
                logResult.setPatrolContent(title);
            }
        }
        //获取检修待办消息
        Result task = repairTaskService.getRepairTaskByUserIdAndTime(userId, nowday);
        if (ObjectUtil.isNotEmpty(task.getResult())) {
            RepairRecordVO result = (RepairRecordVO) task.getResult();

            logResult.setRepairCode(result.getRepairTaskCode());
            List<String> content = result.getRepairPoolContent();
            List<String> contents = new ArrayList<String>();
            for (String s : content) {
                contents.add(s);
            }
            String str2 = StringUtils.join(contents, ",");
            logResult.setRepairContent(str2);
        }
        return Result.ok(logResult);
    }

    /**
     * 编辑工作日志
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editWorkLog(WorkLogDTO dto) {
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
        workLog.setAssortContent(dto.getAssortContent());
        depotMapper.updateById(workLog);

        //删除原附件列表
        enclosureMapper.deleteByName(workLog.getId());
        //重新插入附件列表
        List<String> urlList = dto.getUrlList();
        if (CollUtil.isNotEmpty(urlList)) {
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
        //如果接班人不为空 发送待办消息
        sendMessage(dto);
        return Result.ok();
    }

    /**
     * 日志统计
     * @param param
     * @return
     */
    @Override
    public List<LogCountResult> getLogCount(LogCountParam param) {
        int dayNums = 0 ;
        LocalDate now = LocalDate.now();
        if (param.getDayStart() == null && param.getDayEnd() == null) {
            //获取本月开始时间
            LocalDateTime of = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0, 0);
            param.setDayStart(of);
            //获取当前时间
            LocalDateTime nowDate = now.atTime(23, 59, 59);
            param.setDayEnd(nowDate);
            dayNums = now.getDayOfMonth();
        }else {
            long nd = 24 * 60 * 60 * 1000;
            long startTime = Date.from(param.getDayStart().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            long endTime = Date.from(param.getDayEnd().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            long aa = (endTime-startTime)/nd;
            int i = (int) aa;
            dayNums = i+1;
        }
        List<LogCountResult> logCountResults = depotMapper.selectLogCount(param);
        for (LogCountResult logCountResult : logCountResults) {
            logCountResult.setUnSubmitNum(dayNums-logCountResult.getSubmitNum());
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
        LogSubmitCount logSubmitCount = new LogSubmitCount();
        Long num = depotMapper.selectCount(new LambdaQueryWrapper<WorkLog>()
                .between(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime), WorkLog::getSubmitTime, startTime, endTime));
        logSubmitCount.setSubmitNum(num);
        return Result.ok(logSubmitCount);
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
        }else if (integer > CommonConstant.VALUE_2 && integer<CommonConstant.VALUE_3) {
            num ="0" + String.valueOf(integer+1);
        }else {
            num = String.valueOf(integer+1);
        }
        return code+substring+s1+s2+"."+num;
    }

}
