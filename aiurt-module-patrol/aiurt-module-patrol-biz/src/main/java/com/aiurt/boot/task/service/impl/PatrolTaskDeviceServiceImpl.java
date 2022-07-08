package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
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
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
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
            }
        }

        return patrolTaskDeviceForDeviceParamPage;
    }

    @Override
    public Page<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(Page<PatrolTaskDeviceDTO> pageList, String taskId, String search) {
        List<PatrolTaskDeviceDTO> patrolTaskDeviceList = patrolTaskDeviceMapper.getPatrolTaskDeviceList(pageList, taskId,search);
        patrolTaskDeviceList.stream().forEach(e -> {
            Date startTime = e.getStartTime();
            Date checkTime = e.getCheckTime();
            if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(checkTime)) {
                long duration = DateUtil.between(startTime, checkTime, DateUnit.MINUTE);
                e.setInspectionTime(duration);
            }
            if(ObjectUtil.isNotEmpty(e.getDeviceCode()))
            {
                e.setDevicePosition(e.getDevicePosition());
            }
            else {
                e.setInspectionPosition(e.getDevicePosition()+"/"+e.getCustomPosition());
                e.setDevicePosition(null);
            }
            PatrolStandard taskStandardName = patrolTaskDeviceMapper.getStandardName(e.getId());
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
            List<String> position = patrolTaskDeviceMapper.getPosition(patrolTask.getCode());
            String stationName = position.stream().collect(Collectors.joining(","));
            e.setStationName(stationName);
//            StringBuffer stringBuffer = new StringBuffer();
//            Integer length = position.size();
//            AtomicReference<Integer> size = new AtomicReference<>(length);
//            position.stream().forEach(s -> {
//                size.set(size.get() - 1);
//                stringBuffer.append(s);
//                if (ObjectUtil.isNotEmpty(e.getCustomPosition())) {
//                    stringBuffer.append("/");
//                    stringBuffer.append(e.getCustomPosition());
//                }
//                if (size.get() != 0) {
//                    stringBuffer.append(",");
//                }
//            });
//            e.setInspectionPosition(stringBuffer.toString());
            List<PatrolAccompanyDTO> accompanyDTOList = patrolAccompanyMapper.getAccompanyName(e.getPatrolNumber());
            String userName = accompanyDTOList.stream().map(PatrolAccompanyDTO::getUsername).collect(Collectors.joining(","));
            e.setUserName(userName);
            e.setAccompanyName(accompanyDTOList);
            LambdaQueryWrapper<PatrolCheckResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PatrolCheckResult::getTaskDeviceId, e.getId()).eq(PatrolCheckResult::getHierarchyType,1);
            List<PatrolCheckResult> list = patrolCheckResultMapper.selectList(queryWrapper);
            List<PatrolCheckResult> rightCheck = list.stream().filter(s -> s.getCheckResult() != null && s.getCheckResult() == 1).collect(Collectors.toList());
            List<PatrolCheckResult> aberrant = list.stream().filter(s -> s.getCheckResult() != null && s.getCheckResult() == 0).collect(Collectors.toList());
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
    public Map<String, Object> selectBillInfoByNumber(String patrolNumber) {
        PatrolTaskDeviceParam taskDeviceParam = Optional.ofNullable(patrolTaskDeviceMapper.selectBillInfoByNumber(patrolNumber))
                .orElseGet(PatrolTaskDeviceParam::new);

        // 计算巡检时长
        Date startTime = taskDeviceParam.getStartTime();
        Date checkTime = taskDeviceParam.getCheckTime();
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(checkTime)) {
            long duration = DateUtil.between(startTime, checkTime, DateUnit.MINUTE);
            taskDeviceParam.setDuration(duration);
        }

        // 查询同行人信息
        QueryWrapper<PatrolAccompany> accompanyWrapper = new QueryWrapper<>();
        accompanyWrapper.lambda().eq(PatrolAccompany::getTaskDeviceCode, patrolNumber);
        List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(accompanyWrapper);
        taskDeviceParam.setAccompanyInfo(accompanyList);

        List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getListByTaskDeviceId(taskDeviceParam.getId());

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
            List<PatrolCheckResultDTO> subTree = buildTree(subList, l.getId());
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
        List<PatrolStandardItems> patrolStandardItems = patrolStandardItemsMapper.selectList(standardId);
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
            addResultList.add(result);
        });
        // 批量添加巡检项目
        int resultList = patrolCheckResultMapper.addResultList(addResultList);

//        QueryWrapper<PatrolCheckResultDTO> resultWrapper = new QueryWrapper<>();
//        resultWrapper.lambda().eq(PatrolCheckResult::getTaskDeviceId, taskDeviceId)
//                .eq(PatrolCheckResult::getTaskStandardId, taskStandard.getId());
//
//        List<PatrolCheckResult> resultList = buildResultTree(Optional.ofNullable(patrolCheckResultMapper.selectList(resultWrapper))
//                .orElseGet(Collections::emptyList));
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
    public List<PatrolCheckResultDTO> getPatrolTaskCheck(PatrolTaskDevice patrolTaskDevice) {
        //更新任务状态（将未开始改为执行中）、添加开始检查时间，传任务主键id,巡检工单主键
        if (patrolTaskDevice.getStatus() == 0) {
            LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(PatrolTaskDevice::getStatus, 1)
                    .set(PatrolTaskDevice::getStartTime, LocalDateTime.now())
                    .eq(PatrolTaskDevice::getTaskId, patrolTaskDevice.getTaskId())
                    .eq(PatrolTaskDevice::getId, patrolTaskDevice.getId());
            patrolTaskDeviceMapper.update(patrolTaskDevice, updateWrapper);
            copyItems(patrolTaskDevice);
        }
        String taskDeviceId = patrolTaskDevice.getId();
        List<PatrolCheckResultDTO> patrolCheckResultDTOList = patrolCheckResultMapper.getCheckResult(taskDeviceId);
        if(CollUtil.isNotEmpty(patrolCheckResultDTOList))
        {
            patrolCheckResultDTOList.stream().forEach(e ->
            {
                if(ObjectUtil.isNotNull(e.getDictCode()))
                {

                    List<DictModel> list = sysBaseAPI.getDictItems(e.getDictCode());
                    e.setList(list);
                }
                //获取这个单号下一个巡检项的所有附件
                List<PatrolAccessoryDTO> patrolAccessoryDto = patrolAccessoryMapper.getAllAccessory(patrolTaskDevice.getId(), e.getId());
                e.setAccessoryDTOList(patrolAccessoryDto);
            });
        }
        List<PatrolCheckResultDTO> resultList = buildResultTree(Optional.ofNullable(patrolCheckResultDTOList)
                .orElseGet(Collections::emptyList));
        return resultList;
    }

    @Override
    public Device getDeviceInfoByCode(String deviceCode) {
        return patrolTaskDeviceMapper.getDeviceInfoByCode(deviceCode);
    }
}
