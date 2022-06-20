package com.aiurt.boot.module.standard;

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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description: 检修标准管理
 * @Author: aiurt
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("inspection_code")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="inspection_code对象", description="检修标准管理")
public class InspectionCode {

	/**主键ID*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
	private Integer id;
	/**标题*/
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
	@NotNull(message = "标题不能为空")
	@Size(max = 100, message = "标题长度要求1到100之间")
	private String title;
	/**1-控制中心 2-车辆段 3-班组*/
	@Excel(name = "类型(1-控制中心 2-车辆段 3-班组)", width = 15)
    @ApiModelProperty(value = "类型(1-控制中心 2-车辆段 3-班组)")
	@NotNull(message = "类型不能为空")
	@Min(value = 1, message = "最小值为1")
	@Max(value = 3, message = "最大值为3")
	private Integer type;
	/**0-未生效 1-已生效*/
	@Excel(name = "状态(0-未生效 1-已生效)", width = 15)
    @ApiModelProperty(value = "状态(0-未生效 1-已生效)")
	@NotNull(message = "状态不能为空")
	private Integer status;
	/**年份*/
	@Excel(name = "年份", width = 15)
    @ApiModelProperty(value = "年份")
	@NotNull(message = "年份不能为空")
	private String years;
	/**适用组织id集合*/
	@Excel(name = "适用组织id集合", width = 15)
    @ApiModelProperty(value = "适用组织id集合")
	@NotNull(message = "适用站点不能为空")
	private String organizationIds;

	@TableField(exist = false)
	@ApiModelProperty(value = "适用班组")
	private String teamNames;

	/**生成年检计划状态 0-未生成 1-已生成*/
	@Excel(name = "生成年检计划状态 0-未生成 1-已生成", width = 15)
    @ApiModelProperty(value = "生成年检计划状态 0-未生成 1-已生成")
	private Integer generateStatus;
	/**删除状态 0.未删除 1已删除*/
    @ApiModelProperty(value = "删除状态 0.未删除 1已删除")
	private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
}
