package com.aiurt.modules.training.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.training.entity.TrainingPlan;
import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.aiurt.modules.training.entity.TrainingPlanUser;
import com.aiurt.modules.training.mapper.TrainingPlanMapper;
import com.aiurt.modules.training.service.ITrainingPlanFileService;
import com.aiurt.modules.training.service.ITrainingPlanService;
import com.aiurt.modules.training.service.ITrainingPlanUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 培训计划
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Service
public class TrainingPlanServiceImpl extends ServiceImpl<TrainingPlanMapper, TrainingPlan> implements ITrainingPlanService {
    @Autowired
    private TrainingPlanMapper trainingPlanMapper;
    @Autowired
    private ITrainingPlanFileService trainingPlanFileService;
    @Autowired
    private ITrainingPlanUserService trainingPlanUserService;
    @Autowired
    private ISysBaseAPI iSysBaseApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<TrainingPlan> add(TrainingPlan trainingPlan) {
        Result<TrainingPlan> result = new Result<>();
        try {
            //插入主表
            trainingPlanMapper.insert(trainingPlan);
            if (ObjectUtil.isEmpty(trainingPlan.getId())) {
                throw new AiurtBootException("添加培训计划失败!");
            }
            Long planId = trainingPlan.getId();

            //存人员与文件
            saveUserAndFile(trainingPlan, planId);
            return result.success("添加成功!");


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AiurtBootException("操作失败!");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<TrainingPlan> edit(TrainingPlan trainingPlan) {
        Result<TrainingPlan> result = new Result<>();
        TrainingPlan trainingPlanEntity = trainingPlanMapper.selectById(trainingPlan.getId());
        if (ObjectUtil.isEmpty(trainingPlanEntity)) {
            return result.onnull("未找到对应实体");
        } else {
            int updateById = trainingPlanMapper.updateById(trainingPlan);
            this.trainingPlanFileService.remove(new LambdaQueryWrapper<TrainingPlanFile>().eq(TrainingPlanFile::getPlanId, trainingPlan.getId()));
            this.trainingPlanUserService.remove(new LambdaQueryWrapper<TrainingPlanUser>().eq(TrainingPlanUser::getPlanId, trainingPlan.getId()));
            //存人员与文件
            saveUserAndFile(trainingPlan, trainingPlan.getId());
            if (updateById == 1) {
                return result.success("修改成功!");
            } else {
                return result.error500("修改失败!");
            }
        }
    }

    private void saveUserAndFile(TrainingPlan trainingPlan, Long planId) {
        //存人员信息
        List<String> userIds = trainingPlan.getUserIds();
        List<LoginUser> allUsers = iSysBaseApi.queryAllUsers();
        Map<String, String> realNameMap = allUsers.stream().collect(Collectors.toMap(LoginUser::getId, LoginUser::getRealname));
        List<TrainingPlanUser> planUserList = new ArrayList<>();
        for (String userId : userIds) {
            TrainingPlanUser planUser = new TrainingPlanUser();
            planUser.setPlanId(planId)
                    .setUserId(userId)
                    .setRealName(realNameMap.get(userId))
                    .setSignStatus(CommonConstant.STATUS_NORMAL);
            planUserList.add(planUser);
        }
        this.trainingPlanUserService.saveBatch(planUserList);
        //存文件
        List<Long> fileIds = trainingPlan.getFileIds();
        if (CollectionUtils.isNotEmpty(fileIds)) {
            List<TrainingPlanFile> planFileList = new ArrayList<>();
            for (Long fileId : fileIds) {
                TrainingPlanFile planFile = new TrainingPlanFile();
                planFile.setFileId(fileId).setPlanId(planId);
                planFileList.add(planFile);
            }
            this.trainingPlanFileService.saveBatch(planFileList);
        }
    }
}
