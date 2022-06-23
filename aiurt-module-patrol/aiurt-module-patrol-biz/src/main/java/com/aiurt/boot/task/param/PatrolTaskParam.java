package com.aiurt.boot.task.param;

import com.aiurt.boot.plan.entity.PatrolPlanStation;
import com.aiurt.boot.plan.param.PatrolPlanOrganizationParam;
import com.aiurt.boot.plan.param.PatrolPlanStationParam;
import com.aiurt.boot.task.entity.PatrolTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PatrolTaskParam extends PatrolTask {
    /**
     * 作业类型字典名称
     */
    @Excel(name = "作业类型字典名称", width = 15)
    @ApiModelProperty(value = "作业类型字典名称")
    private String typeName;
    /**
     * 是否委外字典名称
     */
    @Excel(name = "是否委外", width = 15)
    @ApiModelProperty(value = "是否委外")
    private String outsourceName;
    /**
     * 巡检频次字典名称
     */
    @Excel(name = "巡检频次字典名称", width = 15)
    @ApiModelProperty(value = "巡检频次字典名称")
    private String periodName;
    /**
     * 任务获取方式字典名称
     */
    @Excel(name = "任务获取方式字典名称", width = 15)
    @ApiModelProperty(value = "任务获取方式字典名称")
    private String sourceName;
    /**
     * 任务状态字典名称
     */
    @Excel(name = "任务状态字典名称", width = 15)
    @ApiModelProperty(value = "任务状态字典名称")
    private String statusName;
    /**
     * 异常状态字典名称
     */
    @Excel(name = "异常状态字典名称", width = 15)
    @ApiModelProperty(value = "异常状态字典名称")
    private String abnormalName;
    /**
     * 处置状态字典名称
     */
    @Excel(name = "处置状态字典名称", width = 15)
    @ApiModelProperty(value = "处置状态字典名称")
    private String disposeName;

    /**
     * 组织机构信息
     */
    @Excel(name = "组织机构信息", width = 15)
    @ApiModelProperty(value = "组织机构信息")
    private List<PatrolPlanOrganizationParam> departInfo;
    /**
     * 站点信息
     */
    @Excel(name = "站点信息", width = 15)
    @ApiModelProperty(value = "站点信息")
    private List<PatrolPlanStationParam> stationInfo;
}
