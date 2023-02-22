package com.aiurt.modules.sysfile.entity;

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

import java.util.Date;

/**
 * @Description: 文件类型表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Data
@TableName("sys_file_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_file_type对象", description = "文件类型表")
public class SysFileType {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;
	/**
	 * 分类名称
	 */
	@Excel(name = "分类名称", width = 15)
	@ApiModelProperty(value = "分类名称")
	private String name;
	/**
	 * 等级
	 */
	@Excel(name = "等级", width = 15)
	@ApiModelProperty(value = "等级")
	private Integer grade;
	/**
	 * 父级
	 */
	@Excel(name = "父级", width = 15)
	@ApiModelProperty(value = "父级")
	private Long parentId;
	/**
	 * 删除状态
	 */
	@Excel(name = "删除状态", width = 15)
	@ApiModelProperty(value = "删除状态")
	private Integer delFlag;
	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;
	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;
	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**
	 * 修改时间
	 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;

	@ApiModelProperty(value = "是否可编辑，1：是，0：否")
	@TableField(exist = false)
	private Integer edit;

	@ApiModelProperty(value = "是否可删除，1：是，0：否")
	@TableField(exist = false)
	private Integer delete;

	@ApiModelProperty(value = "是否可上传，1：是，0：否")
	@TableField(exist = false)
	private Integer upload;
	@TableField(exist = false)
	private Long value;
	@TableField(exist = false)
	private String label;
}
