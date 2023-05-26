package com.aiurt.modules.workarea.entity;

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

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Data
@TableName("work_area")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="work_area对象", description="work_area")
public class WorkArea implements Serializable {
    private static final long serialVersionUID = 1L;

	/**工区信息表主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "工区信息表主键")
    private String id;
	/**工区编号*/
	@Excel(name = "工区编号", width = 15)
    @ApiModelProperty(value = "工区编号")
    @TableField(value = "`code`")
    private String code;
	/**工区名称*/
	@Excel(name = "工区名称", width = 15)
    @ApiModelProperty(value = "工区名称")
    @TableField(value = "`name`")
    private String name;
	/**工区类型：1运行工区、2检修工区*/
	@Excel(name = "工区类型：1运行工区、2检修工区", width = 15)
    @ApiModelProperty(value = "工区类型：1运行工区、2检修工区")
    @TableField(value = "`type`")
    private Integer type;
    /**工区站点code*/
    @Excel(name = "工区站点code", width = 15)
    @ApiModelProperty(value = "工区站点code")
	private String stationCode;
	/**工区地点*/
	@Excel(name = "工区地点", width = 15)
    @ApiModelProperty(value = "工区地点")
    @TableField(value = "`position`")
    private String position;
	/**专业编号*/
	@Excel(name = "专业编号", width = 15)
    @ApiModelProperty(value = "专业编号")
    private String majorCode;
	/**工区管理负责人ID*/
	@Excel(name = "工区管理负责人ID", width = 15)
    @ApiModelProperty(value = "工区管理负责人ID")
    private String managerId;
	/**工区技术负责人ID*/
	@Excel(name = "工区技术负责人ID", width = 15)
    @ApiModelProperty(value = "工区技术负责人ID")
    private String technicalId;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
