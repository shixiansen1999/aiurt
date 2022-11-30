package com.aiurt.boot.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.mapper.EmergencyTeamMapper;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTeamServiceImpl extends ServiceImpl<EmergencyTeamMapper, EmergencyTeam> implements IEmergencyTeamService {

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private IEmergencyCrewService emergencyCrewService;

    @Override
    public void translate(EmergencyTeam emergencyTeam) {
        JSONObject major = iSysBaseAPI.getCsMajorByCode(emergencyTeam.getMajorCode());
        emergencyTeam.setMajorName(major.getString("majorName"));

        SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(emergencyTeam.getOrgCode());
        emergencyTeam.setMajorName(sysDepartModel.getDepartName());

        String lineName = iSysBaseAPI.getPosition(emergencyTeam.getLineCode());
        emergencyTeam.setLineName(lineName);

        String stationName = iSysBaseAPI.getPosition(emergencyTeam.getStationCode());
        emergencyTeam.setStationName(stationName);

        String positionName = iSysBaseAPI.getPosition(emergencyTeam.getPositionCode());
        emergencyTeam.setPositionName(positionName);

        String workareaCode = emergencyTeam.getWorkareaCode();
        if (StrUtil.isNotBlank(workareaCode)) {
            String workAreaNameByCode = iSysBaseAPI.getWorkAreaNameByCode(workareaCode);
            emergencyTeam.setWorkareaName(workAreaNameByCode);
        }

        LoginUser userById = iSysBaseAPI.getUserById(emergencyTeam.getManagerId());
        emergencyTeam.setManagerName(userById.getRealname());

    }

    @Override
    public EmergencyTeam getCrew(EmergencyTeam emergencyTeam) {
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, 0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
        List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyCrews)) {
            for (EmergencyCrew emergencyCrew : emergencyCrews) {
                List<String> roleNamesById = iSysBaseAPI.getRoleNamesById(emergencyCrew.getUserId());
                if (CollUtil.isNotEmpty(roleNamesById)) {
                    String join = StrUtil.join(",", roleNamesById);
                    emergencyCrew.setRoleName(join);
                }
                LoginUser userById = iSysBaseAPI.getUserById(emergencyCrew.getUserId());
                emergencyCrew.setUserName(userById.getRealname());
            }
            emergencyTeam.setEmergencyCrewList(emergencyCrews);
        }
        translate(emergencyTeam);
        return emergencyTeam;
    }

    @Override
    public Result<String> add(EmergencyTeam emergencyTeam) {
        save(emergencyTeam);
        List<EmergencyCrew> emergencyCrewList = emergencyTeam.getEmergencyCrewList();
        if (CollUtil.isNotEmpty(emergencyCrewList)) {
            for (EmergencyCrew emergencyCrew : emergencyCrewList) {
                emergencyCrew.setEmergencyTeamId(emergencyTeam.getId());
                emergencyCrewService.save(emergencyCrew);
            }
        }
        return Result.OK("添加成功!");
    }

    @Override
    public Result<String> edit(EmergencyTeam emergencyTeam) {
        updateById(emergencyTeam);
        List<EmergencyCrew> emergencyCrewList = emergencyTeam.getEmergencyCrewList();
        if (CollUtil.isNotEmpty(emergencyCrewList)) {
            for (EmergencyCrew emergencyCrew : emergencyCrewList) {
                if (StrUtil.isBlank(emergencyCrew.getEmergencyTeamId())) {
                    emergencyCrew.setEmergencyTeamId(emergencyTeam.getId());
                    emergencyCrewService.save(emergencyCrew);
                } else {
                    Integer delFlag = 1;
                    if (delFlag.equals(emergencyCrew.getDelFlag())) {
                        emergencyCrewService.removeById(emergencyCrew);
                    } else {
                        emergencyCrewService.updateById(emergencyCrew);
                    }
                }
            }
        }
        return Result.OK("编辑成功!");
    }

    @Override
    public Result<String> delete(String id) {
        EmergencyTeam emergencyTeam = this.getById(id);
        if(emergencyTeam==null) {
            return Result.error("未找到对应数据");
        }
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, 0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
        List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyCrews)) {
            emergencyCrewService.removeByIds(emergencyCrews);
        }
        this.removeById(emergencyTeam);
        return Result.OK("删除成功!");
    }

    @Override
    public Result<EmergencyTeam> getTrainingRecordById(String id) {
        EmergencyTeam emergencyTeam = this.getById(id);
        if(emergencyTeam==null) {
            return Result.error("未找到对应数据");
        }


        return Result.OK();
    }
}
