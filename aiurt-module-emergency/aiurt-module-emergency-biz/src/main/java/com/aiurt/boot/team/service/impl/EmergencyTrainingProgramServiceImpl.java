package com.aiurt.boot.team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.boot.team.entity.EmergencyTrainingTeam;
import com.aiurt.boot.team.mapper.EmergencyTrainingProgramMapper;
import com.aiurt.boot.team.service.IEmergencyTrainingProgramService;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTrainingProgramServiceImpl extends ServiceImpl<EmergencyTrainingProgramMapper, EmergencyTrainingProgram> implements IEmergencyTrainingProgramService {
    /**
     * 系统管理员角色编码
     */
    private static final String ADMIN = "admin";

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private EmergencyTrainingProgramMapper emergencyTrainingProgramMapper;

    @Autowired
    private EmergencyTrainingTeamServiceImpl emergencyTrainingTeamService;

    @Override
    public IPage<EmergencyTrainingProgram> queryPageList(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO, Integer pageNo, Integer pageSize) {
        // 系统管理员不做权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<EmergencyTrainingProgram> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyTrainingProgram::getDelFlag, TeamConstant.DEL_FLAG0);
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
                queryWrapper.in(EmergencyTrainingProgram::getOrgCode, orgCodes);
            }
        }else {
            return new Page<>();
        }
        Page<EmergencyTrainingProgram> page = new Page<>(pageNo, pageSize);
        EmergencyTrainingProgram trainingProgram = new EmergencyTrainingProgram();
        BeanUtil.copyProperties(trainingProgram,emergencyTrainingProgramDTO);

        Optional.ofNullable(trainingProgram.getOrgCode())
                .ifPresent(code -> queryWrapper.eq(EmergencyTrainingProgram::getOrgCode, code));
        Optional.ofNullable(trainingProgram.getTrainingPlanTime())
                .ifPresent(time -> queryWrapper.eq(EmergencyTrainingProgram::getTrainingPlanTime, time));
        Optional.ofNullable(trainingProgram.getStatus())
                .ifPresent(status -> queryWrapper.eq(EmergencyTrainingProgram::getStatus, status));
        Optional.ofNullable(trainingProgram.getTrainingProgramCode())
                .ifPresent(programCode -> queryWrapper.eq(EmergencyTrainingProgram::getTrainingProgramCode, programCode));
        Optional.ofNullable(trainingProgram.getTrainingProgramName())
                .ifPresent(programName -> queryWrapper.like(EmergencyTrainingProgram::getTrainingProgramName, programName));

        IPage<EmergencyTrainingProgram> pageList = this.page(page, queryWrapper);

        List<EmergencyTrainingProgram> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (EmergencyTrainingProgram record : records) {
                SysDepartModel sysDepartModel = iSysBaseAPI.selectAllById(record.getOrgCode());
                record.setOrgName(sysDepartModel.getDepartName());
                String trainingTeam = emergencyTrainingProgramMapper.getTrainingTeam(record.getId());
                record.setEmergencyTeamName(trainingTeam);
            }
        }
        return pageList;
    }

    @Override
    public Result<String> add(EmergencyTrainingProgram emergencyTrainingProgram) {
        String code = emergencyTrainingProgram.getTrainingProgramCode();
        String trainPlanCode = getTrainPlanCode();
        if (!code.equals(trainPlanCode)) {
            return Result.OK("训练计划编号已存在，添加失败");
        }

        String result = "添加成功！";
        if (TeamConstant.PUBLISH.equals(emergencyTrainingProgram.getSaveFlag())) {
            emergencyTrainingProgram.setStatus(TeamConstant.WAIT_COMPLETE);
            result = "下发成功！";
        } else {
            emergencyTrainingProgram.setStatus(TeamConstant.WAIT_PUBLISH);
        }
        this.save(emergencyTrainingProgram);
        List<EmergencyTrainingTeam> emergencyTrainingTeamList = emergencyTrainingProgram.getEmergencyTrainingTeamList();
        if (CollUtil.isNotEmpty(emergencyTrainingTeamList)) {
            for (EmergencyTrainingTeam emergencyTrainingTeam : emergencyTrainingTeamList) {
                emergencyTrainingTeam.setEmergencyTrainingProgramId(emergencyTrainingProgram.getId());
                emergencyTrainingTeamService.save(emergencyTrainingTeam);
            }
        }
        return Result.OK(result);
    }

    @Override
    public String getTrainPlanCode() {
        String code = "XLJH-" + DateUtil.format(new Date(), "yyyyMMdd-");
        EmergencyTrainingProgram one = this.lambdaQuery().like(EmergencyTrainingProgram::getTrainingProgramCode, code)
                .orderByDesc(EmergencyTrainingProgram::getTrainingProgramCode)
                .last("limit 1")
                .one();

        if (ObjectUtil.isEmpty(one)) {
            code += String.format("%02d", 1);
        } else {
            String trainingProgramCode = one.getTrainingProgramCode();
            Integer serialNo = Integer.valueOf(trainingProgramCode.substring(trainingProgramCode.lastIndexOf("-") + 1));
            if (serialNo >= 99) {
                code += (serialNo + 1);
            } else {
                code += String.format("%02d", (serialNo + 1));
            }
        }

        return code;
    }

    @Override
    public Result<String> edit(EmergencyTrainingProgram emergencyTrainingProgram) {
        this.updateById(emergencyTrainingProgram);
        List<EmergencyTrainingTeam> emergencyTrainingTeamList = emergencyTrainingProgram.getEmergencyTrainingTeamList();
        if (CollUtil.isNotEmpty(emergencyTrainingTeamList)) {
            for (EmergencyTrainingTeam emergencyTrainingTeam : emergencyTrainingTeamList) {
                if (StrUtil.isBlank(emergencyTrainingTeam.getEmergencyTrainingProgramId())) {
                    emergencyTrainingTeam.setEmergencyTrainingProgramId(emergencyTrainingProgram.getId());
                    emergencyTrainingTeamService.save(emergencyTrainingTeam);
                } else {
                    if (TeamConstant.DEL_FLAG1.equals(emergencyTrainingTeam.getDelFlag())) {
                        emergencyTrainingTeamService.removeById(emergencyTrainingTeam);
                    }
                }

            }
        }
        return Result.OK("编辑成功");
    }
}
