package com.aiurt.boot.team.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.team.constants.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.boot.team.entity.EmergencyTrainingTeam;
import com.aiurt.boot.team.mapper.EmergencyTrainingProgramMapper;
import com.aiurt.boot.team.model.TrainingProgramModel;
import com.aiurt.boot.team.service.IEmergencyTrainingProgramService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.TimeUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTrainingProgramServiceImpl extends ServiceImpl<EmergencyTrainingProgramMapper, EmergencyTrainingProgram> implements IEmergencyTrainingProgramService {
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Autowired
    private EmergencyTrainingProgramMapper emergencyTrainingProgramMapper;

    @Autowired
    private EmergencyTrainingTeamServiceImpl emergencyTrainingTeamService;

    @Autowired
    private EmergencyTeamServiceImpl emergencyTeamService;
    @Autowired
    private ISysParamAPI iSysParamAPI;

    @Override
    public IPage<EmergencyTrainingProgram> queryPageList(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<EmergencyTrainingProgram> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyTrainingProgram::getDelFlag, TeamConstant.DEL_FLAG0);

        Page<EmergencyTrainingProgram> page = new Page<>(pageNo, pageSize);
        EmergencyTrainingProgram trainingProgram = new EmergencyTrainingProgram();
        BeanUtil.copyProperties(emergencyTrainingProgramDTO,trainingProgram);

        Optional.ofNullable(trainingProgram.getOrgCode())
                .ifPresent(code -> queryWrapper.eq(EmergencyTrainingProgram::getOrgCode, code));
        Optional.ofNullable(trainingProgram.getTrainingPlanTime())
                .ifPresent(time -> queryWrapper.eq(EmergencyTrainingProgram::getTrainingPlanTime, time));
        Optional.ofNullable(trainingProgram.getStatus())
                .ifPresent(status -> queryWrapper.eq(EmergencyTrainingProgram::getStatus, status));
        Optional.ofNullable(trainingProgram.getTrainingProgramCode())
                .ifPresent(programCode -> queryWrapper.like(EmergencyTrainingProgram::getTrainingProgramCode, programCode));
        Optional.ofNullable(trainingProgram.getTrainingProgramName())
                .ifPresent(programName -> queryWrapper.like(EmergencyTrainingProgram::getTrainingProgramName, programName));
        queryWrapper.orderByDesc(EmergencyTrainingProgram::getCreateTime).orderByDesc(EmergencyTrainingProgram::getUpdateTime);
        IPage<EmergencyTrainingProgram> pageList = this.page(page, queryWrapper);

        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        List<EmergencyTrainingProgram> records = pageList.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            for (EmergencyTrainingProgram record : records) {
                SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(record.getOrgCode());
                record.setOrgName(ObjectUtil.isNotEmpty(sysDepartModel)?sysDepartModel.getDepartName():"");
                List<EmergencyTrainingTeam> trainingTeam = emergencyTrainingProgramMapper.getTrainingTeam(record.getId());
                record.setEmergencyTrainingTeamList(trainingTeam);
                List<String> names = trainingTeam.stream().map(EmergencyTrainingTeam::getEmergencyTeamName).collect(Collectors.toList());
                record.setEmergencyTeamName(CollUtil.join(names, ","));
            }
        }
        GlobalThreadLocal.setDataFilter(b);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(EmergencyTrainingProgram emergencyTrainingProgram) {
        String code = emergencyTrainingProgram.getTrainingProgramCode();
        String trainPlanCode = getTrainPlanCode();
        if (!code.equals(trainPlanCode)) {
            return Result.OK("训练计划编号已存在，添加失败");
        }

        String result = "添加成功！";
        if (TeamConstant.PUBLISH.equals(emergencyTrainingProgram.getSaveFlag())) {
            emergencyTrainingProgram.setStatus(TeamConstant.WAIT_COMPLETE);
            publish(emergencyTrainingProgram);
            result = "下发成功！";
        } else {
            emergencyTrainingProgram.setStatus(TeamConstant.WAIT_PUBLISH);
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        emergencyTrainingProgram.setOrgCode(user.getOrgCode());
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
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(EmergencyTrainingProgram emergencyTrainingProgram) {
        EmergencyTrainingProgram byId = this.getById(emergencyTrainingProgram.getId());
        if (ObjectUtil.isEmpty(byId)) {
            return Result.error("未找到对应数据");
        }
        if (!TeamConstant.WAIT_PUBLISH.equals(byId.getStatus())) {
            return Result.error("当前计划不可编辑");
        }
        this.updateById(emergencyTrainingProgram);
        List<EmergencyTrainingTeam> emergencyTrainingTeamList = emergencyTrainingProgram.getEmergencyTrainingTeamList();
        if (CollUtil.isNotEmpty(emergencyTrainingTeamList)) {
            LambdaQueryWrapper<EmergencyTrainingTeam> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyTrainingTeam::getEmergencyTrainingProgramId, emergencyTrainingProgram.getId());
            emergencyTrainingTeamService.getBaseMapper().delete(queryWrapper);
            for (EmergencyTrainingTeam emergencyTrainingTeam : emergencyTrainingTeamList) {
                emergencyTrainingTeam.setEmergencyTrainingProgramId(emergencyTrainingProgram.getId());
                emergencyTrainingTeamService.save(emergencyTrainingTeam);
            }
        }
        if (TeamConstant.PUBLISH.equals(emergencyTrainingProgram.getSaveFlag())) {
            emergencyTrainingProgram.setStatus(TeamConstant.WAIT_COMPLETE);
            this.updateById(emergencyTrainingProgram);
            this.publish(emergencyTrainingProgram);
            return Result.OK("下发成功");
        }
        return Result.OK("编辑成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(EmergencyTrainingProgram program) {
        LambdaQueryWrapper<EmergencyTrainingTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyTrainingTeam::getDelFlag, TeamConstant.DEL_FLAG0);
        queryWrapper.eq(EmergencyTrainingTeam::getEmergencyTrainingProgramId, program.getId());
        List<EmergencyTrainingTeam> teamList = emergencyTrainingTeamService.getBaseMapper().selectList(queryWrapper);
        if (CollUtil.isNotEmpty(teamList)) {
            for (EmergencyTrainingTeam emergencyTrainingTeam : teamList) {
                emergencyTrainingTeam.setDelFlag(TeamConstant.DEL_FLAG1);
                emergencyTrainingTeamService.updateById(emergencyTrainingTeam);
            }
        }
        program.setDelFlag(TeamConstant.DEL_FLAG1);
        this.updateById(program);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(EmergencyTrainingProgram program ) {
        LambdaQueryWrapper<EmergencyTrainingTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyTrainingTeam::getDelFlag, TeamConstant.DEL_FLAG0);
        queryWrapper.eq(EmergencyTrainingTeam::getEmergencyTrainingProgramId, program.getId());
        List<EmergencyTrainingTeam> teamList = emergencyTrainingTeamService.getBaseMapper().selectList(queryWrapper);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (CollUtil.isNotEmpty(teamList)) {
            List<String> userIds = new ArrayList<>();
            for (EmergencyTrainingTeam emergencyTrainingTeam : teamList) {
                String emergencyTeamId = emergencyTrainingTeam.getEmergencyTeamId();
                EmergencyTeam team = emergencyTeamService.getById(emergencyTeamId);
                userIds.add(team.getManagerId());
            }
            String[] strings = userIds.toArray(new String[userIds.size()]);
            List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(strings);
            String userNameStr = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(), userNameStr, "应急训练计划"+DateUtil.today(), null, CommonConstant.MSG_CATEGORY_7);
            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put("trainingProgramCode",program.getTrainingProgramCode() );
            map.put("trainingProgramName",program.getTrainingProgramName() );
            map.put("trainingPlanTime",DateUtil.format(program.getTrainingPlanTime(), "yyyy-MM") );
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, program.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.EMERGENCY.getType());
            messageDTO.setData(map);
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.EMERGENCY_MANAGEMENT_MESSAGE);
            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
            messageDTO.setTemplateCode(CommonConstant.EMERGENCY_MANAGEMENT_SERVICE);
            messageDTO.setMsgAbstract("有新的应急训练计划");
            messageDTO.setPublishingContent("有新的应急训练计划,请注意训练任务开始时间!");
            iSysBaseAPI.sendTemplateMessage(messageDTO);
        }
    }

    @Override
    public Result<EmergencyTrainingProgram> queryById(EmergencyTrainingProgram emergencyTrainingProgram) {
        SysDepartModel sysDepartModel = iSysBaseAPI.getDepartByOrgCode(emergencyTrainingProgram.getOrgCode());
        emergencyTrainingProgram.setOrgName(sysDepartModel.getDepartName());
        List<EmergencyTrainingTeam> trainingTeam = emergencyTrainingProgramMapper.getTrainingTeam(emergencyTrainingProgram.getId());
        emergencyTrainingProgram.setEmergencyTrainingTeamList(trainingTeam);
        String trainees = emergencyTrainingProgramMapper.getTrainees(emergencyTrainingProgram.getId());
        emergencyTrainingProgram.setTrainees(trainees);
        return Result.OK(emergencyTrainingProgram);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                String type = FilenameUtils.getExtension(file.getOriginalFilename());
                if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                    return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
                }

                List<TrainingProgramModel> trainingProgramModels = ExcelImportUtil.importExcel(file.getInputStream(), TrainingProgramModel.class, params);
                Iterator<TrainingProgramModel> iterator = trainingProgramModels.iterator();
                while (iterator.hasNext()) {
                    TrainingProgramModel model = iterator.next();
                    boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
                    if (b) {
                        iterator.remove();
                    }
                }
                if (CollUtil.isEmpty(trainingProgramModels)) {
                    return Result.error("文件导入失败:文件内容不能为空！");
                }
                Map<String, String> data = new HashMap<>();
                for (TrainingProgramModel trainingProgramModel : trainingProgramModels) {
                    StringBuilder stringBuilder = new StringBuilder();
                    //数据重复性校验
                    String s = data.get(trainingProgramModel.getTrainingProgramName());
                    if (StrUtil.isNotEmpty(s)) {
                        stringBuilder.append("该数据存在相同数据，");
                    } else {
                        data.put(trainingProgramModel.getTrainingProgramName(), trainingProgramModel.getTrainingTeam());
                    }
                    //数据校验
                    checkTrainingProgram(stringBuilder, trainingProgramModel);
                    if (stringBuilder.length() > 0) {
                        // 截取字符
                        stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        trainingProgramModel.setMistake(stringBuilder.toString());
                        errorLines++;
                    }
                }
                if (errorLines > 0) {
                    //存在错误，导出错误清单
                    return getErrorExcel(errorLines, errorMessage, trainingProgramModels, successLines, null, type);
                }

                //校验通过，添加数据
                for (TrainingProgramModel programModel : trainingProgramModels) {
                    String trainPlanCode = this.getTrainPlanCode();
                    EmergencyTrainingProgram emergencyTrainingProgram = new EmergencyTrainingProgram();
                    emergencyTrainingProgram.setTrainingProgramCode(trainPlanCode);
                    emergencyTrainingProgram.setTrainingProgramName(programModel.getTrainingProgramName());
                    DateTime time = DateUtil.parse(programModel.getTrainingPlanTime(), "yyyy年MM月");
                    String format = DateUtil.format(time, "yyyy-MM");
                    emergencyTrainingProgram.setTrainingPlanTime(DateUtil.parse(format,"yyyy-MM"));
                    emergencyTrainingProgram.setTraineesNum(programModel.getPeopleNum());
                    emergencyTrainingProgram.setStatus(TeamConstant.WAIT_PUBLISH);
                    LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    emergencyTrainingProgram.setOrgCode(user.getOrgCode());
                    emergencyTrainingProgram.setRemark(programModel.getRemark());
                    this.save(emergencyTrainingProgram);

                    List<String> trainingTeamId = programModel.getTrainingTeamId();
                    for (String teamId : trainingTeamId) {
                        EmergencyTrainingTeam emergencyTrainingTeam = new EmergencyTrainingTeam();
                        emergencyTrainingTeam.setEmergencyTrainingProgramId(emergencyTrainingProgram.getId());
                        emergencyTrainingTeam.setEmergencyTeamId(teamId);
                        emergencyTrainingTeamService.save(emergencyTrainingTeam);
                    }
                }
                return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        errorMessage.add("文件导入失败");
        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,null);
    }

    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, List<TrainingProgramModel> trainingProgramModels, int successLines, String url, String type) {

        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/emergencyTrainingProgramError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>();
            List<Map<String, String>> mapList = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            for (TrainingProgramModel trainingProgramModel : trainingProgramModels) {
                map.put("trainingProgramName", trainingProgramModel.getTrainingProgramName());
                map.put("trainingTeam", trainingProgramModel.getTrainingTeam());
                map.put("trainingPlanTime", trainingProgramModel.getTrainingPlanTime());
                map.put("remark", trainingProgramModel.getRemark());
                map.put("mistake", trainingProgramModel.getMistake());
                mapList.add(map);
            }
            errorMap.put("maplist", mapList);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            String fileName = "应急队伍训练计划导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, true, url);
    }

    private void checkTrainingProgram(StringBuilder stringBuilder,TrainingProgramModel trainingProgramModel) {
        String trainingProgramName = trainingProgramModel.getTrainingProgramName();
        String trainingTeam = trainingProgramModel.getTrainingTeam();
        String trainingPlanTime = trainingProgramModel.getTrainingPlanTime();

        if (StrUtil.isEmpty(trainingProgramName)) {
            stringBuilder.append("训练项目不能为空，");
        }
        if (StrUtil.isNotEmpty(trainingTeam)) {
            List<String> teams = StrUtil.splitTrim(trainingTeam, "；");
            //去重
            List<String> list = teams.stream().distinct().collect(Collectors.toList());
            if (teams.size() < list.size()) {
                stringBuilder.append("训练队伍有重复，");
            }
            List<String> teamIds = new ArrayList<>();
            int num = 0;
            for (String team : list) {
                LambdaQueryWrapper<EmergencyTeam> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
                queryWrapper.eq(EmergencyTeam::getEmergencyTeamname, team);
                EmergencyTeam one = emergencyTeamService.getOne(queryWrapper);
                if (ObjectUtil.isEmpty(one)) {
                    stringBuilder.append("系统不存在" + team + "该队伍，");
                }else {
                    teamIds.add(one.getId());
                    if (one.getPeopleNum() != null) {
                        num = num + one.getPeopleNum();
                    }
                }

            }

            trainingProgramModel.setTrainingTeamId(teamIds);
            trainingProgramModel.setPeopleNum(num);
        } else {
            stringBuilder.append("训练队伍不能为空，");
        }

        if (StrUtil.isNotEmpty(trainingPlanTime)) {
            boolean legalDate = TimeUtil.isLegalDate(trainingPlanTime.length(), trainingPlanTime, "yyyy年MM月");
            if (!legalDate) {
                stringBuilder.append("训练时间格式不对，");
            }
        }else {
            stringBuilder.append("训练时间不能为空，");
        }

    }

    @Override
    public ModelAndView exportXls(HttpServletRequest request,HttpServletResponse response, EmergencyTrainingProgramDTO emergencyTrainingProgramDTO) {
        IPage<EmergencyTrainingProgram> pageList = this.queryPageList(emergencyTrainingProgramDTO, 1, Integer.MAX_VALUE);
        List<EmergencyTrainingProgram> records = pageList.getRecords();
        for (EmergencyTrainingProgram record : records) {
            String trainees = emergencyTrainingProgramMapper.getTrainees(record.getId());
            record.setTrainees(trainees);
        }
        List<EmergencyTrainingProgram> exportList = null;
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = records.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = records;
        }

        if (CollUtil.isNotEmpty(exportList)) {
            int sort = 1;
            for (EmergencyTrainingProgram record : exportList) {
                record.setSort(Convert.toStr(sort));
                sort++;
            }
        }
        String title ="应急队伍训练计划表";
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, EmergencyTrainingProgram.class);
        org.jeecgframework.poi.excel.entity.ExportParams exportParams=new ExportParams(title, title);
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }
}
