package com.aiurt.boot.team.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lkj
 */
@Data
public class RecordModel implements Serializable {
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

    /**训练过程记录*/
    @ExcelIgnore
    List<ProcessRecordModel> processRecordModelList;

}
