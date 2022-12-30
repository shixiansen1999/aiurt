package com.aiurt.boot.team.model;

import com.aiurt.boot.team.entity.EmergencyTrainingRecordCrew;
import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lkj
 */
@Data
public class RecordModel implements Serializable {
    /**训练计划编号*/
    private String trainingProgramCode;
    /**
     * 训练科目
     */
    private String emergencyTrainingProgram;
    /**
     * 应急队伍名称
     */
    private String emergencyTeam;
    /**
     * 训练时间
     */
    private String trainingTime;
    /**
     * 参加训练人员
     */
    private String trainees;
    /**
     * 训练地点
     */
    private String position;
    /**
     * 训练效果评估及改进建议
     */
    private String trainingAppraise;

    private String mistake;

    /**训练过程记录*/
    @ExcelIgnore
    List<ProcessRecordModel> processRecordModelList;
    /**训练人员*/
    @ExcelIgnore
    private List<EmergencyTrainingRecordCrew> crewList;
    /**线路编码*/
    @ExcelIgnore
    private String lineCode;
    /**站点编码*/
    @ExcelIgnore
    private String stationCode;
    /**位置编码*/
    @ExcelIgnore
    private String positionCode;
    /**训练人数*/
    @ExcelIgnore
    private Integer traineesNum;
    /**训练计划id*/
    @ExcelIgnore
    private String emergencyTrainingProgramId;
    /**应急队伍id*/
    @ExcelIgnore
    private String emergencyTeamId;
}
