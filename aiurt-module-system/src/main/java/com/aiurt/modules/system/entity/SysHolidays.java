package com.aiurt.modules.system.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: sys_holidays
 * @Author: aiurt
 * @Date:   2023-03-16
 * @Version: V1.0
 */
@Data
@TableName("sys_holidays")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_holidays对象", description="sys_holidays")
public class SysHolidays extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**开始日期*/
    @NotNull(message = "开始日期不能为空")
	@Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    /**结束日期*/
    @NotNull(message = "结束日期不能为空")
    @Excel(name = "结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
    @ApiModelProperty(value = "日期范围")
    @TableField(exist = false)
    private String dateRange;
	/**节假日名称*/
    @NotNull(message = "节假日名称不能为空")
	@Excel(name = "节假日名称", width = 15)
    @ApiModelProperty(value = "节假日名称")
    private String name;
    /**类型*/
    @NotNull(message = "类型不能为空")
    @Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型：1调休，2补班")
    @Dict(dicCode = "holidays_type")
    private Integer type;
    /**导入错误原因*/
    @TableField(exist = false)
    @Excel(name = "导入错误原因", width = 15)
    @ApiModelProperty(value = "导入错误原因")
    private String mistake;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
