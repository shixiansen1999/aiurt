package com.aiurt.boot.plan.controller;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/16
 * @time: 17:34
 */


import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.RecordData;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-16 17:34
 */
@Data
public class PlanRecordExcelListener extends AnalysisEventListener<RecordData> {

    private EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO = new EmergencyPlanRecordImportExcelDTO();
    private  List<EmergencyPlanRecordDisposalProcedureImportExcelDTO> planRecordDisposalProcedureList = new ArrayList<>();
    private  List<EmergencyPlanRecordMaterialsImportExcelDTO> planRecordMaterialsList = new ArrayList<>();
    private  List<EmergencyPlanRecordProblemMeasuresImportExcelDTO> ProblemMeasuresList = new ArrayList<>();


    private boolean flag1 = true;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private int start = 0;
    private int begin = 0;
    @Override
    public void invoke(RecordData data, AnalysisContext context) {
        int sheetNo = context.readSheetHolder().getSheetNo();

        Integer rowNumber = context.readSheetHolder().getApproximateTotalRowNumber();

        String end1 = "备注：应急处置程序可在此次处向上插入行";
        String end2 = "备注：应急物资清单可在此次处向上插入行";
        String end3 = "备注：问题及措施可在此次处向上插入行";


       if(sheetNo == 0){
           //获取行的索引
           int index = context.readRowHolder().getRowIndex();
           //获取改行的map数据
           Map<Integer, Cell>map = context.readRowHolder().getCellMap();

           if (index > 9) {
               if (data.getRow0().equals(end1)) {
                   flag1 = false;
                   start = index;
               }
               if (index>(start + 2)) {
                   if(index == start+3){
                       flag2 = true;
                   }
                   if (data.getRow0().equals(end2)) {
                       flag2 = false;
                       begin = index;
                   }
                   if(index>(begin+2)){
                       if(index ==begin+3){
                           flag3 = true;
                       }
                       if(data.getRow0().equals(end3)){
                           flag3 = false;
                       }
                   }
               }
           }

           if(index == 2){
               String eventClass = data.getRow1();
               String eventProperty = data.getRow4();
               emergencyPlanRecordImportExcelDTO.setEventClass(eventClass);
               emergencyPlanRecordImportExcelDTO.setEventProperty(eventProperty);
           }
           else if(index ==3){
               String emergencyPlanName = data.getRow1();
               String emergencyPlanVersion = data.getRow4();
               emergencyPlanRecordImportExcelDTO.setEmergencyPlanId(emergencyPlanName);
               emergencyPlanRecordImportExcelDTO.setEmergencyPlanVersion(emergencyPlanVersion);
           }
           else if(index ==4){
                String startTime = data.getRow1();
               String emergencyPlanRecordDepartId = data.getRow4();
               emergencyPlanRecordImportExcelDTO.setStarttime(startTime);
               emergencyPlanRecordImportExcelDTO.setEmergencyPlanRecordDepartId(emergencyPlanRecordDepartId);
           }
           else if(index ==5){
               String emergencyTeamId = data.getRow1();
               emergencyPlanRecordImportExcelDTO.setEmergencyTeamId(emergencyTeamId);
           }
           else if(index == 6){
               String advice = data.getRow1();
               emergencyPlanRecordImportExcelDTO.setAdvice(advice);
           }
           else if(index > 8 && flag1){
               EmergencyPlanRecordDisposalProcedureImportExcelDTO emergencyPlanRecordDisposalProcedureImportExcelDTO = new EmergencyPlanRecordDisposalProcedureImportExcelDTO();
               String orgName = data.getRow1();
               String roleName = data.getRow2();
               String disposalProcedureContent = data.getRow3();
               String disposalProcedureSituation = data.getRow4();
               emergencyPlanRecordDisposalProcedureImportExcelDTO.setOrgName(orgName);
               emergencyPlanRecordDisposalProcedureImportExcelDTO.setRoleName(roleName);
               emergencyPlanRecordDisposalProcedureImportExcelDTO.setDisposalProcedureContent(disposalProcedureContent);
               emergencyPlanRecordDisposalProcedureImportExcelDTO.setDisposalProcedureSituation(disposalProcedureSituation);
               planRecordDisposalProcedureList.add(emergencyPlanRecordDisposalProcedureImportExcelDTO);
               emergencyPlanRecordImportExcelDTO.setPlanRecordDisposalProcedureList(planRecordDisposalProcedureList);
           }
           else if (index > (start + 2) && flag2) {
               EmergencyPlanRecordMaterialsImportExcelDTO emergencyPlanRecordMaterialsImportExcelDTO = new EmergencyPlanRecordMaterialsImportExcelDTO();
               String categoryName = data.getRow1();
               String materialsCode = data.getRow2();
               String materialsName = data.getRow3();
               String materialsNumber = data.getRow4();
               String unit = data.getRow5();
               emergencyPlanRecordMaterialsImportExcelDTO.setCategoryName(categoryName);
               emergencyPlanRecordMaterialsImportExcelDTO.setMaterialsCode(materialsCode);
               emergencyPlanRecordMaterialsImportExcelDTO.setMaterialsName(materialsName);
               emergencyPlanRecordMaterialsImportExcelDTO.setMaterialsNumber(materialsNumber);
               emergencyPlanRecordMaterialsImportExcelDTO.setUnit(unit);
               planRecordMaterialsList.add(emergencyPlanRecordMaterialsImportExcelDTO);
               emergencyPlanRecordImportExcelDTO.setPlanRecordMaterialsList(planRecordMaterialsList);
           }
           else if (index > (begin + 2) && flag3) {
               EmergencyPlanRecordProblemMeasuresImportExcelDTO emergencyPlanRecordProblemMeasuresImportExcelDTO = new EmergencyPlanRecordProblemMeasuresImportExcelDTO();
               String problemType = data.getRow1();
               String problemContent = data.getRow2();
               String orgCode = data.getRow3();
               String orgUserId = data.getRow4();
               String managerId = data.getRow5();
               String resolveTime = data.getRow6();
               String status = data.getRow7();
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setProblemType(problemType);
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setProblemContent(problemContent);
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setOrgCode(orgCode);
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setOrgUserId(orgUserId);
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setManagerId(managerId);
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setResolveTime(resolveTime);
               emergencyPlanRecordProblemMeasuresImportExcelDTO.setStatus(status);
               ProblemMeasuresList.add(emergencyPlanRecordProblemMeasuresImportExcelDTO);
               emergencyPlanRecordImportExcelDTO.setProblemMeasuresList(ProblemMeasuresList);
           }
       }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
