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
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.read.listener.ReadListener;
import liquibase.pro.packaged.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-16 17:34
 */
public class RecordExcelListener implements ReadListener<EmergencyPlanImportExcelDTO> {
    private EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO;

    public RecordExcelListener(EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO) {
        this.emergencyPlanImportExcelDTO = emergencyPlanImportExcelDTO;
    }

    @Override
    public void invoke(EmergencyPlanImportExcelDTO data, AnalysisContext context) {
        int sheetNo =context.readSheetHolder().getSheetNo();
        List<EmergencyPlanDisposalProcedureImportExcelDTO> disposalProcedureList = new ArrayList<>();

        String end1 = "应急预案处置程序";
        String end2 = "应急物资清单";
         if(sheetNo==0){
             //获取行的索引
             int index = context.readRowHolder().getRowIndex();
             //获取改行的map数据
             Map<Integer, Cell>map = context.readRowHolder().getCellMap();

             if(index == 3){
                 String emergencyPlanType = map.get(2).toString();
                 String emergencyPlanName = map.get(5).toString();
                 data.setEmergencyPlanType(emergencyPlanType);
                 data.setEmergencyPlanName(emergencyPlanName);
             }
             else if(index ==4){
                 String emergencyTeamName = map.get(2).toString();
                 data.setEmergencyTeamId(emergencyTeamName);
             }
             else if(index ==5){
                 String keyWord = map.get(2).toString();
                 data.setKeyWord(keyWord);
             }
             else if(index >6 && !map.get(1).toString().equals(end1)){
                 String emergencyPlanContent = map.get(2).toString();
                 data.setEmergencyPlanContent(emergencyPlanContent);
             }
             else if(index == 10){
                 EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO = new EmergencyPlanDisposalProcedureImportExcelDTO();
                 String sort = map.get(1).toString();
                 String orgName = map.get(2).toString();

             }



         }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
