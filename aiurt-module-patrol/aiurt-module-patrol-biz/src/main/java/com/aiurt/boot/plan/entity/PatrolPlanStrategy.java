package com.aiurt.boot.plan.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
	@Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    private java.lang.String planId;
	/**巡检类型：1周巡、2月巡*/
	@Excel(name = "巡检类型：1周巡、2月巡", width = 15)
    @ApiModelProperty(value = "巡检类型：1周巡、2月巡")
    private java.lang.Integer type;
	/**巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日*/
	@Excel(name = "巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日", width = 15)
    @ApiModelProperty(value = "巡检星期：1星期一、2星期二、3星期三、4星期四、5星期五、6星期六、7星期日")
    private java.lang.Integer week;
	/**巡检周次：1第一周、2第二周、3第三周、4第四周*/
	@Excel(name = "巡检周次：1第一周、2第二周、3第三周、4第四周", width = 15)
    @ApiModelProperty(value = "巡检周次：1第一周、2第二周、3第三周、4第四周")
    private java.lang.Integer time;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
