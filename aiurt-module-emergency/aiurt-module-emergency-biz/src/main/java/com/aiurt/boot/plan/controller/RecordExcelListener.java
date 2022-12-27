package com.aiurt.boot.plan.controller;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/16
 * @time: 17:34
 */


import com.aiurt.boot.plan.dto.EmergencyPlanDisposalProcedureImportExcelDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanExcelDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanImportExcelDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsImportExcelDTO;
import com.aiurt.boot.team.entity.RecordData;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.read.listener.ReadListener;
import liquibase.pro.packaged.L;
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
public class RecordExcelListener extends AnalysisEventListener<RecordData> {

    private EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO = new EmergencyPlanImportExcelDTO();
    private  List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = new ArrayList<>();
    private  List<EmergencyPlanMaterialsImportExcelDTO> planMaterialsList = new ArrayList<>();
    @Override
    public void invoke(RecordData data, AnalysisContext context) {
        int sheetNo = context.readSheetHolder().getSheetNo();

        Integer rowNumber = context.readSheetHolder().getApproximateTotalRowNumber();

        String end1 = "应急预案处置程序";
        String end2 = "应急物资清单";

       if(sheetNo == 0){
           //获取行的索引
           int index = context.readRowHolder().getRowIndex();
           //获取改行的map数据
           Map<Integer, Cell>map = context.readRowHolder().getCellMap();

           if(index == 3){
               String emergencyPlanType = data.getRow2();
               String emergencyPlanName = data.getRow5();
               emergencyPlanImportExcelDTO.setEmergencyPlanType(emergencyPlanType);
               emergencyPlanImportExcelDTO.setEmergencyPlanName(emergencyPlanName);
           }
           else if(index ==4){
               String emergencyTeamName = data.getRow2();
               emergencyPlanImportExcelDTO.setEmergencyTeamId(emergencyTeamName);
           }
           else if(index ==5){
               String keyWord = data.getRow2();
               emergencyPlanImportExcelDTO.setKeyWord(keyWord);
           }
           else if(index >6 && index<8){
               String emergencyPlanContent = data.getRow2();
               emergencyPlanImportExcelDTO.setEmergencyPlanContent(emergencyPlanContent);
           }
           else if(index == 10){
               EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO = new EmergencyPlanDisposalProcedureImportExcelDTO();
               String orgName = data.getRow2();
               String roleName = data.getRow3();
               String disposalProcedureContent = data.getRow4();
               emergencyPlanDisposalProcedureImportExcelDTO.setOrgName(orgName);
               emergencyPlanDisposalProcedureImportExcelDTO.setRoleName(roleName);
               emergencyPlanDisposalProcedureImportExcelDTO.setDisposalProcedureContent(disposalProcedureContent);
               planDisposalProcedureList.add(emergencyPlanDisposalProcedureImportExcelDTO);

           }
           else if(index ==16){
               EmergencyPlanMaterialsImportExcelDTO emergencyPlanMaterialsImportExcelDTO = new EmergencyPlanMaterialsImportExcelDTO();
               String categoryName = data.getRow2();
               String materialsCode = data.getRow3();
               String materialsName = data.getRow4();
               String materialsNumber = data.getRow5();
               String unit = data.getRow6();
               emergencyPlanMaterialsImportExcelDTO.setCategoryName(categoryName);
               emergencyPlanMaterialsImportExcelDTO.setMaterialsCode(materialsCode);
               emergencyPlanMaterialsImportExcelDTO.setMaterialsName(materialsName);
               emergencyPlanMaterialsImportExcelDTO.setMaterialsNumber(materialsNumber);
               emergencyPlanMaterialsImportExcelDTO.setUnit(unit);
               planMaterialsList.add(emergencyPlanMaterialsImportExcelDTO);

           }
       }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
