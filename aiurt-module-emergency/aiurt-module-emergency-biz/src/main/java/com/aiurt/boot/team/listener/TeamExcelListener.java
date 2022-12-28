package com.aiurt.boot.team.listener;

import com.aiurt.boot.team.entity.RecordData;
import com.aiurt.boot.team.model.CrewModel;
import com.aiurt.boot.team.model.TeamModel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lkj
 */
@Data
public class TeamExcelListener extends AnalysisEventListener<RecordData> {

    private TeamModel teamModel = new TeamModel();

    private List<CrewModel> crewList = new ArrayList<>();


    @Override
    public void invoke(RecordData data, AnalysisContext context) {

        int sheetNo = context.readSheetHolder().getSheetNo();

        Integer rowNumber = context.readSheetHolder().getApproximateTotalRowNumber();
        if (sheetNo == 0) {
            // 获取行的索引
            int index = context.readRowHolder().getRowIndex();
            // 获取该行的map数据
            if (index == 3) {
                teamModel.setMajorName(data.getRow0());
                teamModel.setOrgName(data.getRow1());
                teamModel.setEmergencyTeamname(data.getRow2());
                teamModel.setEmergencyTeamcode(data.getRow3());
                teamModel.setLineName(data.getRow4());
                teamModel.setStationName(data.getRow5());
                teamModel.setPositionName(data.getRow6());
                teamModel.setWorkAreaName(data.getRow7());
                teamModel.setManagerName(data.getRow8());
                teamModel.setManagerPhone(data.getRow9());
            } else if (index > 6 && index <= rowNumber) {
                CrewModel crewModel = new CrewModel();
                crewModel.setScheduleItem(data.getRow0());
                crewModel.setPostName(data.getRow1());
                crewModel.setRealName(data.getRow2());
                crewModel.setUserPhone(data.getRow3());
                crewModel.setRemark(data.getRow4());
                crewList.add(crewModel);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
