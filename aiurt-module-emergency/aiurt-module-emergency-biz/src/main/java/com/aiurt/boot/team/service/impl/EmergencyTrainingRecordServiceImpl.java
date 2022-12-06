package com.aiurt.boot.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.*;
import com.aiurt.boot.team.mapper.EmergencyTrainingRecordMapper;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTrainingRecordServiceImpl extends ServiceImpl<EmergencyTrainingRecordMapper, EmergencyTrainingRecord> implements IEmergencyTrainingRecordService {

    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private EmergencyTrainingRecordMapper emergencyTrainingRecordMapper;
    @Autowired
    private EmergencyTrainingRecordAttServiceImpl emergencyTrainingRecordAttService;
    @Autowired
    private EmergencyTrainingProcessRecordServiceImpl emergencyTrainingProcessRecordService;
    @Autowired
    private EmergencyTrainingRecordCrewServiceImpl emergencyTrainingRecordCrewService;
    @Autowired
    private EmergencyTrainingTeamServiceImpl emergencyTrainingTeamService;
    @Autowired
    private EmergencyTrainingProgramServiceImpl emergencyTrainingProgramService;

    @Override
    public IPage<EmergencyTrainingRecordVO> queryPageList(EmergencyTrainingRecordDTO emergencyTrainingRecordDTO, Integer pageNo, Integer pageSize) {
        // 系统管理员不做权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = user.getRoleCodes();
        if (StrUtil.isNotBlank(roleCodes)) {
            if (!roleCodes.contains(TeamConstant.ADMIN)) {
                //获取用户的专业权限
                List<CsUserMajorModel> majorByUserId = iSysBaseAPI.getMajorByUserId(user.getId());
                if (CollUtil.isEmpty(majorByUserId)) {
                    return new Page<>();
                }
                List<String> majorCodes  = majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
                emergencyTrainingRecordDTO.setMajorCodeList(majorCodes);
            }
        }else {
            return new Page<>();
        }
        Page<EmergencyTrainingRecordVO> page = new Page<>(pageNo, pageSize);
        List<EmergencyTrainingRecordVO> result = emergencyTrainingRecordMapper.queryPageList(page, emergencyTrainingRecordDTO);
        page.setRecords(result);
        return page;
    }

    @Override
    public Result<EmergencyTrainingRecordVO> queryById(String id) {
        EmergencyTrainingRecordVO emergencyTrainingRecordVO = emergencyTrainingRecordMapper.queryById(id);
        List<EmergencyCrewVO> trainingCrews = emergencyTrainingRecordMapper.getTrainingCrews(id);
        if (CollUtil.isNotEmpty(trainingCrews)) {
            for (EmergencyCrewVO trainingCrew : trainingCrews) {
                List<String> roleNamesById = iSysBaseAPI.getRoleNamesById(trainingCrew.getUserId());
                if (CollUtil.isNotEmpty(roleNamesById)) {
                    String join = StrUtil.join(",", roleNamesById);
                    trainingCrew.setRoleNames(join);
                }
            }
        }
        emergencyTrainingRecordVO.setTrainingCrews(trainingCrews);
        LambdaQueryWrapper<EmergencyTrainingRecordAtt> attQueryWrapper = new LambdaQueryWrapper<>();
        attQueryWrapper.eq(EmergencyTrainingRecordAtt::getDelFlag, TeamConstant.DEL_FLAG0);
        attQueryWrapper.eq(EmergencyTrainingRecordAtt::getEmergencyTrainingRecordId, emergencyTrainingRecordVO.getId());
        List<EmergencyTrainingRecordAtt> recordAtts = emergencyTrainingRecordAttService.getBaseMapper().selectList(attQueryWrapper);
        emergencyTrainingRecordVO.setRecordAtts(recordAtts);
        LambdaQueryWrapper<EmergencyTrainingProcessRecord> processRecordQueryWrapper = new LambdaQueryWrapper<>();
        processRecordQueryWrapper.eq(EmergencyTrainingProcessRecord::getDelFlag, TeamConstant.DEL_FLAG0);
        processRecordQueryWrapper.eq(EmergencyTrainingProcessRecord::getEmergencyTrainingRecordId, emergencyTrainingRecordVO.getId());
        List<EmergencyTrainingProcessRecord> processRecords = emergencyTrainingProcessRecordService.getBaseMapper().selectList(processRecordQueryWrapper);
        emergencyTrainingRecordVO.setProcessRecords(processRecords);
        return Result.OK(emergencyTrainingRecordVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(EmergencyTrainingRecord emergencyTrainingRecord) {
        this.save(emergencyTrainingRecord);
        String id = emergencyTrainingRecord.getId();
        List<EmergencyTrainingRecordCrew> crewList = emergencyTrainingRecord.getCrewList();
        for (EmergencyTrainingRecordCrew emergencyTrainingRecordCrew : crewList) {
            emergencyTrainingRecordCrew.setEmergencyTrainingRecordId(id);
            emergencyTrainingRecordCrewService.save(emergencyTrainingRecordCrew);
        }
        List<EmergencyTrainingProcessRecord> processRecordList = emergencyTrainingRecord.getProcessRecordList();
        for (EmergencyTrainingProcessRecord emergencyTrainingProcessRecord : processRecordList) {
            emergencyTrainingProcessRecord.setEmergencyTrainingRecordId(id);
            emergencyTrainingProcessRecordService.save(emergencyTrainingProcessRecord);
        }
        List<EmergencyTrainingRecordAtt> attList = emergencyTrainingRecord.getAttList();
        for (EmergencyTrainingRecordAtt emergencyTrainingRecordAtt : attList) {
            emergencyTrainingRecordAtt.setEmergencyTrainingRecordId(id);
            emergencyTrainingRecordAttService.save(emergencyTrainingRecordAtt);
        }
        return Result.OK("添加成功！");
    }

    private void submit(EmergencyTrainingRecord emergencyTrainingRecord) {
        Integer status = emergencyTrainingRecord.getStatus();
        if (TeamConstant.SUBMITTED.equals(status)) {
            LambdaQueryWrapper<EmergencyTrainingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.eq(EmergencyTrainingRecord::getDelFlag, TeamConstant.DEL_FLAG0);
            recordQueryWrapper.eq(EmergencyTrainingRecord::getEmergencyTrainingProgramId, emergencyTrainingRecord.getEmergencyTrainingProgramId());
            List<EmergencyTrainingRecord> emergencyTrainingRecords = this.getBaseMapper().selectList(recordQueryWrapper);

            LambdaQueryWrapper<EmergencyTrainingTeam> teamQueryWrapper = new LambdaQueryWrapper<>();
            teamQueryWrapper.eq(EmergencyTrainingTeam::getDelFlag, TeamConstant.DEL_FLAG0);
            teamQueryWrapper.eq(EmergencyTrainingTeam::getEmergencyTrainingProgramId, emergencyTrainingRecord.getEmergencyTrainingProgramId());
            List<EmergencyTrainingTeam> emergencyTrainingTeams = emergencyTrainingTeamService.getBaseMapper().selectList(teamQueryWrapper);
            //当计划中的所有训练队伍都提交了记录的时候修改计划状态
            if (emergencyTrainingRecords.size() == emergencyTrainingTeams.size()) {
                emergencyTrainingProgramService.getById(emergencyTrainingRecord.getEmergencyTrainingProgramId());
            }
        }

    }
}
