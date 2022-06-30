package com.aiurt.boot.task.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.task.dto.PatrolCheckResultDTO;
import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.mapper.PatrolAccompanyMapper;
import com.aiurt.boot.task.mapper.PatrolCheckResultMapper;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStandardMapper;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private PatrolAccompanyMapper patrolAccompanyMapper;
    @Autowired
    private PatrolCheckResultMapper patrolCheckResultMapper;

    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;

    @Autowired
    private PatrolStandardItemsMapper patrolStandardItemsMapper;


    @Override
    public IPage<PatrolTaskDeviceParam> selectBillInfo(Page<PatrolTaskDeviceParam> page, PatrolTaskDeviceParam patrolTaskDeviceParam) {
        return patrolTaskDeviceMapper.selectBillInfo(page, patrolTaskDeviceParam);
    }

    @Override
    public Page<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(Page<PatrolTaskDeviceDTO> pageList, String id) {

        List<PatrolTaskDeviceDTO> patrolTaskDeviceList = patrolTaskDeviceMapper.getPatrolTaskDeviceList(pageList, id);
        patrolTaskDeviceList.stream().forEach(e -> {
            String accompanyName = patrolAccompanyMapper.getAccompanyName(e.getPatrolNumber());
            e.setAccompanyName(accompanyName);
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

        QueryWrapper<PatrolAccompany> accompanyWrapper = new QueryWrapper<>();
        accompanyWrapper.lambda().eq(PatrolAccompany::getTaskDeviceCode, patrolNumber);
        List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(accompanyWrapper);
        taskDeviceParam.setAccompanyInfo(accompanyList);

        // 构建巡检项目树
        List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getListByTaskDeviceId(taskDeviceParam.getId());
        List<PatrolCheckResultDTO> tree = getTree(checkResultList, "0");
        Map<String, Object> map = new HashMap<>();
        map.put("taskDeviceParam", taskDeviceParam);
        map.put("itemTree", tree);
        return map;
    }

    @Override
    public void startCheck(PatrolTaskDevice patrolTaskDevice) {
        String taskStandardId = patrolTaskDevice.getTaskStandardId();
        if (StrUtil.isEmpty(taskStandardId)) {
            throw new AiurtBootException("任务标准关联表ID为空！");
        }
        //根据任务设备的任务ID获取任务标准表主键和巡检标准表ID
        PatrolTaskStandard taskStandard = patrolTaskStandardMapper.selectById(taskStandardId);
        String standardId = taskStandard.getStandardId();
        //根据巡检标准表ID获取巡检标准项目列表并添加到结果表中
//        QueryWrapper<PatrolStandardItems> itemsWrapper = new QueryWrapper<>();
//        itemsWrapper.lambda().eq(PatrolStandardItems::getStandardId, standardId);
        List<PatrolStandardItems> patrolStandardItems = patrolStandardItemsMapper.selectList(standardId);
        List<PatrolCheckResult> resultList = new ArrayList<>();
        Optional.ofNullable(patrolStandardItems).orElseGet(Collections::emptyList).stream().forEach(l -> {
            PatrolCheckResult result = new PatrolCheckResult();
            result.setCode(l.getCode());    // 巡检项编号
            result.setContent(l.getContent());  // 巡检项内容
            result.setQualityStandard(l.getQualityStandard());  // 质量标准
            result.setHierarchyType(l.getHierarchyType());  // 层级类型
            result.setParentId(l.getParentId()); //父级ID
            result.setOrder(l.getOrder());  // 内容排序
            result.setCheck(l.getCheck());  // 是否为巡检项目
            result.setInputType(l.getInputType());  // 填写数据类型
            result.setDictCode(l.getDictCode());    // 关联的数据字典
            result.setRegular(l.getRegular());  // 树校验表达式
            resultList.add(result);
        });
        patrolCheckResultMapper.addResultList(resultList);
    }

    // 构建顶级节点
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
}
