package com.aiurt.modules.worklog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.InspectionApi;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.enums.WorkLogCheckStatusEnum;
import com.aiurt.common.enums.WorkLogConfirmStatusEnum;
import com.aiurt.common.enums.WorkLogStatusEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.*;
import com.aiurt.common.util.*;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.common.api.DailyFaultApi;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.modules.worklog.constans.WorkLogConstans;
import com.aiurt.modules.worklog.dto.*;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.entity.WorkLogEnclosure;
import com.aiurt.modules.worklog.mapper.WorkLogEnclosureMapper;
import com.aiurt.modules.worklog.mapper.WorkLogMapper;
import com.aiurt.modules.worklog.mapper.WorkLogRemindMapper;
import com.aiurt.modules.worklog.param.LogCountParam;
import com.aiurt.modules.worklog.param.WorkLogParam;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.alibaba.excel.util.IoUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Slf4j
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
    @Autowired
    private ISysParamAPI iSysParamAPI;

    @Resource
    private ArchiveUtils archiveUtils;

    @Autowired
    private WorkLogRemindMapper workLogRemindMapper;
    @Autowired
    private ScheduleRecordMapper scheduleRecordMapper;
    @Value("${support.path.exportWorkLogPath}")
    private String exportPath;
    @Value("${jeecg.minio.bucketName}")
    private String bucketName;
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;
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
        BeanUtil.copyProperties(dto, depot);

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        String logCode = generateLogCode();
        depot.setCode(logCode);
        depot.setOrgId(loginUser.getOrgId());
        depot.setSubmitId(userId);
        depot.setCreateBy(userId);

        depot.setConfirmStatus(0);
        depot.setCheckStatus(0);
        if (depot.getStatus()==1){
            depot.setSubmitTime(new Date());
        }

        //工作内容赋值
        if (dto.getIsEmergencyDisposal().equals(WorkLogConstans.IS)) {
            depot.setEmergencyDisposalContent(dto.getEmergencyDisposalContent());
        }
        if (dto.getIsDocumentPublicity().equals(WorkLogConstans.IS)) {
            depot.setDocumentPublicityContent(dto.getDocumentPublicityContent());
        }

        if (StringUtils.isNotBlank(dto.getApproverId())) {
            depot.setApprovalTime(new Date());
        }else {
            depot.setApprovalTime(null);
        }
        depot.setLogTime(dto.getLogTime());
        depot.setDelFlag(0);

        depotMapper.insert(depot);
        dto.setId(depot.getId());
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
        WorkLogEnclosure enclosure = new WorkLogEnclosure();
        enclosure.setCreateBy(depot.getCreateBy());
        enclosure.setParentId(depot.getId());
        enclosure.setType(1);
        // 根据配置决定是否需要自动签名
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.AUTO_SIGNATURE);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            LoginUser user = iSysBaseAPI.getUserById(loginUser.getId());
            enclosure.setUrl(user.getSignatureUrl());
        } else {
            enclosure.setUrl(dto.getSignature());
        }
        enclosure.setDelFlag(0);
        enclosureMapper.insert(enclosure);
        //完成任务
        //不存在userTaskService
        //userTaskService.completeWork(userId, DateUtils.date2Str(depot.getSubmitTime(), new SimpleDateFormat("yyyy-MM-dd")));
        //发送待办消息
        if (StringUtils.isNotBlank(dto.getSucceedId())) {
            List<String> list = StrUtil.splitTrim(dto.getSucceedId(), ",");
            for (String s : list) {
                LoginUser userById = iSysBaseAPI.getUserById(s);
                dto.setSucceedUserName(userById.getUsername());
                sendMessage(dto);
            }
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
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //发送通知
            MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(), dto.getSucceedUserName(), "您有一条待接班日志" + DateUtil.today(), null, com.aiurt.common.constant.CommonConstant.MSG_CATEGORY_8);
            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, dto.getId());
            map.put(CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.WORKLOG.getType());
            messageDTO.setData(map);

            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setMsgAbstract("工作日志上报");
            messageDTO.setPublishingContent("您有一条待接班日志");
            iSysBaseAPI.sendTemplateMessage(messageDTO);
        } catch (Exception e) {
            e.printStackTrace();
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
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        param.setSubmitId(user.getId());
//        param.setSuccessorId(user.getId());
        List<CsUserDepartModel> departByUserId = iSysBaseAPI.getDepartByUserId(user.getId());
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        if (!admin) {
            if(CollUtil.isNotEmpty(departByUserId)){
                List<String> departIdsByUserId = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
                param.setDepartList(departIdsByUserId);
            }
            else {
                return null;
            }
        }
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
        List<CsUserDepartModel> departByUserId = iSysBaseAPI.getDepartByUserId(user.getId());
            if(CollUtil.isNotEmpty(departByUserId)){
                List<String> departIdsByUserId = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
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
//            if (WorkLogConstans.IS.equals(record.getIsDisinfect())) {
//                stringBuffer.append("完成工区消毒；");
//            }else {stringBuffer.append("未完成工区消毒；");}

//            if (WorkLogConstans.IS.equals(record.getIsClean())) {
//                stringBuffer.append("完成工区卫生打扫；");
//            }else { stringBuffer.append("未完成工区卫生打扫；");}

//            if (WorkLogConstans.NORMAL.equals(record.getIsAbnormal())) {
//                stringBuffer.append("班组上岗人员体温正常。");
//            }else { stringBuffer.append("班组上岗人员体温异常。");}

           // record.setAntiepidemicWork(stringBuffer.toString());


            StringBuffer stringBuffer2 = new StringBuffer();
            if (WorkLogConstans.IS.equals(record.getIsEmergencyDisposal())) {
                stringBuffer2.append("应急处理情况：");
                stringBuffer2.append(record.getEmergencyDisposalContent()+";");
            }
//            stringBuffer2.append("防疫相关工作：");
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
        List<CsUserDepartModel> departByUserId = iSysBaseAPI.getDepartByUserId(user.getId());
        if (!admin) {
            if(CollUtil.isNotEmpty(departByUserId)){
                List<String> departIdsByUserId = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
                param.setDepartList(departIdsByUserId);
            }
            else {
                return null;
            }
        }
        return getWorkLogResultIPage(page, param);
    }

    @Override
    public Page<WorkLogIndexUnSubmitRespDTO> getIndexUnSubmitWorkLogList(WorkLogIndexUnSubmitReqDTO workLogIndexUnSubmitReqDTO) {
        // 查询开始时间和查询结束时间
        Date startDate = workLogIndexUnSubmitReqDTO.getStartDate();
        Date endDate = workLogIndexUnSubmitReqDTO.getEndDate();

        // --开始时间大于结束时间的话，直接返回
        if (endDate.before(startDate)){
            return new Page<>(workLogIndexUnSubmitReqDTO.getPageNo(), workLogIndexUnSubmitReqDTO.getPageSize());
        }

        // 查看开始时间到结束时间有多少天
        int days = (int) DateUtil.between(startDate, endDate, DateUnit.DAY) + 1;

        // 获取权限部门，如果是管理员和主任就是获取所有部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin") || SecurityUtils.getSubject().hasRole("zhuren");
        List<SysDepartModel> permitDepart;
        //只获取班组数量,组织机构类型不为公司部门
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_ORG_CATEGORY);
        List<String> orgCategoryList = StrUtil.splitTrim(sysParamModel.getValue(), ",");
        if (isAdmin){
            // 管理员和主任获取所有部门
            permitDepart = iSysBaseAPI.getAllSysDepart().stream().filter(s -> orgCategoryList.contains(s.getOrgCategory())).collect(Collectors.toList());
        }else {
            // 其他角色获取权限部门
            List<CsUserDepartModel> csUserDepartModelList = iSysBaseAPI.getDepartByUserId(loginUser.getId())
                    .stream().filter(s -> orgCategoryList.contains(s.getOrgCategory())).collect(Collectors.toList());
            permitDepart = csUserDepartModelList.stream().map(s->{
                SysDepartModel sysDepartModel = new SysDepartModel();
                BeanUtils.copyProperties(s, sysDepartModel);
                sysDepartModel.setId(s.getDepartId());
                return sysDepartModel;
            }).collect(Collectors.toList());
        }
        // --权限部门为空的话直接返回
        if (CollUtil.isEmpty(permitDepart)){
            return new Page<>(workLogIndexUnSubmitReqDTO.getPageNo(), workLogIndexUnSubmitReqDTO.getPageSize());
        }

        // 班组查询的话
        List<String> searchOrgIdList = workLogIndexUnSubmitReqDTO.getOrgIdList();
        if (CollUtil.isNotEmpty(searchOrgIdList)){
            permitDepart = permitDepart.stream().filter(d->searchOrgIdList.contains(d.getId())).collect(Collectors.toList());
        }

        // 根据权限部门和查询日期，获取已提交的日志
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> orgIdList = permitDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        List<WorkLog> submitWorkLogList = depotMapper.queryWorKLogByOrgIdAndDate(orgIdList, startDate, endDate, WorkLogConstans.STATUS_1);
        // 将workLogList根据(orgId, -, logTime)连接作为key，提交个数作为value，做一个Map
        Map<String, Long> submitWorkLogMap = submitWorkLogList.stream()
                .collect(Collectors.groupingBy(w -> w.getOrgId() + "-" + dateFormat.format(w.getLogTime()), Collectors.counting()));

        // 根据权限部门，开始时间和结束时间，先组合成一个初步的WorkLogIndexUnSubmitRespDTO列表
        List<WorkLogIndexUnSubmitRespDTO> list = new ArrayList<>();
        permitDepart.forEach(depart->{
            // 开始时间走到结束时间
            for (int i = 0; i < days; i++) {
                Date logTime = DateUtil.offsetDay(startDate, i);
                WorkLogIndexUnSubmitRespDTO respDTO = new WorkLogIndexUnSubmitRespDTO();
                respDTO.setOrgId(depart.getId());
                respDTO.setOrgName(depart.getDepartName());
                respDTO.setLogTime(logTime);
                // 如果当前时间大于应提交日期，deadLineFlag等于true
                respDTO.setDeadLineFlag(DateUtil.compare(DateUtil.beginOfDay(new Date()), DateUtil.beginOfDay(logTime)) > 0);

                // 查看这个班组这一天的提交日志的数量
                Long submitNum = submitWorkLogMap.get(depart.getId() + "-" + dateFormat.format(logTime));
                if (submitNum == null || submitNum == 0L){
                    // 没有提交记录
                    // 每个班组每天应该提交2个日志
                    respDTO.setUnSubmitNum(2);
                    list.add(respDTO);
                }else if (submitNum == 1L) {
                    // 提交了一条
                    respDTO.setUnSubmitNum(1);
                    list.add(respDTO);
                }
                // 提交了两条以上的，不加入list
            }
        });

        // 组装结果
        int pageNo = workLogIndexUnSubmitReqDTO.getPageNo();
        int pageSize = workLogIndexUnSubmitReqDTO.getPageSize();
        Page<WorkLogIndexUnSubmitRespDTO> pageList = new Page<>(pageNo, pageSize);
        pageList.setTotal(list.size());
        pageList.setPages(list.size() / pageSize + 1);
        // 排序
        list.sort(Comparator.comparing(WorkLogIndexUnSubmitRespDTO::getLogTime).thenComparing(WorkLogIndexUnSubmitRespDTO::getOrgId));

        List<WorkLogIndexUnSubmitRespDTO> records = list.stream().skip((pageNo - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        pageList.setRecords(records);

        return pageList;
    }

    @Override
    public IPage<WorkLogBigScreenRespDTO> bigScreenPageList(Page<WorkLogResult> page, WorkLogBigScreenReqDTO workLogBigScreenReqDTO) {
        IPage<WorkLogBigScreenRespDTO> pageList = depotMapper.bigScreenPageList(page, workLogBigScreenReqDTO);
        // 设置状态，待提交->待提交，已提交&未确认->待确认，已确认->已完成
        pageList.getRecords().forEach(dto->{
            if (WorkLogConstans.STATUS_0.equals(dto.getSubmitStatus())) {
                dto.setStateName("待提交");
            }else if (WorkLogConstans.CONFIRM_STATUS_1.equals(dto.getConfirmStatus())){
                dto.setStateName("已完成");
            }else{
                dto.setStateName("待确认");
            }
        });
        return pageList;
    }

    /**
     * 获取日志列表详情
     * @param page
     * @param param
     * @return
     */
    private IPage<WorkLogResult> getWorkLogResultIPage(IPage<WorkLogResult> page, WorkLogParam param) {
        IPage<WorkLogResult> result = depotMapper.queryWorkLog(page, param, SysParamCodeConstant.WORK_LOG_ORG_CATEGORY);
        List<WorkLogResult> records = result.getRecords();
        boolean b = GlobalThreadLocal.setDataFilter(false);
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
            //判断是否能编辑
            Boolean flag = editFlag(record.getCreateTime(), record.getConfirmStatus(), record.getCheckStatus());
            record.setEditFlag(flag);
            //判断是否能补录
            if (record.getStatus() != null && record.getStatus() == 0) {
                Boolean additionalRecordingFlag = additionalRecordingFlag(record.getCreateTime());
                record.setAdditionalRecordingFlag(additionalRecordingFlag);
            }
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
            //获取是早班会16.30 还是晚班会8.30
            String am = format2+" " + morningTime;
            String pm = format2+" " + nightTime;
            if (record.getCreateTime().after(DateUtil.parse(am)) && record.getCreateTime().before(DateUtil.parse(pm))) {
                record.setClassTime("16时30分");
                record.setClassName("晚班会");
            } else {
                record.setClassTime("8时30分");
                record.setClassName("早班会");
            }

            //查询该部门下的人员
            List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(record.getOrgId());
            //获取负责人
            SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(record.getOrgId());
            LoginUser userById = iSysBaseAPI.getUserById(sysDepartModel.getManagerId());
            if (ObjectUtil.isNotEmpty(userById)) {
                record.setForeman(userById.getRealname());
            }
            //获取参与人员
            List<String> nameList = sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList());
            String str = StringUtils.join(nameList, ",");
            record.setUserList(str);
            String users = "";
            //交班人名称
            String handoverIds = record.getHandoverId();
            if (StrUtil.isNotEmpty(handoverIds)) {
                List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(handoverIds);
                String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
                record.setHandoverName(realNames);
                users = realNames;
            }
            //接班人名称
            String succeedIds = record.getSucceedId();
            if (StrUtil.isNotEmpty(succeedIds)) {
                List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(succeedIds);
                String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
                record.setSucceedName(realNames);
                if(StrUtil.isNotEmpty(handoverIds)){
                    users = users+","+realNames;
                }else {
                    users = realNames;
                }
            }
            //获取参与人员
            record.setUserList(users);

            StringBuffer stringBuffer2 = new StringBuffer();
            if (WorkLogConstans.IS.equals(record.getIsEmergencyDisposal())) {
                stringBuffer2.append("应急处理情况：");
                stringBuffer2.append(record.getEmergencyDisposalContent()+";");
            }
          //  stringBuffer2.append("防疫相关工作：");
            //stringBuffer2.append(stringBuffer);
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
        GlobalThreadLocal.setDataFilter(b);
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

        // 所在班组名称，orgName当前存储的是班组的orgId
        if (StrUtil.isNotBlank(workLog.getOrgName())) {
            List<JSONObject> dept = iSysBaseAPI.queryDepartsByIds(workLog.getOrgName());
            workLogDTO.setOrgName(CollUtil.isEmpty(dept) ? "" : dept.get(0).getString("departName"));
        }

        if(ObjectUtil.isNotEmpty(workLog.getSucceedId()))
        {
            String[] split1 = workLog.getSucceedId().split(",");
            List<LoginUser> assortNames = iSysBaseAPI.queryAllUserByIds(split1);
            String collect1 = assortNames.stream().map(s -> s.getRealname()).collect(Collectors.joining(","));
            String s = assortNames.stream().map(u -> u.getUsername()).collect(Collectors.joining(","));
            workLog.setSucceedName(collect1);
            workLogDTO.setSucceedUserName(s);
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
        //接班人名称
        String succeedIds = workLog.getSucceedId();
        if (StrUtil.isNotEmpty(succeedIds)) {
            List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(succeedIds);
            String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
            workLogDTO.setSucceedName(realNames);

        }

        // 配合施工地点名称
        if (StrUtil.isNotBlank(workLog.getAssortLocation()) ||
                (StrUtil.isBlank(workLog.getAssortLocation()) && StrUtil.isNotBlank(workLog.getStationCode()))) {
            List<String> locations = new ArrayList<>();
            if (StrUtil.isNotBlank(workLog.getAssortLocation())) {
                locations = StrUtil.split(workLog.getAssortLocation(), ',', true, true);
            }else {
                locations = StrUtil.split(workLog.getStationCode(), ',', true, true);
                locations.addAll(StrUtil.split(workLog.getPositionCode(), ',', true, true));
            }

            String locationNames = locations.stream().map(location -> iSysBaseAPI.getPosition(location))
                    .filter(name -> StrUtil.isNotBlank(name)).collect(Collectors.joining(","));
            workLogDTO.setAssortLocationName(locationNames);
        }

        if (StrUtil.isBlank(workLogDTO.getAssortLocationName())) {
            workLogDTO.setAssortLocationName(workLogDTO.getAssortLocation());
        }

        if (StrUtil.isNotBlank(workLog.getAssortLocation()) && !workLog.getAssortLocation().matches(".*[\\u4e00-\\u9fa5].*")) {
            List<String> list = StrUtil.splitTrim(workLog.getAssortLocation(), ',');
            if (CollUtil.isNotEmpty(list)) {
                List<CsStationPosition> stationPositionList = baseMapper.queryPositionList(list);
                if (CollUtil.isNotEmpty(stationPositionList)) {
                    String stationcode = stationPositionList.stream().map(CsStationPosition::getStaionCode).collect(Collectors.joining(","));
                    workLogDTO.setPositionCode(workLog.getAssortLocation());
                    workLogDTO.setStationCode(stationcode);
                }else {
                    workLogDTO.setStationCode(workLog.getAssortLocation());
                }
            }
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
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        WorkLog workLog = this.getOne(new QueryWrapper<WorkLog>().eq(WorkLog.ID, dto.getId()), false);
        workLog.setOrgId(loginUser.getOrgId());
        workLog.setSubmitId(loginUser.getId());
        workLog.setCreateBy(loginUser.getId());

        workLog.setLogTime(dto.getLogTime());
        workLog.setWorkContent(dto.getWorkContent());
        workLog.setContent(dto.getContent());
        workLog.setSucceedId(dto.getSucceedId());
        workLog.setConstructTime(dto.getConstructTime());
        workLog.setAssortTime(dto.getAssortTime());
        workLog.setAssortLocation(dto.getAssortLocation());
        workLog.setAssortIds(dto.getAssortIds());
        workLog.setAssortNum(dto.getAssortNum());
        workLog.setAssortUnit(dto.getAssortUnit());
        workLog.setFaultContent(dto.getFaultContent());
        workLog.setPatrolRepairContent(dto.getPatrolRepairContent());
        workLog.setAssortContent(dto.getAssortContent());
        workLog.setFaultCode(dto.getFaultCode());
        workLog.setFaultContent(dto.getFaultContent());
        workLog.setRepairCode(dto.getRepairCode());
        workLog.setRepairContent(dto.getRepairContent());
        workLog.setPatrolIds(dto.getPatrolIds());
        workLog.setPatrolContent(dto.getPatrolContent());
        workLog.setUnfinishCode(dto.getUnfinishCode());
        workLog.setUnfinishContent(dto.getUnfinishContent());
        workLog.setSchedule(dto.getSchedule());
        if (dto.getStatus() != null) {
            workLog.setStatus(1);
            workLog.setSubmitTime(new Date());
        }
        workLog.setUnfinishedMatters(dto.getUnfinishedMatters());
        //工作内容赋值
        workLog.setIsDisinfect(dto.getIsDisinfect());
        workLog.setIsClean(dto.getIsClean());
        workLog.setIsAbnormal(dto.getIsAbnormal());
        workLog.setIsEmergencyDisposal(dto.getIsEmergencyDisposal());
        workLog.setIsDocumentPublicity(dto.getIsDocumentPublicity());
        if (dto.getIsEmergencyDisposal().equals(WorkLogConstans.IS)) {
            workLog.setEmergencyDisposalContent(dto.getEmergencyDisposalContent());
        }else {
            workLog.setEmergencyDisposalContent("");
        }
        if (dto.getIsDocumentPublicity().equals(WorkLogConstans.IS)) {
            workLog.setDocumentPublicityContent(dto.getDocumentPublicityContent());
        }else {
            workLog.setDocumentPublicityContent("");
        }
        workLog.setOtherWorkContent(dto.getOtherWorkContent());
        workLog.setNote(dto.getNote());
        workLog.setHandoverId(dto.getHandoverId());
        workLog.setStationCode(dto.getStationCode());
        workLog.setPositionCode(dto.getPositionCode());
        workLog.setIsAdditionalRecording(dto.getIsAdditionalRecording());
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
        WorkLogEnclosure enclosure = new WorkLogEnclosure();
        enclosure.setCreateBy(workLog.getCreateBy());
        enclosure.setParentId(workLog.getId());
        enclosure.setType(1);
        // 根据配置决定是否需要自动签名
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.AUTO_SIGNATURE);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            LoginUser user = iSysBaseAPI.getUserById(loginUser.getId());
            enclosure.setUrl(user.getSignatureUrl());
        } else {
            enclosure.setUrl(dto.getSignature());
        }
        enclosure.setDelFlag(0);
        enclosureMapper.insert(enclosure);

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
    public String generateLogCode() {
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
    public Map getTodayJobContent(String nowday,String createTime) {

        Date date = DateUtil.date();
        //如果选择的时间是过去，则截取今天的时分秒和过去时间的年月日合并作为时间点
        String ymd = DateUtil.format(date, "yyyy-MM-dd");
        String hms = DateUtil.format(date, "HH:mm:ss");

        DateTime startTime;
        DateTime endTime;
        if (StrUtil.isEmpty(nowday)&&StrUtil.isEmpty(createTime)) {
            nowday = DateUtil.today();
        } else if (StrUtil.isNotBlank(createTime)) {
            //如果没有指定日期且只有创建时间，则补录日期就是创建日期
            nowday = createTime.substring(0,10);
        }
        if (!ymd.equals(nowday)) {
            String oldDay = nowday + " " + hms;
            date = DateUtil.parse(oldDay, "yyyy-MM-dd HH:mm:ss");
        }

        //获取是晚班会16.30 还是早班会8.30
        String am = nowday+" " + morningTime;
        String pm = nowday+" " + nightTime;
        //昨天时间
        DateTime lastDay = DateUtil.offsetDay(date, -1);
        String lastPM = DateUtil.format(lastDay, "yyyy-MM-dd")+ " 16:30:00";

        //明天时间
        DateTime nextDay = DateUtil.offsetDay(date, +1);
        String nextAM = DateUtil.format(nextDay, "yyyy-MM-dd")+ " 08:29:59";

        if (createTime != null) {
            //补录
            DateTime create = DateUtil.parse(createTime, "yyyy-MM-dd HH:mm:ss");
            if (create.after(DateUtil.parse(am)) && create.before(DateUtil.parse(pm))) {
                //白班
                startTime = DateUtil.parse(nowday+" 08:30:00");
                endTime = DateUtil.parse(nowday+" 16:29:59");
            } else {
                //晚班
                startTime = DateUtil.parse(lastPM);
                endTime = DateUtil.parse(nowday+" 08:29:59");
            }
        }else {
            //正常写入日志
            if (date.after(DateUtil.parse(am)) && date.before(DateUtil.parse(pm))) {
                //白班
                startTime = DateUtil.parse(nowday+" 08:30:00");
                endTime = DateUtil.parse(nowday+" 16:29:59");
            } else if (date.before(DateUtil.parse(am))){
                //昨天晚班
                startTime = DateUtil.parse(lastPM);
                endTime = DateUtil.parse(nowday + " 08:29:59");
            } else {
                //今天晚班
                startTime = DateUtil.parse(nowday+" 16:30:00");
                endTime = DateUtil.parse(nextAM);
            }
        }
        HashMap<String, String> map = new HashMap<>(16);

        //获取巡检内容
        HashMap<String, String> userTask = patrolApi.getUserTask(startTime, endTime);
        //获取检修内容
        HashMap<String, String> inspectionTaskDevice = inspectionApi.getInspectionTaskDevice(startTime, endTime);
        //获取故障内容
        HashMap<String, String> faultTask = dailyFaultApi.getFaultTask(startTime, endTime);
        //获取未完成工作内，包括未完成故障内容，未完成巡视内容
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_UNFINISH_WORK);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            HashMap<String, String> unFinishFaultTask = dailyFaultApi.getUnFinishFaultTask();
            HashMap<String, String> unFinishPatrolTask = patrolApi.getUnFinishPatrolTask();
            String content = (unFinishFaultTask.get("content") != null ? unFinishFaultTask.get("content") : "") + (unFinishPatrolTask.get("content") != null ? unFinishPatrolTask.get("content") : "");
            String code = (unFinishFaultTask.get("code") != null ? unFinishFaultTask.get("code") : "") + (unFinishPatrolTask.get("code") != null ? unFinishPatrolTask.get("code") : "");
            map.put("unfinishContent", content);
            map.put("unfinishCode", code);
        }

        map.put("patrolContent", userTask.get("content"));
        map.put("repairContent", inspectionTaskDevice.get("content"));
        map.put("faultContent", faultTask.get("content"));
        map.put("patrolCode", userTask.get("code"));
        map.put("repairCode", inspectionTaskDevice.get("code"));
        map.put("faultCode", faultTask.get("code"));
        return map;
    }

    @Override
    public void archWorkLog(WorkLogResult workLogResult, String token, String archiveUserId, String refileFolderId, String realname, String sectId) {
        try {
            dealInfo(workLogResult);
            SXSSFWorkbook archiveRepairTask = createArchiveWorkLog(workLogResult, "/templates/workLogTemplate.xlsx");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Date date = new Date();
            Date submitTime = workLogResult.getSubmitTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = workLogResult.getSubmitOrgName() + "工作日志" + sdf.format(submitTime);
            workLogResult.setSubmitOrgName("");
            String path = exportPath + fileName + ".xlsx";
            FileOutputStream fos = new FileOutputStream(path);
            archiveRepairTask.write(os);
            fos.write(os.toByteArray());
            os.close();
            fos.close();
            PdfUtil.excel2pdf(path);
            //传入档案系统
            //创建文件夹
            String foldername = fileName + "_" + date.getTime();
            String refileFolderIdNew = archiveUtils.createFolder(token, refileFolderId, foldername);
            //上传文件
            String fileType = "pdf";
            File file = new File(exportPath + fileName + "." + fileType);
            Long size = file.length();
            InputStream in = Files.newInputStream(file.toPath());
            JSONObject res = archiveUtils.upload(token, refileFolderIdNew, fileName + "." + fileType, size, fileType, in);
            String fileId = res.getString("fileId");
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("fileId", fileId);
            fileInfo.put("operateType", "upload");
            ArrayList<Object> fileList = new ArrayList<>();
            fileList.add(fileInfo);

            // 修改为使用实体类
            Date archDate = new Date();
            String uuid = UUID.randomUUID().toString();
            ArchiveUtils.ArchiveInfo archiveInfo = archiveUtils.getArchiveInfo();
            archiveInfo.setId(uuid);
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            archiveInfo.setArchivedate(sdf.format(archDate));
            archiveInfo.setArchiver(archiveUserId);
            archiveInfo.setArchivername(realname);
            archiveInfo.setArchtypeid();
            archiveInfo.setCarrier("电子");
            archiveInfo.setDuration(workLogResult.getSecertDuration());
            archiveInfo.setObjtype("其他");
            archiveInfo.setEntrystate("0");
            archiveInfo.setFileList(fileList);
            archiveInfo.setIfDossiered("0");
            archiveInfo.setIfInbound("0");
            archiveInfo.setLastAutoAddNo("其他");
            archiveInfo.setLittleStatus("0");
            archiveInfo.setName(fileName);
            archiveInfo.setSecert(workLogResult.getSecert());
            //number怎么取值
            archiveInfo.setRefileFolderId(refileFolderIdNew);
            archiveInfo.setSecertDuration(workLogResult.getSecertDuration());
            archiveInfo.setSectid(sectId);
            archiveInfo.setTimes(archDate.getTime());
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            archiveInfo.setWrittendate(sdf.format(archDate));
            Map result = archiveUtils.arch(archiveInfo, token);

            /*
            Map values = new HashMap();
            values.put("archiver", archiveUserId);
            values.put("username", realname);
            values.put("duration", workLogResult.getSecertduration());
            values.put("secert", workLogResult.getSecert());
            values.put("secertduration", workLogResult.getSecertduration());
            values.put("name", fileName);
            values.put("fileList", fileList);
            values.put("number", values.get("number"));
            values.put("refileFolderId", refileFolderIdNew);
            values.put("sectid", sectId);
            Map result = archiveUtils.arch(values, token);
            */
            Map<String, String> obj = JSON.parseObject((String) result.get("obj"), new TypeReference<HashMap<String, String>>() {
            });

            //更新归档状态
            if (result.get("result").toString() == "true" && "新增".equals(obj.get("rs"))) {
                UpdateWrapper<WorkLog> uwrapper = new UpdateWrapper<>();
                uwrapper.eq("id", workLogResult.getId()).set("ecm_status", 1);
                this.update(uwrapper);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealInfo(WorkLogResult workLogResult) {
        //巡检修内容
        workLogResult.setPatrolRepairContent("巡视工作完成情况："  + workLogResult.getPatrolContent() + StrUtil.CRLF + "检修工作完成情况：" + workLogResult.getRepairContent());
    }

    private SXSSFWorkbook createArchiveWorkLog(WorkLogResult workLogResult, String path) {
        InputStream in = null;
        XSSFWorkbook xssfWb = null;
        try {
            org.springframework.core.io.Resource resource = new ClassPathResource(path);
            in = resource.getInputStream();
            xssfWb = new XSSFWorkbook(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWb);
        Sheet sheet = workbook.getXSSFWorkbook().getSheetAt(0);
        PrintSetup printSetup = sheet.getPrintSetup();
        //横向展示
        //printSetup.setLandscape(true);
        //A4
        printSetup.setPaperSize((short) 9);
        //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
        Drawing drawingPatriarch = sheet.createDrawingPatriarch();
        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        String head = "工作日志";
        cell.setCellValue(head);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Row rowOne = sheet.getRow(1);
        if (ObjectUtil.isNotEmpty(workLogResult.getSubmitTime())) {
            Cell c11 = rowOne.getCell(1);
            c11.setCellValue(sdf.format(workLogResult.getSubmitTime()));
        }
        Cell c13 = rowOne.getCell(3);
        c13.setCellValue(workLogResult.getSubmitName());

        if (StrUtil.isNotBlank(workLogResult.getPatrolRepairContent())) {
            Row rowTwo = sheet.getRow(2);
            Cell c21 = rowTwo.getCell(1);
            c21.setCellValue(workLogResult.getPatrolRepairContent());
            CellStyle c21Style = c21.getCellStyle();
            float height2 = ArchExecelUtil.getExcelCellAutoHeight(workLogResult.getPatrolRepairContent(), 20f);
            c21Style.setWrapText(true);
            rowTwo.setHeightInPoints(height2);
        }

        if (workLogResult.getFaultContent() != null) {
            Row rowThree = sheet.getRow(3);
            Cell c31 = rowThree.getCell(1);
            c31.setCellValue(workLogResult.getFaultContent());
            CellStyle c31Style = c31.getCellStyle();
            c31Style.setWrapText(true);
            float height3 = ArchExecelUtil.getExcelCellAutoHeight(workLogResult.getFaultContent(), 20f);
            rowThree.setHeightInPoints(height3);
        }

        Row rowFour = sheet.getRow(4);
        Cell c41 = rowFour.getCell(1);
        c41.setCellValue(workLogResult.getWorkContent() == null ? "" : workLogResult.getWorkContent());
        Row rowFive = sheet.getRow(5);
        Cell c51 = rowFive.getCell(1);
        c51.setCellValue(workLogResult.getContent() == null ? "" : workLogResult.getContent());

        Row rowSix = sheet.getRow(6);
        Cell c61 = rowSix.getCell(1);
        c61.setCellValue(workLogResult.getSucceedName() == null ? "无" : workLogResult.getSucceedName());
        Cell c63 = rowSix.getCell(3);
        if (workLogResult.getAssortTimes() != null && workLogResult.getAssortTimes().length > 1) {
            String[] assortTimes = workLogResult.getAssortTimes();
            c63.setCellValue(assortTimes[0] + " 至 " + assortTimes[1]);
        }

        Row rowSev = sheet.getRow(7);
        Cell c71 = rowSev.getCell(1);
        c71.setCellValue(workLogResult.getAssortLocationName());
        Cell c73 = rowSev.getCell(3);
        c73.setCellValue(workLogResult.getAssortUnit());

        Row rowEig = sheet.getRow(8);
        Cell c81 = rowEig.getCell(1);
        c81.setCellValue(workLogResult.getAssortNames());
        Cell c83 = rowEig.getCell(3);
        c83.setCellValue(workLogResult.getAssortNum() == null ? "" : workLogResult.getAssortNum().toString());

        Row rowNine = sheet.getRow(9);
        float heigth9 = 25f;
        String assortContent = workLogResult.getAssortContent();
        if (StrUtil.isNotBlank(assortContent)) {
            Cell c91 = rowNine.getCell(1);
            c91.setCellValue(assortContent);
            CellStyle c91Style = c91.getCellStyle();
            c91Style.setWrapText(true);
            heigth9 = ArchExecelUtil.getExcelCellAutoHeight(assortContent, 12f);
            rowNine.setHeightInPoints(heigth9);
        }

        //附件，只展示图片
        List<String> urlList = StrUtil.splitTrim(workLogResult.getUrlList(), ",");
        if (CollUtil.isNotEmpty(urlList)) {
            List<BufferedImage> bufferedImageList = new ArrayList<>();
            //遍历附件，获取其中的图片
            for (String url : urlList) {
                BufferedImage bufferedImage = null;
                try (InputStream inputStreamByUrl = this.getInputStreamByUrl(url)) {
                    //读取图片，非图片bufferedImage为null
                    if (ObjectUtil.isNotNull(inputStreamByUrl)) {
                        bufferedImage = ImageIO.read(inputStreamByUrl);
                    }
                    if (ObjectUtil.isNotNull(bufferedImage)) {
                        bufferedImageList.add(bufferedImage);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            //插入图片到单元格
            if (CollUtil.isNotEmpty(bufferedImageList)) {
                //设置边距
                int widthCol1 = Units.columnWidthToEMU(sheet.getColumnWidth(1));
                int heightRow9 = Units.toEMU(heigth9);
                int wMar = 2 * Units.EMU_PER_POINT;
                int hMar = Units.EMU_PER_POINT;
                int size = bufferedImageList.size();
                //每个图片宽度（大致平均值）
                int ave = (widthCol1 - (size + 1) * wMar) / size;
                for (int i = 0; i < size; i++) {
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        ImageIO.write(bufferedImageList.get(i), "jpg", byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        int pictureIdx = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                        ClientAnchor anchor = helper.createClientAnchor();
                        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                        anchor.setCol1(3);
                        anchor.setCol2(3);
                        anchor.setRow1(8);
                        anchor.setRow2(8);
                        anchor.setDx1((i + 1) * wMar + i * ave);
                        anchor.setDy1(hMar);
                        anchor.setDx2((i + 1) * (wMar + ave));
                        anchor.setDy2(heightRow9 - hMar);
                        drawingPatriarch.createPicture(anchor, pictureIdx);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        //签名
        String signature = workLogResult.getSignature();
        if (StrUtil.isNotBlank(signature)) {
            try (InputStream inputStreamByUrl = this.getInputStreamByUrl(signature)) {
                if (ObjectUtil.isNotEmpty(inputStreamByUrl)) {
                    byte[] bytes = IoUtils.toByteArray(inputStreamByUrl);
                    int pictureIdx = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                    ClientAnchor anchor = helper.createClientAnchor();
                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                    anchor.setCol1(1);
                    anchor.setCol2(4);
                    anchor.setRow1(10);
                    anchor.setRow2(11);
                    anchor.setDx1(Units.EMU_PER_POINT);
                    anchor.setDy1(Units.EMU_PER_POINT);
                    drawingPatriarch.createPicture(anchor, pictureIdx);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return workbook;
    }

    /**
     * 根据图片url获取InputSream
     * @param url
     * @return
     */
    private InputStream getInputStreamByUrl(String url) {
        InputStream inputStream = null;
        SysAttachment sysAttachment = null;
        try {
            if (url.contains("?")) {
                int index = url.indexOf("?");
                String attachId = url.substring(0, index);
                sysAttachment = iSysBaseAPI.getFilePath(attachId);

            }
            if (ObjectUtil.isNotEmpty(sysAttachment)) {
                if (StrUtil.equalsIgnoreCase("minio",sysAttachment.getType())) {
                    inputStream = MinioUtil.getMinioFile(bucketName, sysAttachment.getFilePath());
                } else {
                    String filePath = uploadpath + File.separator + sysAttachment.getFilePath();
                    File file = new File(filePath);
                    if (file.exists()) {
                        inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
                    }
                }
            } else {
                String filePath = uploadpath + File.separator + url;
                File file = new File(filePath);
                if (file.exists()) {
                    inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return inputStream;
    }

    @Override
    public Result<String> getUnfinishedMatters() {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        List<CsUserDepartModel> departByUserId = iSysBaseAPI.getDepartByUserId(user.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unfinishedMatters",null);
        Result result = Result.ok(jsonObject);
        result.setCode(200);
        List<String> departList = new ArrayList<>();
        if (!admin) {
            if (CollUtil.isNotEmpty(departByUserId)) {
                departList = departByUserId.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
            } else {
                return  result;
            }
        }
        String unfinishedMatters  = depotMapper.getUnfinishedMatters(departList);
        jsonObject.put("unfinishedMatters",unfinishedMatters);
        result = Result.ok(jsonObject);
        return  result;
    }

    @Override
    public List<WorkLogDetailResult> queryWorkLogDetailList(String id) {
        List<WorkLogDetailResult> list = new ArrayList<>();
        List<String> idList = depotMapper.getSameDayIdList(id);
        idList.forEach(perId->{
            WorkLogDetailResult workLogDetailResult = queryWorkLogDetail(perId);
            list.add(workLogDetailResult);
        });
        return list;
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
        //获取是早班会16.30 还是晚班会8.30
        String am = format2+" "+ morningTime;
        String pm = format2+" "+ nightTime;
        if (workLog.getCreateTime().after(DateUtil.parse(am)) && workLog.getCreateTime().before(DateUtil.parse(pm))) {
            workLog.setClassTime("16时30分");
            workLog.setClassName("晚班会");
        } else {
            workLog.setClassTime("8时30分");
            workLog.setClassName("早班会");
        }
        List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(workLog.getOrgId());
        //获取负责人
        SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(workLog.getOrgId());
        LoginUser userById = iSysBaseAPI.getUserById(sysDepartModel.getManagerId());
        if (ObjectUtil.isNotEmpty(userById)) {
            workLog.setForeman(userById.getRealname());
        }

        //获取参与人员
        List<String> nameList = sysUsers.stream().map(LoginUser::getRealname).collect(Collectors.toList());
        String str = StringUtils.join(nameList, ",");
        workLog.setUserList(str);
        String users = "";
        //交班人姓名
        String handoverId = workLog.getHandoverId();
        if (StrUtil.isNotEmpty(handoverId)) {
            List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(handoverId);
            String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
            workLog.setHandoverName(realNames);
            users = realNames;
        }
        //接班人名称
        String succeedIds = workLog.getSucceedId();
        if (StrUtil.isNotEmpty(succeedIds)) {
            List<JSONObject> jsonObjects = iSysBaseAPI.queryUsersByIds(succeedIds);
            String realNames = jsonObjects.stream().map(js -> js.getString("realname")).collect(Collectors.joining("；"));
            workLog.setSucceedName(realNames);
            if(StrUtil.isNotEmpty(handoverId)){
                users = users+","+realNames;
            }else {
                users = realNames;
            }
        }
        //获取参与人员
        workLog.setUserList(users);
        //防疫相关工作
       // StringBuffer stringBuffer = new StringBuffer();
//        if (WorkLogConstans.IS.equals(workLog.getIsDisinfect())) {
//            stringBuffer.append("完成工区消毒；");
//        }else {stringBuffer.append("未完成工区消毒；");}

//        if (WorkLogConstans.IS.equals(workLog.getIsClean())) {
//            stringBuffer.append("完成工区卫生打扫；");
//        }else { stringBuffer.append("未完成工区卫生打扫；");}

//        if (WorkLogConstans.NORMAL.equals(workLog.getIsAbnormal())) {
//            stringBuffer.append("班组上岗人员体温正常。");
//        }else {
//            stringBuffer.append("班组上岗人员体温异常。");
//        }
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
        //workLog.setAntiepidemicWork(stringBuffer.toString());
        return workLog;
    }


    @Override
    public Boolean editFlag(Date  createTime,Integer  confirmStatus,Integer  checkStatus) {
        //根据状态判断是否能编辑
        boolean edit = confirmStatus != 1;
        //根据配置是否需要控制在指定时间端内开放编辑按钮，其余时间隐藏
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_EDIT);
        boolean value = "1".equals(paramModel.getValue());
        if (edit && value) {
            SysParamModel amStart = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_AM_STARTEDIT);
            SysParamModel amEnd = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_AM_STOPEDIT);
            SysParamModel pmStart = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_PM_STARTEDIT);
            SysParamModel pmEnd = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_PM_STOPEDIT);

            validateTimeParam(amStart, "早上开始编辑时间");
            validateTimeParam(amEnd, "早上停止编辑时间");
            validateTimeParam(pmStart, "下午开始编辑时间");
            validateTimeParam(pmEnd, "下午停止编辑时间");

            String today = DateUtil.today();
            String amStart1 = today + " " + amStart.getValue();
            String amEnd1 = today + " " + amEnd.getValue();
            String pmStart1 = today + " " + pmStart.getValue();
            String pmEnd1 = today + " " + pmEnd.getValue();

            DateTime date = DateUtil.date();
            //当前时间小于早上可编辑停止时间，并且在可编辑时间范围内，则可编辑
            boolean afterAM = date.before(DateUtil.parse(amEnd1));
            boolean isBeforeAmEnd = createTime.before(DateUtil.parse(amEnd1));
            boolean isAfterAmStart = createTime.equals(DateUtil.parse(amStart1)) || createTime.after(DateUtil.parse(amStart1));
            boolean a = afterAM && isAfterAmStart && isBeforeAmEnd;

            //当前时间小于下午可编辑停止时间，并且在可编辑时间范围内，则可编辑
            boolean afterPM = date.before(DateUtil.parse(pmEnd1));
            boolean isBeforePmEnd = createTime.before(DateUtil.parse(pmEnd1));
            boolean isAfterPmStart = createTime.equals(DateUtil.parse(pmStart1)) || createTime.after(DateUtil.parse(pmStart1));
            boolean p = afterPM && isBeforePmEnd && isAfterPmStart;

            edit = a || p;
        }
        return edit;
    }

    private void validateTimeParam(SysParamModel param, String paramName) {
        if (ObjectUtil.isEmpty(param)) {
            throw new AiurtBootException("工作日志" + paramName + "没有配置");
        } else if (!TimeUtil.isLegalDate(param.getValue().length(), param.getValue(), "HH:mm:ss")) {
            throw new AiurtBootException("工作日志" + paramName + "格式不正确");
        }
    }

    private Boolean additionalRecordingFlag(Date createTime) {
        SysParamModel amEnd = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_AM_STOPEDIT);
        SysParamModel pmEnd = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_PM_STOPEDIT);
        validateTimeParam(amEnd, "早上停止编辑时间");
        validateTimeParam(pmEnd, "下午停止编辑时间");
        String today = DateUtil.today();
        String amEnd1 = today + " " + amEnd.getValue();
        String pmEnd1 = today + " " + pmEnd.getValue();
        today = today + " " + "00:00:00";
        Date date = new Date();

        boolean beforeToday = createTime.before((DateUtil.parse(today)));
        if (beforeToday) {
            return true;
        } else {
            //判断该日志是晚班还是白班
            boolean a = createTime.after((DateUtil.parse(amEnd1))) && createTime.before((DateUtil.parse(pmEnd1)));
            boolean b = date.after((DateUtil.parse(amEnd1)));
            boolean c = date.after((DateUtil.parse(pmEnd1)));

            if (!a && b ) {
                //如果是白班且当前时间大于早上停止编辑时间，则补录
                return true;
            } else if (a && c) {
                //如果是晚班且当前时间大于晚上停止编辑时间，则补录
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public WorkLogIndexDTO getOverviewInfo(Date startDate, Date endDate, HttpServletRequest request) {
        WorkLogIndexDTO workLogIndexDTO = new WorkLogIndexDTO();

        // 查看开始时间到结束时间有多少天
        int days = (int) DateUtil.between(startDate, endDate, DateUnit.DAY) + 1;

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean isAdmin = SecurityUtils.getSubject().hasRole("admin") || SecurityUtils.getSubject().hasRole("zhuren");
        // 权限部门的orgId
        List<String> orgIdList = null;

        // 登录人的权限班组数量
        int teamNum;
        //只获取班组数量,组织机构类型不为公司部门
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_ORG_CATEGORY);
        List<String> orgCategoryList = StrUtil.splitTrim(sysParamModel.getValue(), ",");
        if (isAdmin){
            // 管理员和主任获取所有部门
            List<SysDepartModel> allSysDepart = iSysBaseAPI.getAllSysDepart();
            teamNum = (int) allSysDepart.stream().filter(s -> orgCategoryList.contains(s.getOrgCategory())).count();
        }else {
            // 其他角色获取权限部门的班组数量
            List<CsUserDepartModel> permitDepartList = iSysBaseAPI.getDepartByUserId(loginUser.getId());
            List<CsUserDepartModel> filterDepartList = permitDepartList.stream().filter(s -> orgCategoryList.contains(s.getOrgCategory())).collect(Collectors.toList());
            teamNum = filterDepartList.size();
            // 获取权限部门的orgId
            orgIdList = filterDepartList.stream().map(CsUserDepartModel::getDepartId).collect(Collectors.toList());
        }

        // 应提交日志数，每个班组每天是2个
        Integer shouldSubmitNum = 2 * teamNum * days;
        // 已提交日志数
        Integer submitNum = this.baseMapper.getSubmitNum(startDate, endDate, isAdmin ? null : orgIdList);
        // 未提交数，应提交-已提交
        Integer unSubmitNum = Math.max(shouldSubmitNum - submitNum, 0);
        // 获取前7个已提交的日志
        LambdaQueryWrapper<WorkLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkLog::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.eq(WorkLog::getStatus, 1);
        // 权限部门id过滤
        queryWrapper.in((!isAdmin && CollUtil.isNotEmpty(orgIdList)), WorkLog::getOrgId, orgIdList);
        queryWrapper.ge(WorkLog::getLogTime, DateUtil.beginOfDay(startDate));
        queryWrapper.le(WorkLog::getLogTime, DateUtil.endOfDay(endDate));
        queryWrapper.orderByDesc(WorkLog::getSubmitTime);
        queryWrapper.last("limit 7");
        // 查询已确认、待确认的字段值
        List<DictModel> workLogConfirmStatusList = iSysBaseAPI.getDictItems("work_log_confirm_status");
        List<WorkLogIndexShowDTO> workLogIndexShowDTOList = this.list(queryWrapper).stream().map(workLog -> {
            WorkLogIndexShowDTO workLogIndexShowDTO = new WorkLogIndexShowDTO();
            BeanUtils.copyProperties(workLog, workLogIndexShowDTO);
            String orgName = iSysBaseAPI.selectAllById(workLog.getOrgId()).getDepartName();
            workLogIndexShowDTO.setOrgName(orgName);
            for (DictModel dictModel : workLogConfirmStatusList) {
                if (dictModel.getValue().equals(workLogIndexShowDTO.getConfirmStatus().toString())) {
                    workLogIndexShowDTO.setConfirmStatusName(dictModel.getText());
                    break;
                }
            }
            return workLogIndexShowDTO;
        }).collect(Collectors.toList());

        workLogIndexDTO.setShouldSubmitNum(shouldSubmitNum);
        workLogIndexDTO.setSubmitNum(submitNum);
        workLogIndexDTO.setUnSubmitNum(unSubmitNum);
        workLogIndexDTO.setWorkLogIndexShowDTOList(workLogIndexShowDTOList);


        return workLogIndexDTO;
    }

    @Override
    public List<List<WorkLogDetailResult>> batchPrint(Page<WorkLogResult> page, WorkLogParam param, HttpServletRequest req) {
        // 要打印的日志的id
        List<String> idList;
        String selections = req.getParameter("selections");
        if (StrUtil.isNotEmpty(selections)){
            idList = Arrays.asList(selections.split(","));
        }else {
            idList = this.pageList(page, param, req).getRecords().stream().map(WorkLogResult::getId).collect(Collectors.toList());
        }

        // 打印的日志的id的Set，用这个allIdSet是为了多线程查询。allIdSet->{id1,id2,id3,id4}
        Set<String> allIdSet = new HashSet<>();
        // 要打印的日志的id，组合成每个早班晚班，放到set里，防止传入一个早班一个晚班时打印重复的数据，set->{[id1,id2], [id3,id4]}
        Set<List<String>> set = new HashSet<>();
        idList.forEach(id->{
            // 查出每个日志id，对应的晚班(早班)id
            List<String> sameDayIdList = this.baseMapper.getSameDayIdList(id);
            Collections.sort(sameDayIdList);
            set.add(sameDayIdList);
            allIdSet.addAll(sameDayIdList);
        });

        Map<String, WorkLogDetailResult> map = new ConcurrentHashMap<>();
        // 多线程查询，将结构存入map中
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        allIdSet.forEach(id->{
            threadPoolExecutor.execute(()->{
                WorkLogDetailResult workLogDetailResult = queryWorkLogDetail(id);
                map.put(id, workLogDetailResult);
            });
        });
        threadPoolExecutor.shutdown();
        try {
            // 等待线程池中的任务全部完成
            threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 处理中断异常
            log.info("循环方法的线程中断异常,{}", e.getMessage());
        }

        // 最终返回的结果
        List<List<WorkLogDetailResult>> resultList = new ArrayList<>();
        set.forEach(perIdList->{
            List<WorkLogDetailResult> perResult = perIdList.stream().map(map::get).collect(Collectors.toList());
            resultList.add(perResult);
        });
        return resultList;
    }

    /**发送消息*/
    public void sendMessage(String orgId,Date date ,Integer flag,String workLogId) {
        //根据部门id,获取部门下的当天上班的人员
        List<ScheduleRecord> allUserList =workLogRemindMapper.getWorkUserToday(date,orgId,flag);

        //获取排班人的用户id
        List<String> notWorkLogUserIds = allUserList.stream().map(ScheduleRecord::getUserId).collect(Collectors.toList());
        String[] userIds = notWorkLogUserIds.toArray(new String[0]);
        List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(userIds);
        List<String> userName = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userName)){
            //发消息提醒
            userName.forEach(
                    u->{
                        //发送通知
                        try {
                            MessageDTO messageDTO = new MessageDTO(null, u, "工作日志上报" + DateUtil.today(), null, com.aiurt.common.constant.CommonConstant.MSG_CATEGORY_8);
                            //构建消息模板
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, workLogId);
                            map.put(CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.WORKLOG.getType());
                            map.put("msgContent", "今日工作日志未上报");
                            messageDTO.setData(map);
                            messageDTO.setTemplateCode(com.aiurt.common.constant.CommonConstant.WORK_LOG_SERVICE_NOTICE);
                            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_MESSAGE);
                            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                            messageDTO.setMsgAbstract("工作日志上报");
                            messageDTO.setPublishingContent("今日工作日志未上报");
                            iSysBaseAPI.sendTemplateMessage(messageDTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }
}
