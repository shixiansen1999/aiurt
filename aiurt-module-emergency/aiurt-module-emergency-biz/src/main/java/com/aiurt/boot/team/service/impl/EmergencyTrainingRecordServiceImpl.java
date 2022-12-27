package com.aiurt.boot.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.*;
import com.aiurt.boot.team.listener.RecordExcelListener;
import com.aiurt.boot.team.mapper.EmergencyTrainingProgramMapper;
import com.aiurt.boot.team.mapper.EmergencyTrainingRecordMapper;
import com.aiurt.boot.team.model.RecordModel;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.aiurt.common.util.TimeUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private EmergencyTeamServiceImpl emergencyTeamService;
    @Autowired
    private IEmergencyCrewService emergencyCrewService;

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

    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        List<String> errorMessage = new ArrayList<>();
        int successLines = 0;
        // 错误信息
        int  errorLines = 0;

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return iSysBaseAPI.importReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            RecordExcelListener recordExcelListener = new RecordExcelListener();
            try {
                EasyExcel.read(file.getInputStream(), RecordData.class, recordExcelListener).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RecordModel recordModel = recordExcelListener.getRecordModel();
            errorLines = checkTeam(recordModel, errorLines);



        }
        return Result.ok();
    }

    private int checkTeam(RecordModel recordModel, int  errorLines ) {

        String trainingTime = recordModel.getTrainingTime();
        String position = recordModel.getPosition();
        String emergencyTeam = recordModel.getEmergencyTeam();
        String trainees = recordModel.getTrainees();
        String emergencyTrainingProgram = recordModel.getEmergencyTrainingProgram();
        String trainingProgramCode = recordModel.getTrainingProgramCode();
        String trainingAppraise = recordModel.getTrainingAppraise();

        StringBuilder stringBuilder = new StringBuilder();
        String trainingProgramName = null;
        Date time = null;
        if (StrUtil.isNotEmpty(trainingProgramCode)) {
            LambdaQueryWrapper<EmergencyTrainingProgram> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyTrainingProgram::getTrainingProgramCode, trainingProgramCode).eq(EmergencyTrainingProgram::getDelFlag, 0);
            EmergencyTrainingProgram one = emergencyTrainingProgramService.getOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(one)) {
                recordModel.setEmergencyTrainingProgramId(one.getId());
                trainingProgramName = one.getTrainingProgramName();
                time = one.getTrainingPlanTime();
            } else {
                stringBuilder.append("该训练计划不存在，");
            }
        } else {
            stringBuilder.append("训练计划编码不能为空，");
        }

        if (StrUtil.isNotEmpty(emergencyTrainingProgram)) {
            if (!emergencyTrainingProgram.equals(trainingProgramName)) {
                stringBuilder.append("训练科目和训练计划编码不相符，");
            }
        } else {
            stringBuilder.append("训练科目不能为空，");
        }

        if (StrUtil.isNotEmpty(trainingTime)) {
            boolean legalDate = TimeUtil.isLegalDate(trainingTime.length(), trainingTime, "yyyy-MM-dd");
            if (!legalDate) {
                stringBuilder.append("训练时间格式不对，");
            }
            if (time != null) {
                DateTime parse = DateUtil.parse(trainingTime, "yyyy-MM-dd");
                if (parse.isBefore(time)) {
                    stringBuilder.append("训练时间不能比计划时间早，");
                }
            }
        } else {
            stringBuilder.append("训练时间不能为空，");
        }

        if (StrUtil.isNotEmpty(position)) {
            List<String> list = StrUtil.splitTrim(position, "/");
            if (list.size() > 3) {
                stringBuilder.append("训练地点格式不对，");
            }
            String lineCode = null;
            String stationCode = null;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    JSONObject lineByName = iSysBaseAPI.getLineByName(list.get(0));
                    if (ObjectUtil.isEmpty(lineByName)) {
                        stringBuilder.append("训练地点中线路不存在，");
                    } else {
                        lineCode = lineByName.getString("lineCode");
                    }
                }
                if (i == 1) {
                    JSONObject stationByName = iSysBaseAPI.getStationByName(list.get(1));
                    if (ObjectUtil.isEmpty(stationByName)) {
                        stringBuilder.append("训练地点中站点不存在，");
                    } else {
                        stationCode = stationByName.getString("stationCode");
                    }
                }
                if (i == 2) {
                    JSONObject positionByName = iSysBaseAPI.getPositionByName(list.get(2),lineCode,stationCode);
                    if (ObjectUtil.isEmpty(positionByName)) {
                        stringBuilder.append("训练地点中线路不存在，");
                    }
                }
            }
        } else {
            stringBuilder.append("训练地点不能为空，");
        }

        if (StrUtil.isNotEmpty(emergencyTeam)) {
            LambdaQueryWrapper<EmergencyTeam> teamQueryWrapper = new LambdaQueryWrapper<>();
            teamQueryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
            teamQueryWrapper.eq(EmergencyTeam::getEmergencyTeamname, emergencyTeam);
            EmergencyTeam one = emergencyTeamService.getOne(teamQueryWrapper);
            if (ObjectUtil.isNotEmpty(one)) {
                recordModel.setEmergencyTeamId(one.getId());
                if (StrUtil.isNotEmpty(trainees)) {
                    LambdaQueryWrapper<EmergencyCrew> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(EmergencyCrew::getDelFlag, TeamConstant.DEL_FLAG0);
                    wrapper.eq(EmergencyCrew::getEmergencyTeamId, one.getId());
                    List<EmergencyCrew> emergencyCrews = emergencyCrewService.getBaseMapper().selectList(wrapper);
                    List<String> realNames = new ArrayList<>();
                    for (EmergencyCrew emergencyCrew : emergencyCrews) {
                        LoginUser userById = iSysBaseAPI.getUserById(emergencyCrew.getUserId());
                        realNames.add(userById.getRealname());
                    }
                    List<String> list = StrUtil.splitTrim(trainees, ",");
                    for (String s : list) {
                        if (!realNames.contains(s)) {
                            stringBuilder.append("应急队伍不存在" + s+"该人员,");
                        }
                    }
                }
            } else {
                stringBuilder.append("应急队伍不存在，");
            }
        } else {
            stringBuilder.append("应急队伍名称不能为空，");
        }

        if (StrUtil.isEmpty(trainees)) {
            stringBuilder.append("参加训练人员不能为空，");
        }

        if (StrUtil.isEmpty(trainingAppraise)) {
            stringBuilder.append("训练效果及建议不能为空，");
        }

        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            recordModel.setMistake(stringBuilder.toString());
            errorLines++;
        }
        return errorLines;
    }
}
