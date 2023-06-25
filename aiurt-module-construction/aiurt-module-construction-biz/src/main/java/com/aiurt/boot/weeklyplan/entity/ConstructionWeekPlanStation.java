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
@TableName("construction_week_plan_Station")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="construction_week_plan_Station对象", description="construction_week_plan_Station")
public class ConstructionWeekPlanStation extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**站点编号*/
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    @TableField(value = "`station_code`")
    private String stationCode;
    /**站点编号*/
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    @TableField(value = "`station_name`")
    private String stationName;
}
