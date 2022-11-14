package com.aiurt.boot.weeklyplan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.weeklyplan.entity.BdOverhaulDeviceReport;
import com.aiurt.boot.weeklyplan.entity.BdOverhaulReport;
import com.aiurt.boot.weeklyplan.mapper.BdOverhaulDeviceReportMapper;
import com.aiurt.boot.weeklyplan.mapper.BdOverhaulReportMapper;
import com.aiurt.boot.weeklyplan.mapper.BdSparesListMapper;
import com.aiurt.boot.weeklyplan.service.IBdOverhaulReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: bd_overhaul_report
 * @Author: jeecg-boot
 * @Date: 2021-05-17
 * @Version: V1.0
 */
@Slf4j
@Service
public class BdOverhaulReportServiceImpl extends ServiceImpl<BdOverhaulReportMapper, BdOverhaulReport> implements IBdOverhaulReportService {
    @Autowired
    private BdOverhaulDeviceReportMapper bdOverhaulDeviceReportMapper;
    @Autowired
    private BdSparesListMapper bdSparesListMapper;

    /**
     * 根据计划令code撤销检修任务
     *
     * @param id 计划令id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> cancelTask(String id) {
        if (StrUtil.isEmpty(id)) {
            return Result.error("无相关数据");
        }
        // 查询对应的检修任务
        LambdaQueryWrapper<BdOverhaulReport> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BdOverhaulReport::getOperatePlanDeclarationFormId, id);
        List<BdOverhaulReport> bdOverhaulReportList = baseMapper.selectList(lambdaQueryWrapper);

        if (CollUtil.isNotEmpty(bdOverhaulReportList)) {
            List<Integer> taskIds = bdOverhaulReportList.stream().map(BdOverhaulReport::getId).collect(Collectors.toList());

            if (CollUtil.isNotEmpty(taskIds)) {
                List<BdOverhaulDeviceReport> bdOverhaulDeviceReports = bdOverhaulDeviceReportMapper.selectList(new LambdaQueryWrapper<BdOverhaulDeviceReport>().in(BdOverhaulDeviceReport::getOverhaulReportId, taskIds));
                if (CollUtil.isNotEmpty(bdOverhaulDeviceReports)) {

                    // 如果存在未完成的任务消息，则直接将消息状态变成已读
                    List<Integer> deviceReports = bdOverhaulDeviceReports.stream().map(BdOverhaulDeviceReport::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(deviceReports)) {
                        for (Integer deviceReport : deviceReports) {
                            // 将签字任务的消息改为已完成
                            int messageType = 24;
                            String antId = bdSparesListMapper.getMessage(String.valueOf(deviceReport), messageType);
                            if (StrUtil.isNotEmpty(antId)) {
                                // 更新消息发送表
                                bdSparesListMapper.updateMessage(antId);
                            }
                        }
                    }
                }

                // 将检修任务消息的状态改成已完成
                for (Integer taskId : taskIds) {
                    int messageType = 23;
                    String antId = bdSparesListMapper.getMessage(String.valueOf(taskId), messageType);
                    if (StrUtil.isNotEmpty(antId)) {
                        bdSparesListMapper.updateMessage(antId);
                    }
                }
                // 更改任务状态
                for (BdOverhaulReport bdOverhaulReport : bdOverhaulReportList) {
                    bdOverhaulReport.setFormState(4);
                    baseMapper.updateById(bdOverhaulReport);
                }

                return Result.OK("撤销成功");
            }
        }
        return Result.OK("无相关数据");
    }

}
