package com.aiurt.boot.team.listener;

import com.aiurt.boot.team.entity.RecordData;
import com.aiurt.boot.team.model.ProcessRecordModel;
import com.aiurt.boot.team.model.RecordModel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lkj
 */
@Data
public class RecordExcelListener extends AnalysisEventListener<RecordData> {

    private RecordModel recordModel = new RecordModel();
    private List<ProcessRecordModel> processRecordModels = new ArrayList<>();

    @Override
    public void invoke(RecordData data, AnalysisContext context) {

        int sheetNo = context.readSheetHolder().getSheetNo();

        Integer rowNumber = context.readSheetHolder().getApproximateTotalRowNumber();

        if (sheetNo == 0) {
            // 获取行的索引
            int index = context.readRowHolder().getRowIndex();
            // 获取该行的map数据
            if (index == 3) {
                String trainingTime = data.getRow2();
                String position = data.getRow6();
                recordModel.setTrainingTime(trainingTime);
                recordModel.setPosition(position);
            } else if (index == 4) {
                String emergencyTeam = data.getRow2();
                String trainees = data.getRow6();
                recordModel.setEmergencyTeam(emergencyTeam);
                recordModel.setTrainees(trainees);
            } else if (index == 5) {
                String emergencyTrainingProgram = data.getRow2();
                String trainingProgramCode = data.getRow6();
                recordModel.setEmergencyTrainingProgram(emergencyTrainingProgram);
                recordModel.setTrainingProgramCode(trainingProgramCode);
            } else if (index > 6 && index < rowNumber - 4) {
                ProcessRecordModel processRecordModel = new ProcessRecordModel();
                String sort = data.getRow2();
                String trainingTime = data.getRow3();
                String trainingContent = data.getRow4();
                processRecordModel.setSort(sort);
                processRecordModel.setTrainingTime(trainingTime);
                processRecordModel.setTrainingContent(trainingContent);
                processRecordModels.add(processRecordModel);
            } else if (index == rowNumber - 4) {
                String trainingAppraise = data.getRow2();
                recordModel.setTrainingAppraise(trainingAppraise);
                recordModel.setProcessRecordModelList(processRecordModels);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
