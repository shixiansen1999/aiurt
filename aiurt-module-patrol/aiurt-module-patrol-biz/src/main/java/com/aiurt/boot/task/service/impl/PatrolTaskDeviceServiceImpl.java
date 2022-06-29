package com.aiurt.boot.task.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
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
    public Page<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(Page<PatrolTaskDeviceDTO> pageList, String code) {

        List<PatrolTaskDeviceDTO> patrolTaskDeviceList = patrolTaskDeviceMapper.getPatrolTaskDeviceList(pageList, code);
        patrolTaskDeviceList.stream().forEach(e->{
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
    @Override
    public void getPatrolTaskCheck(PatrolTaskDeviceDTO patrolTaskDeviceDTO) {
        //更新任务状态（将未开始改为执行中）、添加检查人id，传任务主键id,巡检工单主键，
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        PatrolTaskDevice patrolPDevice = new PatrolTaskDevice();
        LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTaskDevice::getStatus, 1)
                .set(PatrolTaskDevice::getUserId, sysUser.getId())
                .eq(PatrolTaskDevice::getTaskId, patrolTaskDeviceDTO.getTaskId()).eq(PatrolTaskDevice::getId,patrolPDevice.getId());
        patrolTaskDeviceMapper.update(patrolPDevice, updateWrapper);
        //1.提前写入检查结果,插入巡检任务检查结果表
        //1.1查询这个任务中，这个单号，获取巡检任务标准关联表Id
        LambdaQueryWrapper<PatrolTaskDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolTaskDevice::getTaskId,patrolTaskDeviceDTO.getTaskId()).eq(PatrolTaskDevice::getId,patrolTaskDeviceDTO.getId());
        PatrolTaskDevice patrolTaskDevice = patrolTaskDeviceMapper.selectOne(queryWrapper);
        //1.2根据巡检任务标准关联表ID，获取巡检标准表Id
        LambdaQueryWrapper<PatrolTaskStandard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolTaskStandard::getId,patrolTaskDevice.getTaskStandardId());
        PatrolTaskStandard patrolTaskStandard = patrolTaskStandardMapper.selectOne(wrapper);
        //1.3根据标准表Id,获取这边标准表的所以的检查项
        LambdaQueryWrapper<PatrolStandardItems> itemsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        itemsLambdaQueryWrapper.eq(PatrolStandardItems::getStandardId,patrolTaskStandard.getStandardId());
        List<PatrolStandardItems> standardItemsList = patrolStandardItemsMapper.selectList(itemsLambdaQueryWrapper);
        standardItemsList.stream().forEach(e->{
            PatrolCheckResult patrolCheckResult = new PatrolCheckResult();
            patrolCheckResult.setTaskDeviceId(patrolTaskDevice.getId());
            patrolCheckResult.setTaskStandardId(patrolTaskDevice.getTaskStandardId());
           // patrolCheckResult.setId()
        });

    }
}
