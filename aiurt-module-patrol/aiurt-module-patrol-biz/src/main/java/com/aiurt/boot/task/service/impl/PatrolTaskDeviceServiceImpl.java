package com.aiurt.boot.task.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.*;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.statistics.dto.IndexStationDTO;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.DateUtils;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolTaskDeviceServiceImpl extends ServiceImpl<PatrolTaskDeviceMapper, PatrolTaskDevice> implements IPatrolTaskDeviceService {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private PatrolAccompanyMapper patrolAccompanyMapper;
    @Autowired
    private PatrolSamplePersonMapper patrolSamplePersonMapper;
    @Autowired
    private PatrolCheckResultMapper patrolCheckResultMapper;
    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;
    @Autowired
    private PatrolStandardItemsMapper patrolStandardItemsMapper;
    @Autowired
    private PatrolAccessoryMapper patrolAccessoryMapper;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private PatrolManager manager;
    @Autowired
    private PatrolTaskFaultMapper patrolTaskFaultMapper;
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;


    @Override
    public IPage<PatrolTaskDeviceParam> selectBillInfo(Page<PatrolTaskDeviceParam> page, PatrolTaskDeviceParam patrolTaskDeviceParam) {
        return patrolTaskDeviceMapper.selectBillInfo(page, patrolTaskDeviceParam);
    }

    @Override
    public IPage<PatrolTaskDeviceParam> selectBillInfoForDevice(Page<PatrolTaskDeviceParam> page, PatrolTaskDeviceParam patrolTaskDeviceParam) {
        IPage<PatrolTaskDeviceParam> patrolTaskDeviceForDeviceParamPage = patrolTaskDeviceMapper.selectBillInfoForDevice(page, patrolTaskDeviceParam);
        List<PatrolTaskDeviceParam> records = patrolTaskDeviceForDeviceParamPage.getRecords();
        if (records != null && records.size() > 0) {
            for (PatrolTaskDeviceParam patrolTaskDeviceForDeviceParam : records) {
                // 计算巡检时长
                Date startTime = patrolTaskDeviceForDeviceParam.getStartTime();
                Date checkTime = patrolTaskDeviceForDeviceParam.getCheckTime();
                if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(checkTime)) {
                    long duration = DateUtil.between(startTime, checkTime, DateUnit.MINUTE);
                    patrolTaskDeviceForDeviceParam.setDuration(DateUtils.getTimeByMinute(duration));
                }
                // 查询同行人信息
                QueryWrapper<PatrolAccompany> accompanyWrapper = new QueryWrapper<>();
                accompanyWrapper.lambda().eq(PatrolAccompany::getTaskDeviceCode, patrolTaskDeviceForDeviceParam.getPatrolNumber());
                List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(accompanyWrapper);
                String res = "";
                if (accompanyList != null && accompanyList.size() > 0) {
                    for (PatrolAccompany patrolAccompany : accompanyList) {
                        res += patrolAccompany.getUsername() + ",";
                    }
                    res = res.substring(0, res.length() - 1);
                }
                patrolTaskDeviceForDeviceParam.setAccompanyInfoStr(res);
                patrolTaskDeviceForDeviceParam.setAccompanyInfo(accompanyList);
                PatrolTaskDeviceParam taskDeviceParam = Optional.ofNullable(patrolTaskDeviceMapper.selectBillInfoByNumber(patrolTaskDeviceForDeviceParam.getPatrolNumber()))
                        .orElseGet(PatrolTaskDeviceParam::new);
                List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getListByTaskDeviceId(taskDeviceParam.getId());
                // 统计检查项中正常项的数据
                long normalItem = Optional.ofNullable(checkResultList).orElseGet(Collections::emptyList)
                        .stream().filter(l -> PatrolConstant.RESULT_NORMAL.equals(l.getCheckResult())).count();
                // 统计检查项中异常项的数据
                long exceptionItem = Optional.ofNullable(checkResultList).orElseGet(Collections::emptyList)
                        .stream().filter(l -> PatrolConstant.RESULT_EXCEPTION.equals(l.getCheckResult())).count();
                patrolTaskDeviceForDeviceParam.setNormalItem(normalItem);
                patrolTaskDeviceForDeviceParam.setExceptionItem(exceptionItem);
            }
        }

        return patrolTaskDeviceForDeviceParamPage;
    }

    @Override
    public Page<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(Page<PatrolTaskDeviceDTO> pageList, String taskId, String search) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<PatrolTaskDeviceDTO> patrolTaskDeviceList = patrolTaskDeviceMapper.getPatrolTaskDeviceList(pageList, taskId, search);
        patrolTaskDeviceList.stream().forEach(e -> {
            if (ObjectUtil.isNull(e.getCheckResult())) {
                e.setCheckResult("-");
            }
            if (ObjectUtil.isNotEmpty( e.getStartTime()) && ObjectUtil.isNotEmpty( e.getCheckTime())) {
                long duration = DateUtil.between( e.getStartTime(),e.getCheckTime() , DateUnit.MINUTE);
                e.setInspectionTime(DateUtils.getTimeByMinute(duration));
            }
            List<StationDTO> codeList = new ArrayList<>();
            StationDTO stationDTO = new StationDTO();
            stationDTO.setLineCode(e.getLineCode());
            stationDTO.setStationCode(e.getStationCode());
            stationDTO.setPositionCode(e.getPositionCode());
            codeList.add(stationDTO);
            List<String> allPosition = patrolTaskDeviceMapper.getAllPosition(e.getStationCode());
            e.setAllPosition(allPosition == null ? new ArrayList<>() : allPosition);
            String positions = manager.translateStation(codeList);
            if (ObjectUtil.isNotEmpty(e.getDeviceCode())) {
                e.setDevicePosition(positions);
                List<StationDTO> stationDtos = new ArrayList<>();
                StationDTO station = new StationDTO();
                station.setLineCode(e.getLineCode());
                station.setStationCode(e.getStationCode());
                stationDtos.add(station);
                String stationName = manager.translateStation(stationDtos);
                e.setStationName(stationName);
            } else {
                if (ObjectUtil.isNotEmpty(e.getCustomPosition())) {
                    e.setInspectionPosition(positions + "/" + e.getCustomPosition());
                    e.setStationName(positions);
                    e.setDevicePosition(null);
                } else {
                    e.setStationName(positions);
                    e.setInspectionPosition("-");
                }
            }
            List<PatrolTaskFault> faultList = patrolTaskFaultMapper.selectList(new LambdaQueryWrapper<PatrolTaskFault>().eq(PatrolTaskFault::getPatrolNumber, e.getPatrolNumber()));
            List<String> collect = faultList.stream().map(PatrolTaskFault::getFaultCode).collect(Collectors.toList());
            e.setFaultList(collect);
            PatrolStandard taskStandardName = patrolTaskDeviceMapper.getStandardName(e.getId());
            e.setSubsystemCode(taskStandardName.getSubsystemCode());
            e.setProfessionCode(taskStandardName.getProfessionCode());
            e.setTaskStandardName(taskStandardName.getName());
            if(ObjectUtil.isNotEmpty(taskStandardName.getName())){
                String[] split = taskStandardName.getName().split("-");
                if(split.length==3){
                    e.setSpiltTaskStandardName(split[2]);
                }
            }
            e.setDeviceType(taskStandardName.getDeviceType());
            e.setStandardCode(taskStandardName.getCode());
            boolean nullSafetyPrecautions = sysBaseApi.isNullSafetyPrecautions(e.getProfessionCode(), e.getSubsystemCode(),taskStandardName.getCode(),0);
            e.setIsNullSafetyPrecautions(nullSafetyPrecautions);
            List<PatrolAccessoryDTO> patrolAccessoryDTOList = new ArrayList<>();
            patrolAccessoryDTOList.addAll(patrolAccessoryMapper.getCheckAllAccessory(e.getId()));
            e.setAccessoryDTOList(patrolAccessoryDTOList);
            e.setSubmitName(patrolTaskDeviceMapper.getSubmitName(e.getUserId()));
            PatrolTask patrolTask = patrolTaskMapper.selectById(e.getTaskId());
            List<PatrolTaskUser> userList = patrolTaskUserMapper.selectList(new LambdaQueryWrapper<PatrolTaskUser>().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()));
            List<PatrolTaskUser> showButton = userList.stream().filter(u -> u.getUserId().equals(sysUser.getId())).collect(Collectors.toList());
            if (showButton.size() > 0 || SecurityUtils.getSubject().hasRole(PatrolConstant.MANAGER)) {
                e.setShowEditButton(1);
            } else {
                e.setShowEditButton(0);
            }
            e.setOrgList(patrolTaskMapper.getOrgCode(patrolTask.getCode()));
            List<PatrolAccompanyDTO> accompanyDTOList = patrolAccompanyMapper.getAccompanyName(e.getPatrolNumber());
            String userName = accompanyDTOList.stream().map(PatrolAccompanyDTO::getUsername).collect(Collectors.joining("；"));
            e.setUserName(userName);
            e.setAccompanyName(accompanyDTOList);
            // 设置抽检人信息
            List<PatrolSamplePerson> samplePersonList = patrolSamplePersonMapper.getSamplePersonList(e.getPatrolNumber());
            String samplePersonName = samplePersonList.stream().map(PatrolSamplePerson::getUsername).collect(Collectors.joining(";"));
            e.setSamplePersonName(samplePersonName);
            e.setSamplePersonList(samplePersonList);
            List<PatrolCheckResult> list = patrolCheckResultMapper.selectList(new LambdaQueryWrapper<PatrolCheckResult>().eq(PatrolCheckResult::getTaskDeviceId, e.getId()));
            List<PatrolCheckResult> rightCheck = list.stream().filter(s -> s.getCheckResult() != null && 1 == s.getCheckResult()).collect(Collectors.toList());
            List<PatrolCheckResult> aberrant = list.stream().filter(s -> s.getCheckResult() != null && 0 == s.getCheckResult()).collect(Collectors.toList());
            e.setRightCheckNumber(rightCheck.size()==0?0:rightCheck.size());
            e.setAberrantNumber(aberrant.size()==0?0:aberrant.size());
        });
        return pageList.setRecords(patrolTaskDeviceList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getPatrolSubmit(PatrolTaskDevice patrolTaskDevice) {
        PatrolTaskDevice taskDevice = patrolTaskDeviceMapper.selectOne(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getId, patrolTaskDevice.getId()));
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        PatrolTask patrolTask = patrolTaskMapper.selectOne(new LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getId, taskDevice.getTaskId()));
        if (manager.checkTaskUser(patrolTask.getCode()) == false && !admin) {
            throw new AiurtBootException("只有该任务的巡检人才可以提交工单");
        } else {
            List<PatrolCheckResult> patrolCheckResultList = patrolCheckResultMapper.selectList(new LambdaQueryWrapper<PatrolCheckResult>().eq(PatrolCheckResult::getTaskDeviceId, taskDevice.getId()));
            List<PatrolCheckResult> collect = patrolCheckResultList.stream().filter(s -> s.getCheckResult() != null && PatrolConstant.RESULT_EXCEPTION.equals(s.getCheckResult())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                taskDevice.setCheckResult(PatrolConstant.RESULT_EXCEPTION);
            } else {
                taskDevice.setCheckResult(PatrolConstant.RESULT_NORMAL);
            }
            patrolTaskDeviceMapper.updateById(taskDevice);
            LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(PatrolTaskDevice::getUserId, sysUser.getId())
                    .set(PatrolTaskDevice::getCheckTime, LocalDateTime.now())
                    .set(PatrolTaskDevice::getStatus, PatrolConstant.BILL_COMPLETE)
                    .set(PatrolTaskDevice::getMac, patrolTaskDevice.getMac())
                    .eq(PatrolTaskDevice::getId, patrolTaskDevice.getId());
            patrolTaskDeviceMapper.update(patrolTaskDevice, updateWrapper);
            if(!patrolTask.getStatus().equals(PatrolConstant.TASK_BACK)){
                getPatrolTaskSubmit(patrolTask);
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void getPatrolTaskSubmit(PatrolTask task) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LoginUser user = sysBaseApi.getUserById(sysUser.getId());
        PatrolTask patrolTask = patrolTaskMapper.selectById(task.getId());
            List<PatrolTaskDevice> taskDevices = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, patrolTask.getId()));
            List<PatrolTaskDevice> errDeviceList = taskDevices.stream().filter(e -> PatrolConstant.RESULT_EXCEPTION.equals(e.getCheckResult())).collect(Collectors.toList());
            List<PatrolTaskDevice> nonSubmitDeviceList = taskDevices.stream().filter(e -> !PatrolConstant.BILL_COMPLETE.equals(e.getStatus())).collect(Collectors.toList());
            if(CollUtil.isEmpty(nonSubmitDeviceList)){
                LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
                if (PatrolConstant.TASK_CHECK.equals(patrolTask.getAuditor())) {
                    if (CollUtil.isNotEmpty(errDeviceList)) {
                        updateWrapper.set(PatrolTask::getStatus, 6)
                                .set(PatrolTask::getEndUserId, sysUser.getId())
                                .set(PatrolTask::getSignUrl, user.getSignatureUrl())
                                .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                                .set(PatrolTask::getAbnormalState, 0)
                                .eq(PatrolTask::getId, task.getId());
                    } else {
                        updateWrapper.set(PatrolTask::getStatus, 6)
                                .set(PatrolTask::getEndUserId, sysUser.getId())
                                .set(PatrolTask::getSignUrl, user.getSignatureUrl())
                                .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                                .set(PatrolTask::getAbnormalState, 1)
                                .eq(PatrolTask::getId, task.getId());
                    }
                } else {
                    if (CollUtil.isNotEmpty(errDeviceList)) {
                        updateWrapper.set(PatrolTask::getStatus, 7)
                                .set(PatrolTask::getEndUserId, sysUser.getId())
                                .set(PatrolTask::getSignUrl, user.getSignatureUrl())
                                .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                                .set(PatrolTask::getAbnormalState, 0)
                                .eq(PatrolTask::getId, task.getId());
                    } else {
                        updateWrapper.set(PatrolTask::getStatus, 7)
                                .set(PatrolTask::getEndUserId, sysUser.getId())
                                .set(PatrolTask::getSignUrl, user.getSignatureUrl())
                                .set(PatrolTask::getAbnormalState, 1)
                                .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                                .eq(PatrolTask::getId, task.getId());
                    }

                }

                //获取mac地址
                List<PatrolTaskDeviceDTO> mac = patrolTaskDeviceMapper.getMac(task.getId());
                List<IndexStationDTO> stationInfo = patrolTaskStationMapper.getStationInfo(task.getCode());
                List<String> list = Optional.ofNullable(stationInfo)
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .map(IndexStationDTO::getStationCode)
                        .collect(Collectors.toList());
                List<String> wifiMac = sysBaseApi.getWifiMacByStationCode(list);

                if (CollUtil.isNotEmpty(mac)) {
                    for (PatrolTaskDeviceDTO patrolTaskDeviceDTO : mac) {
                        if (StrUtil.isNotEmpty(patrolTaskDeviceDTO.getMac()) && CollUtil.isNotEmpty(wifiMac)) {
                            //忽略大小写全匹配
                            String mac1 = patrolTaskDeviceDTO.getMac();
                            String join = CollUtil.join(wifiMac, ",");
                            if (join.toLowerCase().contains(mac1.toLowerCase())) {
                                updateWrapper.set(PatrolTask::getMacStatus, 1);
                            } else {
                                updateWrapper.set(PatrolTask::getMacStatus, 0);
                                break;
                            }
                        }else {
                            updateWrapper.set(PatrolTask::getMacStatus, 0);
                            break;
                        }
                    }
                } else {
                    updateWrapper.set(PatrolTask::getMacStatus, 0);
                }
                patrolTaskMapper.update(new PatrolTask(), updateWrapper);
                // 提交任务如果需要审核则发送一条审核待办消息
                try {
                    if (PatrolConstant.TASK_CHECK.equals(patrolTask.getAuditor())) {
                        QueryWrapper<PatrolTaskOrganization> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(PatrolTaskOrganization::getTaskCode, patrolTask.getCode())
                                .eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0);
                        List<PatrolTaskOrganization> organizations = patrolTaskOrganizationMapper.selectList(wrapper);
                        List<String> orgCodes = organizations.stream().map(PatrolTaskOrganization::getOrgCode).collect(Collectors.toList());
                        String userName = sysBaseApi.getUserNameByOrgCodeAndRoleCode(orgCodes, Arrays.asList(RoleConstant.FOREMAN));

                        QueryWrapper<PatrolTaskUser> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()).eq(PatrolTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0);
                        List<PatrolTaskUser> taskUsers = patrolTaskUserMapper.selectList(queryWrapper);
                        if (CollectionUtil.isEmpty(taskUsers)) {
                            return;
                        }

                        String[] userIds = taskUsers.stream().map(PatrolTaskUser::getUserId).toArray(String[]::new);
                        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                        String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));

                        //构建消息模板
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("code",patrolTask.getCode());
                        map.put("patrolTaskName",patrolTask.getName());
                        List<String>  station = patrolTaskStationMapper.getStationByTaskCode(patrolTask.getCode());
                        map.put("patrolStation",CollUtil.join(station,","));
                        String patrolDate = DateUtil.format(patrolTask.getPatrolDate(), "yyyy-MM-dd");
                        map.put("patrolTaskTime",patrolDate);
                        map.put("patrolName", realNames);

                        //发送通知
                        MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),userName, "巡视任务-审核通过" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
                        PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
                        BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
                        //业务类型，消息类型，消息模板编码，摘要，发布内容
                    /*patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
                    messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("巡视任务完成");
                    messageDTO.setPublishingContent("巡视任务已完成，请确认");
                    sendMessage(messageDTO,realNames,null,patrolMessageDTO);*/
                        //发送代办
                        TodoDTO todoDTO = new TodoDTO();
                        todoDTO.setData(map);
                        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_MESSAGE_PROCESS);
                        todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");

                        todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                        todoDTO.setTitle("巡视任务-审核"+DateUtil.today());
                        todoDTO.setMsgAbstract("巡视任务完成");
                        todoDTO.setPublishingContent("巡视任务已完成，请确认");

                        todoDTO.setProcessDefinitionName("巡视管理");
                        todoDTO.setTaskName(patrolTask.getName() + "(待审核)");
                        todoDTO.setBusinessKey(patrolTask.getId());
                        todoDTO.setBusinessType(TodoBusinessTypeEnum.PATROL_AUDIT.getType());
                        todoDTO.setCurrentUserName(userName);
                        todoDTO.setTaskType(TodoTaskTypeEnum.PATROL.getType());
                        todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                        todoDTO.setUrl(PatrolMessageUrlConstant.AUDIT_URL);
                        todoDTO.setAppUrl(PatrolMessageUrlConstant.AUDIT_APP_URL);
                        isTodoBaseAPI.createTodoTask(todoDTO);

                        // 更新待办
                        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_EXECUTE.getType(), patrolTask.getId(), sysUser.getUsername(), "1");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }
    @Override
    public List<PatrolStationDTO> getBillGangedInfo(String taskId) {
        List<PatrolBillDTO> billGangedInfo = patrolTaskDeviceMapper.getBillGangedInfo(taskId);
        Map<String, List<PatrolBillDTO>> collect = billGangedInfo.stream().filter((t) -> StrUtil.isNotBlank(t.getStationCode())).collect(Collectors.groupingBy(PatrolBillDTO::getStationCode));
        List<PatrolStationDTO> stationList = new ArrayList<>();
        for (Map.Entry<String, List<PatrolBillDTO>> entry : collect.entrySet()) {
            String stationCode = entry.getKey();
            if (ObjectUtil.isEmpty(stationCode)) {
                continue;
            }
            PatrolStationDTO station = new PatrolStationDTO();
            station.setStationCode(stationCode);
            station.setStationName(patrolTaskDeviceMapper.getStationName(stationCode));
            station.setBillInfo(entry.getValue());
            stationList.add(station);
        }
        return stationList;
    }

    @Override
    public Map<String, Object> selectBillInfoByNumber(String patrolNumber) {
        PatrolTaskDeviceParam taskDeviceParam = Optional.ofNullable(patrolTaskDeviceMapper.selectBillInfoByNumber(patrolNumber))
                .orElseGet(PatrolTaskDeviceParam::new);
        List<PatrolTaskFault> faultList = patrolTaskFaultMapper.selectList(new LambdaQueryWrapper<PatrolTaskFault>().eq(PatrolTaskFault::getPatrolNumber, patrolNumber));
        List<String> faultCodeList = faultList.stream().map(f -> f.getFaultCode()).collect(Collectors.toList());
        taskDeviceParam.setFaultList(faultCodeList);
        // 计算巡检时长
        Date startTime = taskDeviceParam.getStartTime();
        Date checkTime = taskDeviceParam.getCheckTime();
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(checkTime)) {
            long duration = DateUtil.between(startTime, checkTime, DateUnit.MINUTE);
            taskDeviceParam.setDuration(DateUtils.getTimeByMinute(duration));
        }
        StationDTO stationDTO = new StationDTO();
        stationDTO.setLineCode(taskDeviceParam.getLineCode());
        stationDTO.setStationCode(taskDeviceParam.getStationCode());
        stationDTO.setPositionCode(taskDeviceParam.getPositionCode());
        List<StationDTO> stationDTOList = new ArrayList<>();
        stationDTOList.add(stationDTO);
        String s = manager.translateStation(stationDTOList);
        //设备的位置
        if (ObjectUtil.isNotEmpty(taskDeviceParam.getDeviceCode())) {
            taskDeviceParam.setDevicePositionName(s);
        } else {
            taskDeviceParam.setInspectionPositionName(s);
            taskDeviceParam.setDevicePositionName(null);
        }
        // 查询同行人信息
        QueryWrapper<PatrolAccompany> accompanyWrapper = new QueryWrapper<>();
        accompanyWrapper.lambda().eq(PatrolAccompany::getTaskDeviceCode, patrolNumber);
        List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(accompanyWrapper);
        taskDeviceParam.setAccompanyInfo(accompanyList);
        List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getListByTaskDeviceId(taskDeviceParam.getId());
        // 字典翻译
        Map<String, String> requiredItems = sysBaseApi.getDictItems(PatrolDictCode.ITEM_REQUIRED)
                .stream().filter(l->StrUtil.isNotEmpty(l.getText()))
                .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        checkResultList.stream().forEach(c -> {
            c.setRequiredDictName(requiredItems.get(String.valueOf(c.getRequired())));
            if (ObjectUtil.isNotNull(c.getDictCode())) {
                List<DictModel> list = sysBaseApi.getDictItems(c.getDictCode());
                list.stream().forEach(l -> {
                    if (PatrolConstant.DEVICE_INP_TYPE.equals(c.getInputType())) {
                        if (l.getValue().equals(c.getOptionValue())) {
                            c.setCheckDictName(l.getTitle());
                        }
                    }
                });
            }
            String userName = patrolTaskMapper.getUserName(c.getUserId());
            c.setCheckUserName(userName);
        });
        // 统计检查项中正常项的数据
        long normalItem = Optional.ofNullable(checkResultList).orElseGet(Collections::emptyList)
                .stream().filter(l -> PatrolConstant.RESULT_NORMAL.equals(l.getCheckResult())).count();
        // 统计检查项中异常项的数据
        long exceptionItem = Optional.ofNullable(checkResultList).orElseGet(Collections::emptyList)
                .stream().filter(l -> PatrolConstant.RESULT_EXCEPTION.equals(l.getCheckResult())).count();
        taskDeviceParam.setNormalItem(normalItem);
        taskDeviceParam.setExceptionItem(exceptionItem);
        // 放入项目的附件信息
        Optional.ofNullable(checkResultList).orElseGet(Collections::emptyList).stream().forEach(l -> {
            QueryWrapper<PatrolAccessory> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(PatrolAccessory::getCheckResultId, l.getId());
            List<PatrolAccessory> accessoryList = patrolAccessoryMapper.selectList(wrapper);
            l.setAccessoryInfo(accessoryList);
        });
        // 构建巡检项目树
        List<PatrolCheckResultDTO> tree = getTree(checkResultList, "0");
        Map<String, Object> map = new HashMap<>(16);
        map.put("taskDeviceParam", taskDeviceParam);
        map.put("itemTree", tree);
        return map;
    }

    /**
     * 构建巡检项目树
     *
     * @param list
     * @param parentId
     * @return
     */
    public List<PatrolCheckResultDTO> getTree(List<PatrolCheckResultDTO> list, String parentId) {
        // 树的根节点
        List<PatrolCheckResultDTO> tree = Optional.ofNullable(list).orElseGet(Collections::emptyList)
                .stream().filter(l -> ObjectUtil.isNotEmpty(l.getParentId()) && parentId.equals(l.getParentId()))
                .collect(Collectors.toList());
        // 非根节点数据
        List<PatrolCheckResultDTO> subList = Optional.ofNullable(list).orElseGet(Collections::emptyList)
                .stream().filter(l -> !parentId.equals(l.getParentId()))
                .collect(Collectors.toList());
        // 构建根节点下的子树
        tree.stream().forEach(l -> {
            List<PatrolCheckResultDTO> subTree = buildTree(subList, l.getOldId());
            l.setChildren(subTree);
        });
        return tree;
    }

    /**
     * 递归获取子树
     *
     * @param list
     * @param parentId
     * @return
     */
    public List<PatrolCheckResultDTO> buildTree(List<PatrolCheckResultDTO> list, String parentId) {
        List<PatrolCheckResultDTO> tree = new ArrayList<>();
        for (PatrolCheckResultDTO dept : list) {
            if (dept.getParentId() != null) {
                if (dept.getParentId().equals(parentId)) {
                    List<PatrolCheckResultDTO> subList = buildTree(list, dept.getId());
                    dept.setChildren(subList);
                    tree.add(dept);
                }
            }
        }
        return tree;
    }

    @Override
    public int copyItems(PatrolTaskDevice patrolTaskDevice) {
        String taskStandardId = patrolTaskDevice.getTaskStandardId();
        String taskDeviceId = patrolTaskDevice.getId();
        if (StrUtil.isEmpty(taskStandardId)) {
            throw new AiurtBootException("任务标准关联表ID为空！");
        }
        if (StrUtil.isEmpty(taskDeviceId)) {
            throw new AiurtBootException("记录主键ID为空！");
        }
        //根据任务设备的任务ID获取任务标准表主键和巡检标准表ID
        PatrolTaskStandard taskStandard = patrolTaskStandardMapper.selectById(taskStandardId);
        String standardId = taskStandard.getStandardId();
        //根据巡检标准表ID获取巡检标准项目列表并添加到结果表中
        List<PatrolStandardItems> patrolStandardItems = patrolStandardItemsMapper.selectItemList(standardId);
        if (CollUtil.isEmpty(patrolStandardItems)) {
            throw new AiurtBootException("小主！巡检标准的配置项，未配置哦！");
        }
        List<PatrolCheckResult> addResultList = new ArrayList<>();
        Optional.ofNullable(patrolStandardItems).orElseGet(Collections::emptyList).stream().forEach(l -> {
            PatrolCheckResult result = new PatrolCheckResult();
            result.setTaskStandardId(taskStandard.getId());   // 任务标准关联表ID
            result.setTaskDeviceId(taskDeviceId); // 任务设备关联表ID
            result.setCode(l.getCode());    // 巡检项编号
            result.setContent(l.getContent());  // 巡检项内容
            result.setQualityStandard(l.getQualityStandard());  // 质量标准
            result.setHierarchyType(l.getHierarchyType());  // 层级类型
            result.setOldId(l.getId()); // 原标准项目表ID
            result.setParentId(l.getParentId()); //父级ID
            result.setOrder(l.getOrder());  // 内容排序
            result.setCheck(l.getCheck());  // 是否为巡检项目
            result.setInputType(l.getInputType());  // 填写数据类型
            result.setDictCode(l.getDictCode());    // 关联的数据字典
            result.setRegular(l.getRegular());  // 数据校验表达式
            result.setDelFlag(0);  // 数据校验表达式
            result.setRequired(l.getRequired()); // 检查值是否必填
            addResultList.add(result);
        });
        // 批量添加巡检项目
        int resultList = patrolCheckResultMapper.addResultList(addResultList);
        return resultList;
    }

    /**
     * 构建检查结果项目树
     *
     * @param trees
     * @return
     */
    public static List<PatrolCheckResultDTO> buildResultTree(List<PatrolCheckResultDTO> trees) {
        //获取parentId = 0的根节点
        List<PatrolCheckResultDTO> list = trees.stream().filter(item -> "0".equals(item.getParentId())).collect(Collectors.toList());
        //根据parentId进行分组
        Map<String, List<PatrolCheckResultDTO>> map = trees.stream().collect(Collectors.groupingBy(PatrolCheckResultDTO::getParentId));
        recursionTree(list, map);
        return list;
    }

    /**
     * 递归遍历节点
     *
     * @param list
     * @param map
     */
    public static void recursionTree(List<PatrolCheckResultDTO> list, Map<String, List<PatrolCheckResultDTO>> map) {
        for (PatrolCheckResultDTO treeSelect : list) {
            List<PatrolCheckResultDTO> childList = map.get(treeSelect.getOldId());
            treeSelect.setChildren(childList);
            if (null != childList && 0 < childList.size()) {
                recursionTree(childList, map);
            }
        }
    }


    @Override
    public List<PatrolCheckResultDTO> getPatrolTaskCheck(PatrolTaskDevice patrolTaskDevice, Integer checkDetail) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDevice.getTaskId());
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        //checkDetail 为了做查看用，不做限制
        if (manager.checkTaskUser(patrolTask.getCode()) == false && ObjectUtil.isNull(checkDetail) && !admin) {
            throw new AiurtBootException("只有该任务的巡检人才可以填写工单");
        } else {
            //更新任务状态（将未开始改为执行中）、添加开始检查时间(判断是否已经有了，有就不更新)，传任务主键id,巡检工单主键
            String taskDeviceId = patrolTaskDevice.getId();
            int status = 2;
            PatrolTaskDevice device = patrolTaskDeviceMapper.selectOne(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getId, patrolTaskDevice.getId()));
            if (ObjectUtil.isNull(checkDetail)&&patrolTaskDevice.getStatus()!=status) {
                if (!PatrolConstant.TASK_AUDIT.equals(patrolTask.getStatus()) && !PatrolConstant.TASK_COMPLETE.equals(patrolTask.getStatus())) {
                    LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(PatrolTaskDevice::getStatus, 1)
                            .set(PatrolTaskDevice::getCheckTime, null)
                            .eq(PatrolTaskDevice::getTaskId, patrolTaskDevice.getTaskId())
                            .eq(PatrolTaskDevice::getId, patrolTaskDevice.getId());
                    if (device.getStartTime() == null) {
                        updateWrapper.set(PatrolTaskDevice::getStartTime, LocalDateTime.now());
                    }
                    patrolTaskDeviceMapper.update(new PatrolTaskDevice(), updateWrapper);
                }
            }
            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckResult(taskDeviceId);
            // 字典翻译
            Map<String, String> requiredItems = sysBaseApi.getDictItems(PatrolDictCode.ITEM_REQUIRED)
                    .stream().filter(l-> StrUtil.isNotEmpty(l.getText()))
                    .collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
            checkResultList.stream().forEach(e ->
            {
                e.setRequiredDictName(requiredItems.get(String.valueOf(e.getRequired())));
                if (ObjectUtil.isNotNull(e.getDictCode())) {
                    List<DictModel> list = sysBaseApi.getDictItems(e.getDictCode());
                    e.setList(list);
                    list.stream().forEach(l -> {
                        if (PatrolConstant.DEVICE_INP_TYPE.equals(e.getInputType())) {
                            if (l.getValue().equals(e.getOptionValue())) {
                                e.setCheckDictName(l.getTitle());
                            }
                        }
                    });
                }
                String userName = patrolTaskMapper.getUserName(e.getUserId());
                e.setCheckUserName(userName);
                //获取这个单号下一个巡检项的所有附件
                List<PatrolAccessoryDTO> patrolAccessoryDto = patrolAccessoryMapper.getAllAccessory(patrolTaskDevice.getId(), e.getId());
                e.setAccessoryDTOList(patrolAccessoryDto);
            });
            List<PatrolCheckResultDTO> resultList = buildResultTree(Optional.ofNullable(checkResultList)
                    .orElseGet(Collections::emptyList));
            return resultList;
        }
    }

    @Override
    public Device getDeviceInfoByCode(String deviceCode) {
        return patrolTaskDeviceMapper.getDeviceInfoByCode(deviceCode);
    }

    @Override
    public PatrolTaskDeviceDTO getPatrolTaskDeviceDetail(String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        PatrolTaskDeviceDTO e = patrolTaskDeviceMapper.getTaskDeviceDetail(id);
            if (ObjectUtil.isNull(e.getCheckResult())) {
                e.setCheckResult("-");
            }
            if (ObjectUtil.isNotEmpty( e.getStartTime()) && ObjectUtil.isNotEmpty( e.getCheckTime())) {
                long duration = DateUtil.between( e.getStartTime(),e.getCheckTime() , DateUnit.MINUTE);
                e.setInspectionTime(DateUtils.getTimeByMinute(duration));
            }
            List<StationDTO> codeList = new ArrayList<>();
            StationDTO stationDTO = new StationDTO();
            stationDTO.setLineCode(e.getLineCode());
            stationDTO.setStationCode(e.getStationCode());
            stationDTO.setPositionCode(e.getPositionCode());
            codeList.add(stationDTO);
            List<String> allPosition = patrolTaskDeviceMapper.getAllPosition(e.getStationCode());
            e.setAllPosition(allPosition == null ? new ArrayList<>() : allPosition);
            String positions = manager.translateStation(codeList);
            if (ObjectUtil.isNotEmpty(e.getDeviceCode())) {
                e.setDevicePosition(positions);
                List<StationDTO> stationDtos = new ArrayList<>();
                StationDTO station = new StationDTO();
                station.setLineCode(e.getLineCode());
                station.setStationCode(e.getStationCode());
                stationDtos.add(station);
                String stationName = manager.translateStation(stationDtos);
                e.setStationName(stationName);
            } else {
                if (ObjectUtil.isNotEmpty(e.getCustomPosition())) {
                    e.setInspectionPosition(positions + "/" + e.getCustomPosition());
                    e.setStationName(positions);
                    e.setDevicePosition(null);
                } else {
                    e.setStationName(positions);
                }
            }
            List<PatrolTaskFault> faultList = patrolTaskFaultMapper.selectList(new LambdaQueryWrapper<PatrolTaskFault>().eq(PatrolTaskFault::getPatrolNumber, e.getPatrolNumber()));
            List<String> collect = faultList.stream().map(PatrolTaskFault::getFaultCode).collect(Collectors.toList());
            e.setFaultList(collect);
            PatrolStandard taskStandardName = patrolTaskDeviceMapper.getStandardName(e.getId());
            e.setSubsystemCode(taskStandardName.getSubsystemCode());
            e.setProfessionCode(taskStandardName.getProfessionCode());
            e.setTaskStandardName(taskStandardName.getName());
            if(ObjectUtil.isNotEmpty(taskStandardName.getName())){
            String[] split = taskStandardName.getName().split("-");
            if(split.length==3){
                e.setSpiltTaskStandardName(split[2]);
            }
            }
            e.setDeviceType(taskStandardName.getDeviceType());
            e.setStandardCode(taskStandardName.getCode());
            boolean nullSafetyPrecautions = sysBaseApi.isNullSafetyPrecautions(e.getProfessionCode(), e.getSubsystemCode(),taskStandardName.getCode(),0);
            e.setIsNullSafetyPrecautions(nullSafetyPrecautions);
            List<PatrolAccessoryDTO> patrolAccessoryDTOList = new ArrayList<>();
            patrolAccessoryDTOList.addAll(patrolAccessoryMapper.getCheckAllAccessory(e.getId()));
            e.setAccessoryDTOList(patrolAccessoryDTOList);
            e.setSubmitName(patrolTaskDeviceMapper.getSubmitName(e.getUserId()));
            PatrolTask patrolTask = patrolTaskMapper.selectById(e.getTaskId());
            List<PatrolTaskUser> userList = patrolTaskUserMapper.selectList(new LambdaQueryWrapper<PatrolTaskUser>().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()));
            List<PatrolTaskUser> showButton = userList.stream().filter(u -> u.getUserId().equals(sysUser.getId())).collect(Collectors.toList());
            if (showButton.size() > 0 || SecurityUtils.getSubject().hasRole(PatrolConstant.MANAGER)) {
                e.setShowEditButton(1);
            } else {
                e.setShowEditButton(0);
            }
            e.setOrgList(patrolTaskMapper.getOrgCode(patrolTask.getCode()));
            List<PatrolAccompanyDTO> accompanyDTOList = patrolAccompanyMapper.getAccompanyName(e.getPatrolNumber());
            String userName = accompanyDTOList.stream().map(PatrolAccompanyDTO::getUsername).collect(Collectors.joining("；"));
            e.setUserName(userName);
            e.setAccompanyName(accompanyDTOList);
            // 设置抽检人信息
            List<PatrolSamplePerson> samplePersonList = patrolSamplePersonMapper.getSamplePersonList(e.getPatrolNumber());
            String samplePersonName = samplePersonList.stream().map(PatrolSamplePerson::getUsername).collect(Collectors.joining(";"));
            e.setSamplePersonName(samplePersonName);
            e.setSamplePersonList(samplePersonList);
            List<PatrolCheckResult> list = patrolCheckResultMapper.selectList(new LambdaQueryWrapper<PatrolCheckResult>().eq(PatrolCheckResult::getTaskDeviceId, e.getId()));
            List<PatrolCheckResult> rightCheck = list.stream().filter(s -> s.getCheckResult() != null && 1 == s.getCheckResult()).collect(Collectors.toList());
            List<PatrolCheckResult> aberrant = list.stream().filter(s -> s.getCheckResult() != null && 0 == s.getCheckResult()).collect(Collectors.toList());
            e.setRightCheckNumber(rightCheck.size()==0?0:rightCheck.size());
            e.setAberrantNumber(aberrant.size()==0?0:aberrant.size());
        return e;
    }
}
