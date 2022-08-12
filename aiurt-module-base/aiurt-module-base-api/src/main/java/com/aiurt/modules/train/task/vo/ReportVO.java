package com.aiurt.modules.train.task.vo;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * Administrator
 * 2022/4/22
 * 报表实体
 */
@Data
public class ReportVO implements Serializable {
    @ApiModelProperty(value = "培训任务id")
    private String trainTaskId;
    @ApiModelProperty(value = "培训子计划id")
    private String planSubId;
    @Excel(name = "部门", width = 15)
    @ApiModelProperty(value = "部门")
    private String sysOrgCode;
    @ApiModelProperty(value = "部门")
    private String taskTeamId;
    @Excel(name = "培训时间", width = 15)
    @ApiModelProperty(value = "培训时间")
    private String trainTime;
    @Excel(name = "培训内容", width = 15)
    @ApiModelProperty(value = "培训内容")
    private String planSubName;
    @Excel(name = "培训课时", width = 15)
    @ApiModelProperty(value = "培训课时")
    private String taskHours;
    @Excel(name = "培训讲师", width = 15)
    @ApiModelProperty(value = "培训讲师")
    private String teacher;
    @Excel(name = "培训对象", width = 15)
    @ApiModelProperty(value = "培训对象")
    private String trainTarget;
    @Excel(name = "应到人数", width = 15)
    @ApiModelProperty(value = "应到人数")
    private Integer inComeNum;
    @Excel(name = "实到人数", width = 15)
    @ApiModelProperty(value = "实到人数")
    private Integer reallyComNum;
    @Excel(name = "培训出勤率", width = 15)
    @ApiModelProperty(value = "培训出勤率")
    private String trainRate;
    @Excel(name = "有无考核", width = 15,dicCode = "examStatus_type")
    @ApiModelProperty(value = "有无考核")
    @Dict(dicCode = "examStatus_type")
    private Integer examStatus;
    @Excel(name = "考核及格率", width = 15)
    @ApiModelProperty(value = "考核及格率")
    private String examPassRate;
    @Excel(name = "完成情况", width = 15,dicCode = "train_task_state")
    @ApiModelProperty(value = "完成情况")
    @Dict(dicCode = "train_task_state")
    private Integer taskState;
    @Excel(name = "是否为计划内", width = 15)
    @ApiModelProperty(value = "是否为计划内")
    private String isPlan;
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
}
