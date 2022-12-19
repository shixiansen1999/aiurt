package com.aiurt.boot.team.listener;

import com.aiurt.boot.team.model.ProcessRecordModel;
import com.aiurt.boot.team.model.RecordModel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lkj
 */
@Data
public class RecordExcelListener extends AnalysisEventListener<RecordModel> {

    private RecordModel recordModel = new RecordModel();

    @Override
    public void invoke(RecordModel data, AnalysisContext context) {

        int sheetNo = context.readSheetHolder().getSheetNo();

        List<ProcessRecordModel> processRecordModels = new ArrayList<>();

        String end = "训练效果及建议";
        if (sheetNo == 0) {
            // 获取行的索引
            int index = context.readRowHolder().getRowIndex();
            // 获取该行的map数据
            Map<Integer, Cell> map = context.readRowHolder().getCellMap();

            if (index == 3) {
                String trainingTime = map.get(2).toString();
                String position = map.get(6).toString();
                recordModel.setTrainingTime(trainingTime);
                recordModel.setPosition(position);
            } else if (index == 4) {
                String emergencyTeam = map.get(2).toString();
                String trainees = map.get(6).toString();
                recordModel.setEmergencyTeam(emergencyTeam);
                recordModel.setTrainees(trainees);
            } else if (index == 5) {
                String emergencyTrainingProgram = map.get(2).toString();
                recordModel.setEmergencyTrainingProgram(emergencyTrainingProgram);
            } else if (index > 6 && !map.get(1).toString().equals(end)) {
                ProcessRecordModel processRecordModel = new ProcessRecordModel();
                String sort = map.get(2).toString();
                String trainingTime = map.get(3).toString();
                String trainingContent = map.get(4).toString();
                processRecordModel.setSort(sort);
                processRecordModel.setTrainingTime(trainingTime);
                processRecordModel.setTrainingContent(trainingContent);
                processRecordModels.add(processRecordModel);
            } else if (map.get(1).toString().equals(end)){
                String trainingAppraise = map.get(2).toString();
                recordModel.setTrainingAppraise(trainingAppraise);
                recordModel.setProcessRecordModelList(processRecordModels);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
