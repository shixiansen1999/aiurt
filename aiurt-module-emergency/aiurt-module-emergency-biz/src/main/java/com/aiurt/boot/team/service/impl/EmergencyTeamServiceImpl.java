package com.aiurt.boot.team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.dto.EmergencyTeamTrainingDTO;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.mapper.EmergencyTeamMapper;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTeamServiceImpl extends ServiceImpl<EmergencyTeamMapper, EmergencyTeam> implements IEmergencyTeamService {
    /**
     * 系统管理员角色编码
     */
    private static final String ADMIN = "admin";

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private IEmergencyCrewService emergencyCrewService;

    @Autowired
    private EmergencyTeamMapper emergencyTeamMapper;

    @Override
    public IPage<EmergencyTeam> queryPageList(EmergencyTeamDTO emergencyTeamDTO, Integer pageNo, Integer pageSize) {
        EmergencyTeam team = new EmergencyTeam();
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        BeanUtil.copyProperties(emergencyTeamDTO, team);
        // 系统管理员不做权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = user.getRoleCodes();
        List<SysDepartModel> models = new ArrayList<>();
        if (StrUtil.isNotBlank(roleCodes)) {
            if (!roleCodes.contains(ADMIN)) {
                //获取用户的所属部门及所属部门子部门
                models = iSysBaseAPI.getUserDepartCodes();
                if (CollUtil.isEmpty(models)) {
                    return new Page<>();
                }
                List<String> orgCodes = models.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
                queryWrapper.in(EmergencyTeam::getOrgCode, orgCodes);
            }
        }else {
            return new Page<>();
        }

        if (StrUtil.isNotBlank(team.getMajorCode())) {
            queryWrapper.eq(EmergencyTeam::getMajorCode, team.getMajorCode());
        }
        if (StrUtil.isNotBlank(team.getEmergencyTeamname())) {
            queryWrapper.like(EmergencyTeam::getEmergencyTeamname, team.getEmergencyTeamname());
        }
        queryWrapper.eq(EmergencyTeam::getDelFlag, 0);
        Page<EmergencyTeam> page = new Page<EmergencyTeam>(pageNo, pageSize);
        IPage<EmergencyTeam> pageList = this.page(page, queryWrapper);
        List<EmergencyTeam> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (EmergencyTeam record : records) {
                this.translate(record);
            }
        }
        return pageList;
    }


    @Override
    public void translate(EmergencyTeam emergencyTeam) {
        JSONObject major = iSysBaseAPI.getCsMajorByCode(emergencyTeam.getMajorCode());
        emergencyTeam.setMajorName(major.getString("majorName"));

        SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(emergencyTeam.getOrgCode());
        emergencyTeam.setOrgName(sysDepartModel.getDepartName());

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
        List<EmergencyTeamTrainingDTO> trainingRecord = emergencyTeamMapper.getTrainingRecord(id);
        translate(emergencyTeam);
        if (CollUtil.isNotEmpty(trainingRecord)) {
            for (EmergencyTeamTrainingDTO emergencyTeamDTO : trainingRecord) {
                emergencyTeamDTO.setManagerName(emergencyTeam.getManagerName());
            }
        }
        return Result.OK();
    }

}
