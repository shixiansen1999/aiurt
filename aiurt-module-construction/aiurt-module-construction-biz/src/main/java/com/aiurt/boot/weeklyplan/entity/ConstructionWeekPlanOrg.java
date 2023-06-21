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
@TableName("construction_week_plan_Org")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="construction_week_plan_Org对象", description="construction_week_plan_Org")
public class ConstructionWeekPlanOrg extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**站点编号*/
    @Excel(name = "组织机构编号", width = 15)
    @ApiModelProperty(value = "组织机构编号")
    @TableField(value = "`code`")
    private String code;
    /**站点编号*/
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    @TableField(value = "`name`")
    private String name;
}
