package com.aiurt.boot.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.*;
import com.aiurt.boot.team.mapper.EmergencyTrainingProgramMapper;
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
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Autowired
    private EmergencyTrainingProgramMapper emergencyTrainingProgramMapper;

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
        boolean empty = StrUtil.isEmpty(emergencyTrainingRecord.getLineCode());
        boolean empty1 = StrUtil.isEmpty(emergencyTrainingRecord.getStationCode());
        boolean empty2 = StrUtil.isEmpty(emergencyTrainingRecord.getPositionCode());
        if (empty && empty1 && empty2) {
            return Result.error("添加失败，训练地点不能为空");
        }
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
            String size = emergencyTrainingRecordAtt.getSize();
            String type = emergencyTrainingRecordAtt.getType();
            BigDecimal bigDecimal = new BigDecimal(size);
            BigDecimal divide = bigDecimal.divide(new BigDecimal(1048576));
            int i = divide.compareTo(new BigDecimal(20));
            if (i > 0 || !"doc/docx/xls/xlsx/ppt/pptx/jpeg/pdf/zip/rar".contains(type)) {
                return Result.error("文件大小超过限制或者文件格式不对");
            }
            emergencyTrainingRecordAttService.save(emergencyTrainingRecordAtt);
        }
        if (TeamConstant.SUBMITTED.equals(emergencyTrainingRecord.getStatus())) {
            //如果是提交，判断是否所有内容填写完整
            if (CollUtil.isEmpty(emergencyTrainingRecord.getCrewList()) || CollUtil.isEmpty(emergencyTrainingRecord.getProcessRecordList()) ||CollUtil.isEmpty(emergencyTrainingRecord.getAttList())) {
                return Result.error("还有内容没有填写，不能提交");
            }
            submit(emergencyTrainingRecord);
            return Result.OK("提交成功");
        }
        return Result.OK("添加成功！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(EmergencyTrainingRecord emergencyTrainingRecord) {
        EmergencyTrainingRecord byId = this.getById(emergencyTrainingRecord.getId());
        if (ObjectUtil.isEmpty(byId)) {
            return Result.error("未找到对应数据！");
        }
        if (TeamConstant.SUBMITTED.equals(byId.getStatus())) {
            return Result.error("当前记录已提交，不可编辑");
        }
        this.updateById(emergencyTrainingRecord);
        String id = emergencyTrainingRecord.getId();
        List<EmergencyTrainingRecordCrew> crewList = emergencyTrainingRecord.getCrewList();
        if (CollUtil.isNotEmpty(crewList)) {
            LambdaQueryWrapper<EmergencyTrainingRecordCrew> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyTrainingRecordCrew::getEmergencyTrainingRecordId, emergencyTrainingRecord.getId());
            emergencyTrainingRecordCrewService.getBaseMapper().delete(queryWrapper);
            for (EmergencyTrainingRecordCrew emergencyTrainingRecordCrew : crewList) {
                emergencyTrainingRecordCrew.setEmergencyTrainingRecordId(id);
                emergencyTrainingRecordCrewService.save(emergencyTrainingRecordCrew);
            }
        }

        List<EmergencyTrainingProcessRecord> processRecordList = emergencyTrainingRecord.getProcessRecordList();
        if (CollUtil.isNotEmpty(processRecordList)) {
            LambdaQueryWrapper<EmergencyTrainingProcessRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyTrainingProcessRecord::getEmergencyTrainingRecordId, emergencyTrainingRecord.getId());
            emergencyTrainingProcessRecordService.getBaseMapper().delete(queryWrapper);
            for (EmergencyTrainingProcessRecord emergencyTrainingProcessRecord : processRecordList) {
                emergencyTrainingProcessRecord.setEmergencyTrainingRecordId(id);
                emergencyTrainingProcessRecordService.save(emergencyTrainingProcessRecord);
            }
        }

        List<EmergencyTrainingRecordAtt> attList = emergencyTrainingRecord.getAttList();
        if (CollUtil.isNotEmpty(attList)) {

            LambdaQueryWrapper<EmergencyTrainingRecordAtt> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyTrainingRecordAtt::getEmergencyTrainingRecordId, emergencyTrainingRecord.getId());
            emergencyTrainingRecordAttService.getBaseMapper().delete(queryWrapper);

            for (EmergencyTrainingRecordAtt emergencyTrainingRecordAtt : attList) {
                emergencyTrainingRecordAtt.setEmergencyTrainingRecordId(id);
                String size = emergencyTrainingRecordAtt.getSize();
                String type = emergencyTrainingRecordAtt.getType();
                BigDecimal bigDecimal = new BigDecimal(size);
                BigDecimal divide = bigDecimal.divide(new BigDecimal(1048576));
                int i = divide.compareTo(new BigDecimal(20));
                if (i > 0 || !"doc/docx/xls/xlsx/ppt/pptx/jpeg/pdf/zip/rar".contains(type)) {
                    return Result.error("文件大小超过限制或者文件格式不对");
                }
                emergencyTrainingRecordAttService.save(emergencyTrainingRecordAtt);
            }
        }

        if (TeamConstant.SUBMITTED.equals(byId.getStatus())) {
            //如果是提交，判断是否所有内容填写完整
            if (CollUtil.isEmpty(emergencyTrainingRecord.getCrewList()) || CollUtil.isEmpty(emergencyTrainingRecord.getProcessRecordList()) ||CollUtil.isEmpty(emergencyTrainingRecord.getAttList())) {
                return Result.error("还有内容没有填写，不能提交");
            }
            submit(byId);
            return Result.OK("提交成功");
        }
        return Result.OK("编辑成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(EmergencyTrainingRecord emergencyTrainingRecord) {
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
            EmergencyTrainingProgram program = emergencyTrainingProgramService.getById(emergencyTrainingRecord.getEmergencyTrainingProgramId());
            program.setStatus(TeamConstant.COMPLETED);
            emergencyTrainingProgramService.updateById(program);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        LambdaQueryWrapper<EmergencyTrainingRecordCrew> crewQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<EmergencyTrainingProcessRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<EmergencyTrainingRecordAtt> attQueryWrapper = new LambdaQueryWrapper<>();
        crewQueryWrapper.eq(EmergencyTrainingRecordCrew::getEmergencyTrainingRecordId, id);
        recordQueryWrapper.eq(EmergencyTrainingProcessRecord::getEmergencyTrainingRecordId, id);
        attQueryWrapper.eq(EmergencyTrainingRecordAtt::getEmergencyTrainingRecordId, id);
        emergencyTrainingRecordCrewService.getBaseMapper().delete(crewQueryWrapper);
        emergencyTrainingProcessRecordService.getBaseMapper().delete(recordQueryWrapper);
        emergencyTrainingRecordAttService.getBaseMapper().delete(attQueryWrapper);
        this.removeById(id);
    }

    @Override
    public IPage<EmergencyTrainingProgram> getTrainingProgram(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO, Integer pageNo, Integer pageSize) {
        Page<EmergencyTrainingProgram> page = new Page<>(pageNo, pageSize);
        List<EmergencyTrainingProgram> trainingProgram = emergencyTrainingProgramMapper.getTrainingProgram(page, emergencyTrainingProgramDTO);
        if (CollUtil.isNotEmpty(trainingProgram)) {
            for (EmergencyTrainingProgram record : trainingProgram) {
                SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(record.getOrgCode());
                record.setOrgName(sysDepartModel.getDepartName());
                List<EmergencyTrainingTeam> trainingTeam = emergencyTrainingProgramMapper.getTrainingTeam(record.getId());
                record.setEmergencyTrainingTeamList(trainingTeam);
                List<String> names = trainingTeam.stream().map(EmergencyTrainingTeam::getEmergencyTeamName).collect(Collectors.toList());
                record.setEmergencyTeamName(CollUtil.join(names, ","));
            }
        }
        return page.setRecords(trainingProgram);
    }
}
