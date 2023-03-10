package com.aiurt.boot.task.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.task.entity.PatrolSamplePerson;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolSamplePersonMapper;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.service.IPatrolSamplePersonService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname :  IPatrolSamplePersonServiceImpl
 * @Description : TODO
 * @Date :2023/3/8 17:30
 * @Created by   : sbx
 */

@Service
public class PatrolSamplePersonServiceImpl extends ServiceImpl<PatrolSamplePersonMapper, PatrolSamplePerson> implements IPatrolSamplePersonService {

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;

    @Autowired
    private PatrolSamplePersonMapper patrolSamplePersonMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPatrolSamplePerson(String patrolNumber, String sampleId) {
        PatrolTaskDevice patrolTaskDevice = patrolTaskDeviceMapper.selectOne(new LambdaQueryWrapper<PatrolTaskDevice>()
                .eq(PatrolTaskDevice::getPatrolNumber, patrolNumber)
                .eq(PatrolTaskDevice::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (ObjectUtil.isEmpty(patrolTaskDevice)) {
            throw new AiurtBootException("只有该任务的巡检人才可以填写工单");
        }

        // 删除巡检单号关联的同行人记录
        patrolSamplePersonMapper.delete(new LambdaQueryWrapper<PatrolSamplePerson>()
                .eq(PatrolSamplePerson::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));

        // 更新同行人
        if (StrUtil.isNotEmpty(sampleId)) {
            List<String> userIds = StrUtil.splitTrim(sampleId, ",");
            userIds.forEach(userId -> {
                PatrolSamplePerson patrolSamplePerson = new PatrolSamplePerson();
                patrolSamplePerson.setTaskDeviceCode(patrolTaskDevice.getPatrolNumber());
                patrolSamplePerson.setUserId(userId);
                patrolSamplePerson.setUsername(ObjectUtil.isNotEmpty(sysBaseApi.getUserById(userId)) ? sysBaseApi.getUserById(userId).getRealname() : "");
                patrolSamplePersonMapper.insert(patrolSamplePerson);
            });
        }


    }
}
