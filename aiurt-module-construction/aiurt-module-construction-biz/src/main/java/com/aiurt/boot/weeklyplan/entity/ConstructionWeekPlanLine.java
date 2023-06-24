package com.aiurt.boot.weeklyplan.entity;

import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Data
@TableName("construction_week_plan_Line")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="construction_week_plan_Line对象", description="construction_week_plan_Line")
public class ConstructionWeekPlanLine extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**站点编号*/
    @Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    @TableField(value = "`line_code`")
    private String lineCode;
    /**站点编号*/
    @Excel(name = "线路名称", width = 15)
    @ApiModelProperty(value = "线路名称")
    @TableField(value = "`line_name`")
    private String lineName;
}
