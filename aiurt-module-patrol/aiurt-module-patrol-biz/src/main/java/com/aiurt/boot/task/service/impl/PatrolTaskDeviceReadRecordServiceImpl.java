package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.task.entity.PatrolTaskDeviceReadRecord;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceReadRecordMapper;
import com.aiurt.boot.task.service.IPatrolTaskDeviceReadRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/29
 * @desc
 */
@Service
public class PatrolTaskDeviceReadRecordServiceImpl extends ServiceImpl<PatrolTaskDeviceReadRecordMapper, PatrolTaskDeviceReadRecord> implements IPatrolTaskDeviceReadRecordService {
    @Autowired
    private PatrolTaskDeviceReadRecordMapper taskDeviceReadRecordMapper;
    @Override
    public boolean getPatrolTaskDeviceList(String taskDeviceId, String majorCode, String subsystemCode, String taskId) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询是否是第一次阅读安全事项
        List<PatrolTaskDeviceReadRecord> myselfList = taskDeviceReadRecordMapper.getReadList(taskDeviceId,majorCode,subsystemCode,taskId,user.getId());
        //查询当前登录人，在同一个任务，其他单号是否阅读过安全事项
         LambdaQueryWrapper<PatrolTaskDeviceReadRecord> queryWrapper = new LambdaQueryWrapper<>();
         if(ObjectUtil.isNotEmpty(subsystemCode)){
             queryWrapper.eq(PatrolTaskDeviceReadRecord::getSubsystemCode,subsystemCode);
         }
        queryWrapper.eq(PatrolTaskDeviceReadRecord::getTaskId,taskId);
        queryWrapper.eq(PatrolTaskDeviceReadRecord::getMajorCode,majorCode);
        queryWrapper.eq(PatrolTaskDeviceReadRecord::getUserId,user.getId());
         List<PatrolTaskDeviceReadRecord> otherDeviceList = taskDeviceReadRecordMapper.selectList(queryWrapper);
         if(CollUtil.isNotEmpty(myselfList)||CollUtil.isNotEmpty(otherDeviceList)) {
             return true;
         }
         else {
             return false;
         }
    }
}
