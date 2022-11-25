package com.aiurt.boot.weeklyplan.vo;

import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 前端信息渲染对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ConstructionWeekPlanCommandVO对象", description="ConstructionWeekPlanCommandVO对象")
public class ConstructionWeekPlanCommandVO  extends ConstructionWeekPlanCommand {

    private static final long serialVersionUID = -8071884430619763636L;

    @ApiModelProperty(value = "实例id")
    private String processInstanceId;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "模板key，流程标识")
    private String modelKey;

}
