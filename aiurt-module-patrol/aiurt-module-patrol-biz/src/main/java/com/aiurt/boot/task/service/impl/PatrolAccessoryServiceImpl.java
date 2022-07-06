package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.task.dto.PatrolAccessoryDTO;
import com.aiurt.boot.task.dto.PatrolAccessorySaveDTO;
import com.aiurt.boot.task.entity.PatrolAccessory;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.aiurt.boot.task.mapper.PatrolAccessoryMapper;
import com.aiurt.boot.task.mapper.PatrolCheckResultMapper;
import com.aiurt.boot.task.service.IPatrolAccessoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: patrol_accessory
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolAccessoryServiceImpl extends ServiceImpl<PatrolAccessoryMapper, PatrolAccessory> implements IPatrolAccessoryService {
@Autowired
private  PatrolAccessoryMapper patrolAccessoryMapper;
@Autowired
private PatrolCheckResultMapper patrolCheckResultMapper;
    @Override
    public void savePatrolTaskAccessory(PatrolAccessorySaveDTO patrolAccessory) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<PatrolCheckResult> updateWrapper= new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolCheckResult::getUserId,sysUser.getId()).set(PatrolCheckResult::getDelFlag,0).eq(PatrolCheckResult::getId,patrolAccessory.getId());
        PatrolCheckResult result = new PatrolCheckResult();
        patrolCheckResultMapper.update(result,updateWrapper);
        LambdaQueryWrapper<PatrolAccessory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolAccessory::getCheckResultId,patrolAccessory.getId()).eq(PatrolAccessory::getTaskDeviceId,patrolAccessory.getTaskDeviceId());
        List<PatrolAccessory> list = patrolAccessoryMapper.selectList(queryWrapper);
        if(CollUtil.isNotEmpty(list))
        {
            patrolAccessoryMapper.deleteBatchIds(list);
        }
            List<PatrolAccessoryDTO> patrolAccessoryDTOList = patrolAccessory.getPatrolAccessoryDTOList();
            if(CollUtil.isNotEmpty(patrolAccessoryDTOList))
            {
                patrolAccessoryDTOList.stream().forEach(e->{
                    PatrolAccessory accessory = new PatrolAccessory();
                    accessory.setDelFlag(0);
                    accessory.setName(e.getName());
                    accessory.setAddress(e.getAddress());
                    accessory.setTaskDeviceId(patrolAccessory.getTaskDeviceId());
                    accessory.setCheckResultId(patrolAccessory.getId());
                    patrolAccessoryMapper.insert(accessory);
                });
            }
    }
}
