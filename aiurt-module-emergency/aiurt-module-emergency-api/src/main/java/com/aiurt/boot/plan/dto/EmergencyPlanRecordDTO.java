package com.aiurt.boot.plan.dto;

import com.aiurt.boot.plan.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyPlanRecordDTO extends EmergencyPlanRecord {
    /**应急预案启动记录应急队伍*/
    @ApiModelProperty(value = "应急预案启动记录应急救援队伍")
    List<String> emergencyPlanRecordTeamId;

    /**应急预案启动记录参与部门*/
    @ApiModelProperty(value = "应急预案启动记录参与部门")
    List<String> emergencyPlanRecordDepartId;

    /**
     * 应急预案启动记录处置程序
     */
    @ApiModelProperty(value = "应急预案启动记录处置程序")
    private List<EmergencyPlanRecordDisposalProcedure> emergencyPlanRecordDisposalProcedureList;

    /**
     * 应急预案启动记录应急物资
     */
    @ApiModelProperty(value = "应急预案启动记录应急物资")
    private List<EmergencyPlanRecordMaterials> emergencyPlanRecordMaterialsList;

    /**
     * 应急预案启动记录附件
     */
    @ApiModelProperty(value = "应急预案启动记录事件材料附件")
    private List<EmergencyPlanRecordAtt> emergencyPlanRecordAttList;

    /**
     * 应急预案启动记录问题措施
     */
    @ApiModelProperty(value = "应急预案启动记录问题措施表")
    private List<EmergencyPlanRecordProblemMeasures> emergencyPlanRecordProblemMeasuresList;


}
