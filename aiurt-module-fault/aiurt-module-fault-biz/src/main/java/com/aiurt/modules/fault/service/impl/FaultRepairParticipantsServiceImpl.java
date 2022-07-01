package com.aiurt.modules.fault.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.aiurt.modules.fault.mapper.FaultRepairParticipantsMapper;
import com.aiurt.modules.fault.service.IFaultRepairParticipantsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 故障参与人
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Service
public class FaultRepairParticipantsServiceImpl extends ServiceImpl<FaultRepairParticipantsMapper, FaultRepairParticipants> implements IFaultRepairParticipantsService {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     * 根据维修记录查询参与人员
     * @param recordId 维修解落id
     * @return
     */
    @Override
    public List<FaultRepairParticipants> queryParticipantsByRecordId(String recordId) {
        // 查询参与人
        LambdaQueryWrapper<FaultRepairParticipants> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaultRepairParticipants::getFaultRepairRecordId, recordId);

        List<FaultRepairParticipants> participantsList = baseMapper.selectList(queryWrapper);

        if (CollectionUtil.isEmpty(participantsList)) {
            return Collections.emptyList();
        }

        String[] userNameArr = participantsList.stream().map(FaultRepairParticipants::getUserName).toArray(String[]::new);

        List<LoginUser> loginUserList = sysBaseAPI.queryUserByNames(userNameArr);
        if (CollectionUtil.isEmpty(loginUserList)) {
            return participantsList;
        }

        Map<String, String> userNameMap = loginUserList.stream().collect(Collectors.toMap(LoginUser::getUsername, LoginUser::getRealname, (t1, t2) -> t2));
        Map<String, String> userIdMap = loginUserList.stream().collect(Collectors.toMap(LoginUser::getUsername, LoginUser::getId, (t1, t2) -> t2));

        participantsList.stream().forEach(par->{
            par.setRealName(userNameMap.getOrDefault(par.getUserName(), par.getUserName()));
            par.setUserId(userIdMap.get(par.getUserName()));
        });
        return participantsList;
    }
}
