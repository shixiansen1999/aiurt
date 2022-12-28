package com.aiurt.boot.team.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * @author lkj
 */
@Data
public class TrainingProgramModel implements Serializable {

    /**训练项目名称*/
    @Excel(name = "训练项目", width = 15)
    @ApiModelProperty(value = "训练项目")
    private String trainingProgramName;
    /**训练项目名称*/
    @Excel(name = "训练队伍", width = 15)
    @ApiModelProperty(value = "训练队伍")
    private String trainingTeam;
    /**计划训练时间*/
    @Excel(name = "计划训练时间", width = 15)
    @ApiModelProperty(value = "计划训练时间")
    private String trainingPlanTime;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**队伍id*/
    private List<String> trainingTeamId;
    /**队伍人数*/
    private Integer peopleNum;

    private String mistake;
}
