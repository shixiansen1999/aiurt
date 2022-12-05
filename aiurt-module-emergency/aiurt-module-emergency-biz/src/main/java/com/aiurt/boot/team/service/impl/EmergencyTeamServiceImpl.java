package com.aiurt.boot.team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.dto.EmergencyTeamTrainingDTO;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.mapper.EmergencyTeamMapper;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
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
            if (!roleCodes.contains(TeamConstant.ADMIN)) {
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
        queryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
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
        emergencyTeam.setMajorName(major != null ? major.getString("majorName") : null);

        SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(emergencyTeam.getOrgCode());
        emergencyTeam.setOrgName(sysDepartModel != null ? sysDepartModel.getDepartName(): null);

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
        emergencyTeam.setManagerName(userById != null ? userById.getRealname(): null);

    }

    @Override
    public EmergencyTeam getCrew(EmergencyTeam emergencyTeam) {
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
        List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyCrews)) {
            List<EmergencyCrewVO> list = new ArrayList<>();
            for (EmergencyCrew emergencyCrew : emergencyCrews) {
                EmergencyCrewVO emergencyCrewVO = new EmergencyCrewVO();
                List<String> roleNamesById = iSysBaseAPI.getRoleNamesById(emergencyCrew.getUserId());
                if (CollUtil.isNotEmpty(roleNamesById)) {
                    String join = StrUtil.join(",", roleNamesById);
                    emergencyCrewVO.setRoleNames(join);
                }
                LoginUser userById = iSysBaseAPI.getUserById(emergencyCrew.getUserId());
                emergencyCrewVO.setRealname(userById.getRealname());
                emergencyCrewVO.setId(emergencyCrew.getId());
                emergencyCrewVO.setPost(emergencyCrew.getPost());
                emergencyCrewVO.setPhone(emergencyCrew.getUserPhone());
                SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(userById.getOrgCode());
                emergencyCrewVO.setOrgName(sysDepartModel.getDepartName());
                emergencyCrewVO.setRemark(emergencyCrew.getRemark());
                list.add(emergencyCrewVO);
            }
            emergencyTeam.setEmergencyCrewVOList(list);
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
                    if (TeamConstant.DEL_FLAG1.equals(emergencyCrew.getDelFlag())) {
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
    public void delete(EmergencyTeam emergencyTeam ) {
        LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
        wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
        List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyCrews)) {
            emergencyCrewService.removeByIds(emergencyCrews);
        }
        this.removeById(emergencyTeam);
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


    @Override
    public Result<List<EmergencyTeam>> getTeamByCode(String orgCode) {
        LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(orgCode)) {
            queryWrapper.eq(EmergencyTeam::getOrgCode, orgCode);
        }
        queryWrapper.select(EmergencyTeam::getId,EmergencyTeam::getEmergencyTeamname, EmergencyTeam::getEmergencyTeamcode,EmergencyTeam::getManagerId);
        List<EmergencyTeam> emergencyTeams = this.getBaseMapper().selectList(queryWrapper);
        if (CollUtil.isNotEmpty(emergencyTeams)) {
            for (EmergencyTeam emergencyTeam : emergencyTeams) {
                LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
                wrapper.eq(EmergencyCrew::getEmergencyTeamId, emergencyTeam.getId());
                List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
                emergencyTeam.setCrews(Convert.toStr(emergencyCrews.size()));

                LoginUser userById = iSysBaseAPI.getUserById(emergencyTeam.getManagerId());
                emergencyTeam.setManagerName(userById.getRealname());
            }
        }

        return Result.OK(emergencyTeams);
    }


}
