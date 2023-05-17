package com.aiurt.boot.plan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: patrol_plan_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("patrol_plan_strategy")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_plan_strategy对象", description="patrol_plan_strategy")
public class PatrolPlanStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**计划编号*/
	@Excel(name = "计划表主键", width = 15)
    @ApiModelProperty(value = "计划表主键")
    private java.lang.String planId;
	/**巡检类型：0天巡、1周巡、2月巡、3季巡*/
	@Excel(name = "巡检类型：0天巡、1周巡、2月巡、3季巡", width = 15)
    @ApiModelProperty(value = "巡检类型：0天巡、1周巡、2月巡、3季巡")
    private java.lang.Integer type;
	/**巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日*/
	@Excel(name = "巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日", width = 15)
    @ApiModelProperty(value = "巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日")
    private java.lang.Integer week;
	/**巡检周次：1第一周、2第二周、3第三周、4第四周、5第五周*/
	@Excel(name = "巡检周次：1第一周、2第二周、3第三周、4第四周、5第五周", width = 15)
    @ApiModelProperty(value = "巡检周次：1第一周、2第二周、3第三周、4第四周、5第五周")
    private java.lang.Integer time;
    /**巡检周次：开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm:ss")
    @DateTimeFormat(pattern="HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;
    /**巡检周次：结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm:ss")
    @DateTimeFormat(pattern="HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
