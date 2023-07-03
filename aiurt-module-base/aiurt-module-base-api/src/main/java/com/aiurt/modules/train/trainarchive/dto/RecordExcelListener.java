package com.aiurt.modules.train.trainarchive.dto;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.train.traindegreerecord.dto.DegreeRecordExcelDTO;
import com.aiurt.modules.train.trainjobchangerecord.dto.JobChangeRecordExcelDTO;
import com.aiurt.modules.train.trainrecord.dto.RecordExcelDTO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author: hlq
 * @date: 2022-12-16 17:34
 */
@Data
public class RecordExcelListener extends AnalysisEventListener<RecordData> {

    private ArchiveExcelDTO excelDTO = new ArchiveExcelDTO();
    private DegreeRecordExcelDTO firstDegreeRecord = new DegreeRecordExcelDTO();
    private DegreeRecordExcelDTO highestDegreeRecord = new DegreeRecordExcelDTO();
    private List<JobChangeRecordExcelDTO> changeRecords = new ArrayList<>();
    private List<RecordExcelDTO> trainRecords = new ArrayList<>();
    private StringBuffer archiveMistake = new StringBuffer();
    private StringBuilder firstMistake = new StringBuilder();
    private StringBuilder highestMistake = new StringBuilder();
    private StringBuilder changeMistake = new StringBuilder();
    private StringBuilder recordMistake = new StringBuilder();
    private boolean isPassJobTime = false;
    private boolean isPassTrainTime = false;
    private boolean isErrorLine = false;
    private int isPassTitle = 0;
    private int passJobTimeNumber = 1;
    private int passTrainTimeNumber = 1;
    private int errorLines = 0;
    private int successLines = 0;

    @Override
    public void invoke(RecordData data, AnalysisContext context) {
        int sheetNo = context.readSheetHolder().getSheetNo();
        String firstDegree = "第一学历";
        String degreeName = "学历";
        String endSchool = "毕业院校";
        String jobTime = "上岗时间";
        String trainTime = "培训时间";
        String highestDegree = "最高学历";
        String changRecord = "岗位变动记录";
        String trainRecord = "培训记录";
        if (sheetNo == 0) {
            //获取行的索引
            int index = context.readRowHolder().getRowIndex();
            if (index == 2) {
                if(ObjectUtil.isEmpty(data.getRow1())||ObjectUtil.isEmpty(data.getRow6())){
                    errorLines++;
                }
                if(ObjectUtil.isEmpty(data.getRow1())){
                    archiveMistake.append("姓名不为空，");
                }
                if(ObjectUtil.isEmpty(data.getRow6())){
                    archiveMistake.append("工号不为空，");
                }

                String realName = data.getRow1();
                String workCode = data.getRow6();
                excelDTO.setRealname(realName);
                excelDTO.setWorkNo(workCode);
                if(ObjectUtil.isNotEmpty(archiveMistake)){
                    archiveMistake.append("如已填写，请检查格式是否有误，");
                    archiveMistake = archiveMistake.deleteCharAt(archiveMistake.length() - 1);
                    excelDTO.setArchiveMistake(archiveMistake.toString());
                    errorLines++;
                    excelDTO.setErrorLines(errorLines);
                }else {
                    successLines++;
                    excelDTO.setSuccessLines(successLines);

                }
            }
            if (index > 2) {
                if (data.getRow0().equals(firstDegree)) {
                    isPassTitle = isPassTitle + 1;
                }
                if (data.getRow0().equals(highestDegree)) {
                    isPassTitle = isPassTitle + 1;
                }
                if (data.getRow0().equals(changRecord)) {
                    isPassTitle = isPassTitle + 1;
                }
                if (data.getRow0().equals(trainRecord)) {
                    isPassTitle = isPassTitle + 1;
                }
                if (isPassTitle == 1) {
                    if (data.getRow0().equals(degreeName)) {
                        if(ObjectUtil.isEmpty(data.getRow1())){
                            firstMistake.append("学历不为空，");
                        }
                        if(ObjectUtil.isEmpty(data.getRow4())){
                            firstMistake.append("毕业形式不为空，");
                        }
                        if(ObjectUtil.isEmpty(data.getRow8())){
                            firstMistake.append("毕业时间不为空，");
                        }
                        String degree = data.getRow1();
                        String form = data.getRow4();
                        String endTime = data.getRow8();
                        firstDegreeRecord.setDegreeName(degree);
                        firstDegreeRecord.setGraduationFormName(form);
                        firstDegreeRecord.setSchoolEndTime(endTime);
                        if(ObjectUtil.isNotEmpty(firstMistake)){
                            errorLines++;
                            excelDTO.setErrorLines(errorLines);
                        }else {
                            successLines++;
                            excelDTO.setSuccessLines(successLines);
                        }
                    }
                    boolean isError =false;
                    if (data.getRow0().equals(endSchool)) {
                        if(ObjectUtil.isEmpty(data.getRow1())){
                            firstMistake.append("毕业学校不为空，");
                            isError =true;
                        }
                        if(ObjectUtil.isEmpty(data.getRow6())){
                            firstMistake.append("所学专业不为空，");
                            isError =true;
                        }
                        String school = data.getRow1();
                        String major = data.getRow6();
                        firstDegreeRecord.setGraduationSchool(school);
                        firstDegreeRecord.setMajorsStudied(major);
                        excelDTO.setFirstDegree(firstDegreeRecord);
                        if(!isError){
                            successLines++;
                            excelDTO.setSuccessLines(successLines);
                        }
                    }
                    if(ObjectUtil.isNotEmpty(firstMistake)){
                        excelDTO.setFirstMistake(firstMistake.toString());
                    }
                    if(isError){
                        errorLines++;
                        excelDTO.setErrorLines(errorLines);
                    }

                }
                if (isPassTitle == 2) {

                    if (data.getRow0().equals(degreeName)) {
                        if(ObjectUtil.isEmpty(data.getRow1())){
                            highestMistake.append("学历不为空，");
                        }
                        if(ObjectUtil.isEmpty(data.getRow4())){
                            highestMistake.append("毕业形式不为空，");
                        }
                        if(ObjectUtil.isEmpty(data.getRow8())){
                            highestMistake.append("毕业时间不为空，");
                        }
                        String degree = data.getRow1();
                        String form = data.getRow4();
                        String endTime = data.getRow8();
                        highestDegreeRecord.setDegreeName(degree);
                        highestDegreeRecord.setGraduationFormName(form);
                        highestDegreeRecord.setSchoolEndTime(endTime);
                        if(ObjectUtil.isNotEmpty(highestMistake)){
                            errorLines++;
                            excelDTO.setErrorLines(errorLines);
                        }else {
                                successLines++;
                                excelDTO.setSuccessLines(successLines);
                        }
                    }
                    boolean isError =false;
                    if (data.getRow0().equals(endSchool)) {
                        if(ObjectUtil.isEmpty(data.getRow1())){
                            highestMistake.append("毕业学校不为空，");
                            isError =true;
                        }
                        if(ObjectUtil.isEmpty(data.getRow6())){
                            highestMistake.append("所学专业不为空，");
                            isError =true;
                        }
                        String school = data.getRow1();
                        String major = data.getRow6();
                        highestDegreeRecord.setGraduationSchool(school);
                        highestDegreeRecord.setMajorsStudied(major);
                        excelDTO.setHighestDegree(highestDegreeRecord);
                        if(!isError){
                            successLines++;
                            excelDTO.setSuccessLines(successLines);
                        }
                    }
                    if(ObjectUtil.isNotEmpty(highestMistake)){
                        excelDTO.setHighestMistake(highestMistake.toString());
                    }
                    if(isError){
                        errorLines++;
                        excelDTO.setErrorLines(errorLines);
                    }
                }
                if (isPassTitle == 3) {
                    if (data.getRow0().equals(jobTime)) {
                        isPassJobTime = true;
                    }
                    if (isPassJobTime) {
                        if (passJobTimeNumber > 1) {
                            if(ObjectUtil.isEmpty(data.getRow0())){
                                changeMistake.append("上岗时间不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow1())){
                                changeMistake.append("上岗部门不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow4())){
                                changeMistake.append("上岗岗位不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow7())){
                                changeMistake.append("上岗成绩不为空，");
                            }
                            JobChangeRecordExcelDTO changeRecord = new JobChangeRecordExcelDTO();
                            String goJobTime = data.getRow0();
                            String jobDepartName = data.getRow1();
                            String jobName = data.getRow4();
                            String jobGrade = data.getRow7();
                            changeRecord.setExcelJobTime(goJobTime);
                            changeRecord.setJobGradeName(jobGrade);
                            changeRecord.setJobName(jobName);
                            changeRecord.setDepartName(jobDepartName);
                            changeRecords.add(changeRecord);
                            excelDTO.setChangeRecordList(changeRecords);
                            if(ObjectUtil.isNotEmpty(changeMistake)){
                                excelDTO.setChangeMistake(changeMistake.toString());
                                errorLines++;
                                excelDTO.setErrorLines(errorLines);
                            }else {
                                    successLines++;
                                    excelDTO.setSuccessLines(successLines);
                            }
                        }
                        passJobTimeNumber = passJobTimeNumber + 1;
                    }
                }
                if (isPassTitle == 4) {
                    if (data.getRow0().equals(trainTime)) {
                        isPassTrainTime = true;
                    }
                    if (isPassTrainTime) {
                        if (passTrainTimeNumber > 1) {
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("培训时间不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("培训内容不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("培训分级不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("课时不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("考核成绩不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("是否为计划内不为空，");
                            }
                            if(ObjectUtil.isEmpty(data.getRow6())){
                                recordMistake.append("记录编号不为空，");
                            }
                            String excelTrainTime = data.getRow0();
                            String trainContent = data.getRow1();
                            String trainGrade = data.getRow3();
                            String hour = data.getRow4();
                            String checkGrade = data.getRow5();
                            String isPlan = data.getRow6();
                            String taskCode = data.getRow7();
                            RecordExcelDTO record = new RecordExcelDTO();
                            record.setExcelTrainTime(excelTrainTime);
                            record.setTrainContent(trainContent);
                            record.setTaskGradeName(trainGrade);
                            record.setCheckGrade(checkGrade);
                            record.setExcelHour(hour);
                            record.setIsAnnualPlanName(isPlan);
                            record.setTaskCode(taskCode);
                            trainRecords.add(record);
                            excelDTO.setTrainRecordList(trainRecords);
                            if(ObjectUtil.isNotEmpty(recordMistake)){
                                excelDTO.setRecordMistake(recordMistake.toString());
                                errorLines++;
                                excelDTO.setErrorLines(errorLines);
                            }else {
                                successLines++;
                                excelDTO.setSuccessLines(successLines);
                            }
                        }
                        passTrainTimeNumber = passTrainTimeNumber + 1;
                    }
                }

            }
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
