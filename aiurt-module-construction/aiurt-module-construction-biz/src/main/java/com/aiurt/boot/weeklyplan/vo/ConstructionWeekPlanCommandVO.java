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
    /**计划令编号*/
    @Excel(name = "计划令编号", width = 15)
    @ApiModelProperty(value = "计划令编号")
    private String code;
}
