package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.task.dto.PatrolAccessoryDTO;
import com.aiurt.boot.task.dto.PatrolAccompanyDTO;
import com.aiurt.boot.task.dto.PatrolCheckResultDTO;
import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private PatrolCheckResultMapper patrolCheckResultMapper;
    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;
    @Autowired
    private PatrolStandardItemsMapper patrolStandardItemsMapper;
    @Autowired
    private PatrolAccessoryMapper patrolAccessoryMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private PatrolManager manager;


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
                    long second = DateUtil.between(startTime, checkTime, DateUnit.SECOND);
                    if (second % 60 > 0) {
                        duration += 1;
                    }
                    patrolTaskDeviceForDeviceParam.setDuration(duration);
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
        List<PatrolTaskDeviceDTO> patrolTaskDeviceList = patrolTaskDeviceMapper.getPatrolTaskDeviceList(pageList, taskId,search);
        patrolTaskDeviceList.stream().forEach(e -> {
            if(ObjectUtil.isNull(e.getCheckResult()))
            { e.setCheckResult("-"); }
            Date startTime = e.getStartTime();
            Date checkTime = e.getCheckTime();
            if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(checkTime)) {
                long duration = DateUtil.between(startTime, checkTime, DateUnit.MINUTE);
                long second = DateUtil.between(startTime, checkTime, DateUnit.SECOND);
                if (second % 60 > 0) {
                    duration += 1;
                }
                e.setInspectionTime(duration);
            }
            List<StationDTO> codeList = new ArrayList<>();
            StationDTO stationDTO =new StationDTO();
            stationDTO.setLineCode(e.getLineCode());
            stationDTO.setStationCode(e.getStationCode());
            stationDTO.setPositionCode(e.getPositionCode());
            codeList.add(stationDTO);
            List<String> allPosition = patrolTaskDeviceMapper.getAllPosition(e.getStationCode());
            e.setAllPosition(allPosition== null ?new ArrayList<>():allPosition);
            String positions = manager.translateStation(codeList);
            if(ObjectUtil.isNotEmpty(e.getDeviceCode()))
            {
                List<StationDTO> stationDTOS = new ArrayList<>();
                StationDTO station =new StationDTO();
                station.setLineCode(e.getLineCode());
                station.setStationCode(e.getStationCode());
                stationDTOS.add(station);
                String stationName = manager.translateStation(stationDTOS);
                e.setStationName(stationName);
            }
            else {
                if(ObjectUtil.isNotEmpty(e.getCustomPosition()))
                {
                    e.setInspectionPosition(positions+"/"+e.getCustomPosition());
                    e.setStationName(positions);
                    e.setDevicePosition(null);
                }
                else
                {
                    e.setStationName(positions);
                    e.setInspectionPosition("-");
                }
            }
            PatrolStandard taskStandardName = patrolTaskDeviceMapper.getStandardName(e.getId());
            e.setSubsystemCode(taskStandardName.getSubsystemCode());
            e.setProfessionCode(taskStandardName.getProfessionCode());
            e.setTaskStandardName(taskStandardName.getName());
            e.setDeviceType(taskStandardName.getDeviceType());
            LambdaQueryWrapper<PatrolCheckResult> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<PatrolAccessoryDTO> patrolAccessoryDTOList = new ArrayList<>();
            lambdaQueryWrapper.eq(PatrolCheckResult::getTaskDeviceId, e.getId());
            patrolAccessoryDTOList.addAll(patrolAccessoryMapper.getCheckAllAccessory(e.getId()));
            e.setAccessoryDTOList(patrolAccessoryDTOList);
            String submitName = patrolTaskDeviceMapper.getSubmitName(e.getUserId());
            e.setSubmitName(submitName);
            PatrolTask patrolTask = patrolTaskMapper.selectById(e.getTaskId());
            List<String> orgCodes = patrolTaskMapper.getOrgCode(patrolTask.getCode());
            e.setOrgList(orgCodes);
            List<PatrolAccompanyDTO> accompanyDTOList = patrolAccompanyMapper.getAccompanyName(e.getPatrolNumber());
            String userName = accompanyDTOList.stream().map(PatrolAccompanyDTO::getUsername).collect(Collectors.joining(","));
            e.setUserName(userName);
            e.setAccompanyName(accompanyDTOList);
            LambdaQueryWrapper<PatrolCheckResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PatrolCheckResult::getTaskDeviceId, e.getId());
            List<PatrolCheckResult> list = patrolCheckResultMapper.selectList(queryWrapper);
            List<PatrolCheckResult> rightCheck = list.stream().filter(s -> s.getCheckResult() != null && 1==s.getCheckResult()).collect(Collectors.toList());
            List<PatrolCheckResult> aberrant = list.stream().filter(s -> s.getCheckResult() != null && 0==s.getCheckResult()).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(rightCheck)) {
                e.setRightCheckNumber(rightCheck.size());
            } else {
                e.setRightCheckNumber(0);
            }
            if (CollUtil.isNotEmpty(aberrant)) {
                e.setAberrantNumber(aberrant.size());
            } else {
                e.setAberrantNumber(0);
            }
        });
        return pageList.setRecords(patrolTaskDeviceList);
    }

    @Override
    public void getPatrolSubmit(PatrolTaskDevice patrolTaskDevice) {
        PatrolTaskDevice taskDevice = patrolTaskDeviceMapper.selectOne(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getId, patrolTaskDevice.getId()));
        PatrolTask patrolTask = patrolTaskMapper.selectOne(new LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getId, taskDevice.getTaskId()));
        if(manager.checkTaskUser(patrolTask.getCode())==false)
        {
            throw new AiurtBootException("小主，该巡检任务不在您的提交范围之内哦");
        }
        else
        {
                List<PatrolCheckResult> patrolCheckResultList = patrolCheckResultMapper.selectList(new LambdaQueryWrapper<PatrolCheckResult>().eq(PatrolCheckResult::getTaskDeviceId, taskDevice.getId()));
                List<PatrolCheckResult> collect = patrolCheckResultList.stream().filter(s -> s.getCheckResult() != null && PatrolConstant.RESULT_EXCEPTION.equals(s.getCheckResult())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect)) {
                    taskDevice.setCheckResult(PatrolConstant.RESULT_EXCEPTION);
                } else {
                    taskDevice.setCheckResult(PatrolConstant.RESULT_NORMAL);
                }
                patrolTaskDeviceMapper.updateById(taskDevice);
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper= new LambdaUpdateWrapper<>();
            updateWrapper.set(PatrolTaskDevice::getUserId,sysUser.getId()).set(PatrolTaskDevice::getCheckTime, LocalDateTime.now()).set(PatrolTaskDevice::getStatus,PatrolConstant.BILL_COMPLETE).eq(PatrolTaskDevice::getId,patrolTaskDevice.getId());
            patrolTaskDeviceMapper.update(patrolTaskDevice,updateWrapper);
        }
    }

    @Override
    public Map<String, Object> selectBillInfoByNumber(String patrolNumber) {
        PatrolTaskDeviceParam taskDeviceParam = Optional.ofNullable(patrolTaskDeviceMapper.selectBillInfoByNumber(patrolNumber))
                .orElseGet(PatrolTaskDeviceParam::new);
        // 计算巡检时长
        Date startTime = taskDeviceParam.getStartTime();
        Date checkTime = taskDeviceParam.getCheckTime();
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(checkTime)) {
            long duration = DateUtil.between(startTime, checkTime, DateUnit.MINUTE);
            long second = DateUtil.between(startTime, checkTime, DateUnit.SECOND);
            if (second % 60 > 0) {
                duration += 1;
            }
            taskDeviceParam.setDuration(duration);
        }
        StationDTO stationDTO = new StationDTO();
        stationDTO.setLineCode(taskDeviceParam.getLineCode());
        stationDTO.setStationCode(taskDeviceParam.getStationCode());
        stationDTO.setPositionCode(taskDeviceParam.getPositionCode());
        List<StationDTO> stationDTOList = new ArrayList<>();
        stationDTOList.add(stationDTO);
        String s = manager.translateStation(stationDTOList);
        //设备的位置
        if(ObjectUtil.isNotEmpty(taskDeviceParam.getDeviceCode()))
        {
            taskDeviceParam.setDevicePositionName(s);
        }
        else {
            taskDeviceParam.setInspectionPositionName(s);
            taskDeviceParam.setDevicePositionName(null);
        }
        // 查询同行人信息
        QueryWrapper<PatrolAccompany> accompanyWrapper = new QueryWrapper<>();
        accompanyWrapper.lambda().eq(PatrolAccompany::getTaskDeviceCode, patrolNumber);
        List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(accompanyWrapper);
        taskDeviceParam.setAccompanyInfo(accompanyList);
        List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getListByTaskDeviceId(taskDeviceParam.getId());
        checkResultList.stream().forEach(c->{
            if(ObjectUtil.isNotNull(c.getDictCode()))
            {
                    List<DictModel> list = sysBaseAPI.getDictItems(c.getDictCode());
                    list.stream().forEach(l->{
                        if(2==c.getInputType())
                        {
                            if(l.getValue().equals(c.getOptionValue()))
                            {
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
        Map<String, Object> map = new HashMap<>();
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

    // 递归获取子树
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
        if(CollUtil.isEmpty(patrolStandardItems))
        { throw new AiurtBootException("小主！巡检标准的配置项，未配置哦！"); }
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
    public List<PatrolCheckResultDTO> getPatrolTaskCheck(PatrolTaskDevice patrolTaskDevice,Integer checkDetail) {
        PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDevice.getTaskId());
        if(manager.checkTaskUser(patrolTask.getCode())==false&&ObjectUtil.isNull(checkDetail))
        {
                throw new AiurtBootException("小主，该巡检任务不在您的检查范围之内哦");
        }
        else {
            //更新任务状态（将未开始改为执行中）、添加开始检查时间，传任务主键id,巡检工单主键
            String taskDeviceId = patrolTaskDevice.getId();
            if(ObjectUtil.isNull(checkDetail))
            {
                if(!PatrolConstant.TASK_AUDIT.equals(patrolTask.getStatus())&& !PatrolConstant.TASK_COMPLETE.equals(patrolTask.getStatus()))
                {
                    LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(PatrolTaskDevice::getStatus, 1)
                            .set(PatrolTaskDevice::getStartTime, LocalDateTime.now())
                            .eq(PatrolTaskDevice::getTaskId, patrolTaskDevice.getTaskId())
                            .eq(PatrolTaskDevice::getId, patrolTaskDevice.getId());
                    patrolTaskDeviceMapper.update(new PatrolTaskDevice(), updateWrapper);
                }
            }
           List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckResult(taskDeviceId);
                checkResultList.stream().forEach(e ->
                {
                    if(ObjectUtil.isNotNull(e.getDictCode()))
                    {
                        List<DictModel> list = sysBaseAPI.getDictItems(e.getDictCode());
                        e.setList(list);
                        list.stream().forEach(l->{
                            if(2==e.getInputType())
                            {
                                if(l.getValue().equals(e.getOptionValue()))
                                {
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
}
