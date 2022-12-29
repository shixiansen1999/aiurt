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
import com.aiurt.boot.plan.entity.RecordData;
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

    private boolean flag1 = true;
    private boolean flag2 = false;
    private int start = 0;
    @Override
    public void invoke(RecordData data, AnalysisContext context) {
        int sheetNo = context.readSheetHolder().getSheetNo();

        Integer rowNumber = context.readSheetHolder().getApproximateTotalRowNumber();

        String end1 = "备注：应急处置程序可在此次处向上插入行";
        String end2 = "备注：应急物资清单可在此次处向上插入行";


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
                   }
               }
           }

           if(index == 2){
               String emergencyPlanType = data.getRow1();
               String emergencyPlanName = data.getRow4();
               emergencyPlanImportExcelDTO.setEmergencyPlanType(emergencyPlanType);
               emergencyPlanImportExcelDTO.setEmergencyPlanName(emergencyPlanName);
           }
           else if(index ==3){
               String emergencyTeamName = data.getRow1();
               emergencyPlanImportExcelDTO.setEmergencyTeamId(emergencyTeamName);
           }
           else if(index ==4){
               String keyWord = data.getRow1();
               emergencyPlanImportExcelDTO.setKeyWord(keyWord);
           }
           else if(index >=5 && index<=7){
               String emergencyPlanContent = data.getRow1();
               emergencyPlanImportExcelDTO.setEmergencyPlanContent(emergencyPlanContent);
           }
           else if(index > 9 && flag1){
               EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO = new EmergencyPlanDisposalProcedureImportExcelDTO();
               String orgName = data.getRow1();
               String roleName = data.getRow2();
               String disposalProcedureContent = data.getRow3();
               emergencyPlanDisposalProcedureImportExcelDTO.setOrgName(orgName);
               emergencyPlanDisposalProcedureImportExcelDTO.setRoleName(roleName);
               emergencyPlanDisposalProcedureImportExcelDTO.setDisposalProcedureContent(disposalProcedureContent);
               planDisposalProcedureList.add(emergencyPlanDisposalProcedureImportExcelDTO);
               emergencyPlanImportExcelDTO.setPlanDisposalProcedureList(planDisposalProcedureList);
           } else if (index > (start + 2) && flag2) {
               EmergencyPlanMaterialsImportExcelDTO emergencyPlanMaterialsImportExcelDTO = new EmergencyPlanMaterialsImportExcelDTO();
               String categoryName = data.getRow1();
               String materialsCode = data.getRow2();
               String materialsName = data.getRow3();
               String materialsNumber = data.getRow4();
               String unit = data.getRow5();
               emergencyPlanMaterialsImportExcelDTO.setCategoryName(categoryName);
               emergencyPlanMaterialsImportExcelDTO.setMaterialsCode(materialsCode);
               emergencyPlanMaterialsImportExcelDTO.setMaterialsName(materialsName);
               emergencyPlanMaterialsImportExcelDTO.setMaterialsNumber(materialsNumber);
               emergencyPlanMaterialsImportExcelDTO.setUnit(unit);
               planMaterialsList.add(emergencyPlanMaterialsImportExcelDTO);
               emergencyPlanImportExcelDTO.setPlanMaterialsList(planMaterialsList);
           }
       }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
