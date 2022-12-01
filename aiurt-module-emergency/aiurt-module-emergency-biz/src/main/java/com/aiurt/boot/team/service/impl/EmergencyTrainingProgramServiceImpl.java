package com.aiurt.boot.team.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.boot.team.mapper.EmergencyTrainingProgramMapper;
import com.aiurt.boot.team.service.IEmergencyTrainingProgramService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private EmergencyTrainingTeamServiceImpl emergencyTrainingTeamService;

    @Override
    public IPage<EmergencyTrainingProgram> queryPageList(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO, Integer pageNo, Integer pageSize) {
        // 系统管理员不做权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<EmergencyTrainingProgram> queryWrapper = new LambdaQueryWrapper<>();
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


            }
        }


        return pageList;
    }
}
