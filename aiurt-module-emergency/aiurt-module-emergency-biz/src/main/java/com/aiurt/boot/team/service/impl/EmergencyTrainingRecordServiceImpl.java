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
import com.aiurt.boot.team.constants.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.*;
import com.aiurt.boot.team.listener.RecordExcelListener;
import com.aiurt.boot.team.mapper.EmergencyTrainingProgramMapper;
import com.aiurt.boot.team.mapper.EmergencyTrainingRecordMapper;
import com.aiurt.boot.team.model.ProcessRecordModel;
import com.aiurt.boot.team.model.RecordModel;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.common.util.TimeUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTrainingRecordServiceImpl extends ServiceImpl<EmergencyTrainingRecordMapper, EmergencyTrainingRecord> implements IEmergencyTrainingRecordService {
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;
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

        LambdaQueryWrapper<EmergencyTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyTeam::getDelFlag, TeamConstant.DEL_FLAG0);
        List<EmergencyTeam> emergencyTeams = emergencyTeamService.getBaseMapper().selectList(wrapper);
        if (CollUtil.isNotEmpty(emergencyTeams)) {
            List<String> list = emergencyTeams.stream().map(EmergencyTeam::getId).collect(Collectors.toList());
            emergencyTrainingRecordDTO.setIds(list);
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
                LoginUser userById = iSysBaseAPI.getUserById(trainingCrew.getUserId());
                trainingCrew.setRealname(userById.getRealname());
                String jobName = iSysBaseAPI.translateDict(TeamConstant.SYS_POST, Convert.toStr(userById.getJobName()));
                trainingCrew.setJobName(jobName!=null?jobName:"");
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
        if (CollUtil.isNotEmpty(attList)) {
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

        if (TeamConstant.SUBMITTED.equals(emergencyTrainingRecord.getStatus())) {
            //如果是提交，判断是否所有内容填写完整
            if (CollUtil.isEmpty(emergencyTrainingRecord.getCrewList()) || CollUtil.isEmpty(emergencyTrainingRecord.getProcessRecordList()) ||CollUtil.isEmpty(emergencyTrainingRecord.getAttList())) {
                return Result.error("信息未填写完整，无法提交");
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
        } else {
            LambdaQueryWrapper<EmergencyTrainingRecordCrew> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmergencyTrainingRecordCrew::getEmergencyTrainingRecordId, emergencyTrainingRecord.getId());
            emergencyTrainingRecordCrewService.getBaseMapper().delete(queryWrapper);
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
                return Result.error("信息未填写完整，无法提交");
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
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return XlsUtil.importReturnRes(errorLines, successLines, errorMessage, false, null);
            }
            RecordExcelListener recordExcelListener = new RecordExcelListener();
            try {
                EasyExcel.read(file.getInputStream(), RecordData.class, recordExcelListener).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RecordModel recordModel = recordExcelListener.getRecordModel();
            //校验是否有空行
            List<ProcessRecordModel> processRecordModelList = recordModel.getProcessRecordModelList();
            Iterator<ProcessRecordModel> iterator = processRecordModelList.iterator();
            while (iterator.hasNext()) {
                ProcessRecordModel model = iterator.next();
                boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                if (a) {
                    iterator.remove();
                }
            }
            if (CollUtil.isEmpty(processRecordModelList)) {
                return Result.error("文件导入失败:训练过程记录不能为空！");
            }

            errorLines = checkTeam(recordModel, errorLines);

            if (errorLines > 0) {
                //存在错误，导出错误清单
                return getErrorExcel(errorLines, errorMessage, recordModel, successLines, null, type);
            }

            //校验通过，添加数据
            EmergencyTrainingRecord emergencyTrainingRecord = new EmergencyTrainingRecord();
            emergencyTrainingRecord.setStatus(TeamConstant.To_BE_SUBMITTED);
            BeanUtil.copyProperties(recordModel, emergencyTrainingRecord);
            List<EmergencyTrainingProcessRecord> processRecordList = new ArrayList<>();
            for (ProcessRecordModel processRecordModel : recordModel.getProcessRecordModelList()) {
                EmergencyTrainingProcessRecord processRecord = new EmergencyTrainingProcessRecord();
                processRecord.setNextDay(1);
                BeanUtil.copyProperties(processRecordModel, processRecord);
                processRecordList.add(processRecord);
            }
            emergencyTrainingRecord.setProcessRecordList(processRecordList);
            this.add(emergencyTrainingRecord);
            return Result.ok("文件导入成功！");
        }
        return Result.ok("文件导入失败！");
    }


    private Result<?> getErrorExcel(int errorLines, List<String> errorMessage, RecordModel recordModel, int successLines,String url, String type) {
        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/emergencyTrainingRecordError.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>();
            List<Map<String, String>> mapList = new ArrayList<>();
            List<Map<String, String>> mistakeMapList = new ArrayList<>();
            errorMap.put("trainingTime", recordModel.getTrainingTime());
            errorMap.put("position", recordModel.getPosition());
            errorMap.put("emergencyTeam", recordModel.getEmergencyTeam());
            errorMap.put("trainees", recordModel.getTrainees());
            errorMap.put("emergencyTrainingProgram", recordModel.getEmergencyTrainingProgram());
            errorMap.put("trainingProgramCode", recordModel.getTrainingProgramCode());
            errorMap.put("trainingAppraise", recordModel.getTrainingAppraise());
            errorMap.put("mistake", recordModel.getMistake());

            List<ProcessRecordModel> processRecordModelList = recordModel.getProcessRecordModelList();
            if (CollUtil.isNotEmpty(processRecordModelList)) {
                for (ProcessRecordModel processRecordModel : processRecordModelList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("sort", processRecordModel.getSort());
                    map.put("trainingTime", processRecordModel.getTrainingTime());
                    map.put("trainingContent", processRecordModel.getTrainingContent());
                    map.put("mistake", processRecordModel.getMistake());
                    mapList.add(map);
                }
            }

            errorMap.put("maplist", mapList);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            //处理合并单元格
            int size = processRecordModelList.size();
            Sheet sheet = workbook.getSheetAt(0);
            CellRangeAddress region = new CellRangeAddress(6,6+size,1,1);
            //左右空白边框合并
            CellRangeAddress regionLeft = new CellRangeAddress(1,10+size,0,0);
            CellRangeAddress regionRight = new CellRangeAddress(1,10+size,10,11);

            //合并
            sheet.addMergedRegion(region);
            sheet.addMergedRegion(regionLeft);
            sheet.addMergedRegion(regionRight);
            //合并后设置下边框
            RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
            for (int j = 0 ;j < size; j++) {
                CellRangeAddress cellAddresses = new CellRangeAddress(7+j,7+j,4,9);
                //合并
                sheet.addMergedRegion(cellAddresses);
                //合并后设置下边框
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
            }

            String fileName = "应急队伍训练记录导入错误清单"+"_" + System.currentTimeMillis()+"."+type;
            FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
            url = File.separator+"errorExcelFiles"+ File.separator+fileName;
            workbook.write(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return XlsUtil.importReturnRes(errorLines, successLines, errorMessage,true,url);
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
            if (list.size() == 3) {
                JSONObject lineByName = iSysBaseAPI.getLineByName(list.get(0));
                JSONObject stationByName = iSysBaseAPI.getStationByName(list.get(1));
                String lineCode = lineByName.getString("lineCode");
                String  stationCode = stationByName.getString("stationCode");
                JSONObject positionByName = iSysBaseAPI.getPositionByName(list.get(2),lineCode,stationCode);
                if (ObjectUtil.isEmpty(positionByName)) {
                    stringBuilder.append("训练地点中位置不存在，");
                }else {
                    recordModel.setPositionCode(positionByName.getString("positionCode"));
                }
            }
            if (list.size() == 2) {
                JSONObject stationByName = iSysBaseAPI.getStationByName(list.get(1));
                if (ObjectUtil.isEmpty(stationByName)) {
                    stringBuilder.append("训练地点中站点不存在，");
                } else {
                    String stationCode = stationByName.getString("stationCode");
                    recordModel.setStationCode(stationCode);
                }
            }
            if (list.size() == 1) {
                JSONObject lineByName = iSysBaseAPI.getLineByName(list.get(0));
                if (ObjectUtil.isEmpty(lineByName)) {
                    stringBuilder.append("训练地点中线路不存在，");
                } else {
                    String lineCode = lineByName.getString("lineCode");
                    recordModel.setLineCode(lineCode);
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
                    HashMap<String, String> map = new HashMap<>(emergencyCrews.size());
                    for (EmergencyCrew emergencyCrew : emergencyCrews) {
                        LoginUser userById = iSysBaseAPI.getUserById(emergencyCrew.getUserId());
                        map.put(userById.getRealname(), emergencyCrew.getId());
                    }
                    List<String> list = StrUtil.splitTrim(trainees, "：");
                    List<EmergencyTrainingRecordCrew> emergencyTrainingRecordCrews = new ArrayList<>();
                    for (String s : list) {
                        String id = map.get(s);
                        if (StrUtil.isEmpty(id)) {
                            stringBuilder.append("应急队伍不存在" + s + "该人员,");
                        } else {
                            EmergencyTrainingRecordCrew emergencyTrainingRecordCrew = new EmergencyTrainingRecordCrew();
                            emergencyTrainingRecordCrew.setEmergencyCrewId(id);
                            emergencyTrainingRecordCrews.add(emergencyTrainingRecordCrew);
                        }
                    }
                    recordModel.setTraineesNum(list.size());
                    recordModel.setCrewList(emergencyTrainingRecordCrews);
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

        List<ProcessRecordModel> processRecordModelList = recordModel.getProcessRecordModelList();
        if (CollUtil.isNotEmpty(processRecordModelList)) {
            for (ProcessRecordModel processRecordModel : processRecordModelList) {
                StringBuilder stringBuilder1 = new StringBuilder();
                String trainingTime1 = processRecordModel.getTrainingTime();
                String trainingContent = processRecordModel.getTrainingContent();
                if (StrUtil.isNotEmpty(trainingTime1)) {
                    boolean legalDate = TimeUtil.isLegalDate(trainingTime1.length(), trainingTime1, "HH:mm");
                    if (!legalDate) {
                        stringBuilder1.append("时间格式不对，");
                    }
                }
                if (StrUtil.isEmpty(trainingContent)) {
                    stringBuilder1.append("训练内容不能为空，");
                }
                if (stringBuilder1.length() > 0) {
                    // 截取字符
                    stringBuilder1 = stringBuilder1.deleteCharAt(stringBuilder1.length() - 1);
                    processRecordModel.setMistake(stringBuilder1.toString());
                    errorLines++;
                }
            }
        }else {
            stringBuilder.append("训练过程记录不能为空，");
        }

        if (StrUtil.isNotEmpty(recordModel.getEmergencyTeamId()) && StrUtil.isNotEmpty(recordModel.getEmergencyTrainingProgramId())) {
            LambdaQueryWrapper<EmergencyTrainingTeam> teamQueryWrapper = new LambdaQueryWrapper<>();
            teamQueryWrapper.eq(EmergencyTrainingTeam::getDelFlag, TeamConstant.DEL_FLAG0);
            teamQueryWrapper.eq(EmergencyTrainingTeam::getEmergencyTrainingProgramId, recordModel.getEmergencyTrainingProgramId());
            teamQueryWrapper.eq(EmergencyTrainingTeam::getEmergencyTeamId, recordModel.getEmergencyTeamId());
            EmergencyTrainingTeam one = emergencyTrainingTeamService.getBaseMapper().selectOne(teamQueryWrapper);
            if (ObjectUtil.isEmpty(one)) {
                stringBuilder.append("该应急队伍不存在该应急计划，");
            }

            LambdaQueryWrapper<EmergencyTrainingRecord> recordLambdaQueryWrapper = new LambdaQueryWrapper<>();
            recordLambdaQueryWrapper.eq(EmergencyTrainingRecord::getEmergencyTeamId, recordModel.getEmergencyTeamId());
            recordLambdaQueryWrapper.eq(EmergencyTrainingRecord::getEmergencyTrainingProgramId, recordModel.getEmergencyTrainingProgramId());
            recordLambdaQueryWrapper.eq(EmergencyTrainingRecord::getDelFlag,TeamConstant.DEL_FLAG0);
            EmergencyTrainingRecord emergencyTrainingRecord = this.getBaseMapper().selectOne(recordLambdaQueryWrapper);
            if (ObjectUtil.isNotEmpty(emergencyTrainingRecord)) {
                stringBuilder.append("该应急队伍已存在该应急计划的训练记录，");
            }
        }
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            recordModel.setMistake(stringBuilder.toString());
            errorLines++;
        }
        return errorLines;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportXls(HttpServletResponse response, String id) {
        EmergencyTrainingRecordVO emergencyTrainingRecordVO = emergencyTrainingRecordMapper.queryById(id);
        String lineCode = emergencyTrainingRecordVO.getLineCode();
        String stationCode = emergencyTrainingRecordVO.getStationCode();
        String positionCode = emergencyTrainingRecordVO.getPositionCode();
        String position = null;
        if (StrUtil.isNotEmpty(lineCode)) {
            position = iSysBaseAPI.getPosition(lineCode);
        }
        if (StrUtil.isNotEmpty(stationCode)) {
            position = iSysBaseAPI.getPosition(stationCode);
        }
        if (StrUtil.isNotEmpty(positionCode)) {
            position = iSysBaseAPI.getPosition(positionCode);
        }

        List<EmergencyCrewVO> trainingCrews = emergencyTrainingRecordMapper.getTrainingCrews(id);

        List<String> collects = trainingCrews.stream().map(EmergencyCrewVO::getRealname).collect(Collectors.toList());
        String trainees = CollUtil.join(collects, ",");

        LambdaQueryWrapper<EmergencyTrainingRecordAtt> attQueryWrapper = new LambdaQueryWrapper<>();
        attQueryWrapper.eq(EmergencyTrainingRecordAtt::getDelFlag, TeamConstant.DEL_FLAG0);
        attQueryWrapper.eq(EmergencyTrainingRecordAtt::getEmergencyTrainingRecordId, emergencyTrainingRecordVO.getId());
        List<EmergencyTrainingRecordAtt> recordAtts = emergencyTrainingRecordAttService.getBaseMapper().selectList(attQueryWrapper);


        LambdaQueryWrapper<EmergencyTrainingProcessRecord> processRecordQueryWrapper = new LambdaQueryWrapper<>();
        processRecordQueryWrapper.eq(EmergencyTrainingProcessRecord::getDelFlag, TeamConstant.DEL_FLAG0);
        processRecordQueryWrapper.eq(EmergencyTrainingProcessRecord::getEmergencyTrainingRecordId, emergencyTrainingRecordVO.getId());
        List<EmergencyTrainingProcessRecord> processRecords = emergencyTrainingProcessRecordService.getBaseMapper().selectList(processRecordQueryWrapper);

        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/emergencyTrainingRecordXls.xlsx");
            Map<String, Object> errorMap = new HashMap<String, Object>();
            List<Map<String, String>> mapList = new ArrayList<>();
            errorMap.put("trainingTime", DateUtil.format(emergencyTrainingRecordVO.getTrainingTime(),"yyyy-MM-dd"));
            errorMap.put("position", position);
            errorMap.put("emergencyTeam", emergencyTrainingRecordVO.getEmergencyTeamname());
            errorMap.put("trainees", trainees);
            errorMap.put("emergencyTrainingProgram", emergencyTrainingRecordVO.getTrainingProgramName());
            errorMap.put("trainingProgramCode", emergencyTrainingRecordVO.getTrainingProgramCode());
            errorMap.put("trainingAppraise", emergencyTrainingRecordVO.getTrainingAppraise());

            if (CollUtil.isNotEmpty(processRecords)) {
                for (int i = 0; i < processRecords.size(); i++) {
                    Map<String, String> map = new HashMap<>();
                    EmergencyTrainingProcessRecord processRecord = processRecords.get(i);
                    map.put("sort", Convert.toStr(i));
                    map.put("trainingTime", DateUtil.format(processRecord.getTrainingTime(),"HH:mm"));
                    map.put("trainingContent", processRecord.getTrainingContent());
                    mapList.add(map);
                }
            }

            errorMap.put("maplist", mapList);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            //处理合并单元格
            int size = processRecords.size();
            Sheet sheet = workbook.getSheetAt(0);
            CellRangeAddress region = new CellRangeAddress(5,5+size,1,1);
            //合并
            sheet.addMergedRegion(region);
            //合并后设置下边框
            RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
            for (int j = 0 ;j < size; j++) {
                CellRangeAddress cellAddresses = new CellRangeAddress(6+j,6+j,4,9);
                //合并
                sheet.addMergedRegion(cellAddresses);
                //合并后设置下边框
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
            }

            //打包成压缩包导出
            String fileName = "应急队伍训练记录.zip";
            response.setContentType("application/zip");
            response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            OutputStream outputStream = response.getOutputStream();
            // 压缩输出流,包装流,将临时文件输出流包装成压缩流,将所有文件输出到这里,打成zip包
            ZipOutputStream zipOut = new ZipOutputStream(outputStream);

            for (EmergencyTrainingRecordAtt recordAtt : recordAtts) {
                String attName = null;
                String filePath = null;
                String path = recordAtt.getPath();
                filePath = StrUtil.subBefore(path, "?", false);

                filePath = filePath.replace("..", "").replace("../", "");
                if (filePath.endsWith(SymbolConstant.COMMA)) {
                    filePath = filePath.substring(0, filePath.length() - 1);
                }

                SysAttachment sysAttachment = iSysBaseAPI.getFilePath(filePath);
                InputStream inputStream = null;

                if (Objects.isNull(sysAttachment)) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new AiurtBootException("文件不存在..");
                    }
                    if (StrUtil.isBlank(recordAtt.getName())) {
                        attName = file.getName();
                    } else {
                        attName = recordAtt.getName();
                    }
                    inputStream = new BufferedInputStream(new FileInputStream(filePath));

                    XlsUtil.outZip(inputStream,attName,zipOut);
                    //关闭流
                    inputStream.close();

                }else {
                    if (StrUtil.equalsIgnoreCase("minio",sysAttachment.getType())) {
                        inputStream = MinioUtil.getMinioFile("platform",sysAttachment.getFilePath());
                    }else {
                        String imgPath = upLoadPath + File.separator + sysAttachment.getFilePath();
                        File file = new File(imgPath);
                        if (!file.exists()) {
                            response.setStatus(404);
                            throw new RuntimeException("文件[" + imgPath + "]不存在..");
                        }
                        inputStream = new BufferedInputStream(new FileInputStream(imgPath));
                    }
                    XlsUtil.outZip(inputStream,sysAttachment.getFileName(),zipOut);
                    //关闭流
                    inputStream.close();
                }

            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(barray);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
            String file = "应急队伍训练记录.xlsx";
            XlsUtil.outZip(bufferedInputStream,file,zipOut);
            //关闭流
            is.close();
            bufferedInputStream.close();

            zipOut.flush();
            // 压缩完成后,关闭压缩流
            zipOut.close();

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
