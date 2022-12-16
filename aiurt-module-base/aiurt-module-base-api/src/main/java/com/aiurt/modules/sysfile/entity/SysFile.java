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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Description: 文件表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Data
@TableName("sys_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_file对象", description = "文件表")
public class SysFile {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;
	/**
	 * 类型id
	 */
	@Excel(name = "类型id", width = 15)
	@ApiModelProperty(value = "类型id")
	@NotNull(message = "分类id不能为空")
	private Long typeId;
	/**
	 * 文件名称
	 */
	@Excel(name = "文件名称", width = 15)
	@ApiModelProperty(value = "文件名称")
	private String name;
	/**
	 * 文件url
	 */
	@Excel(name = "文件url", width = 15)
	@ApiModelProperty(value = "文件url")
	@NotBlank(message = "文件url不能为空")
	private String url;
	/**
	 * 类型
	 */
	@Excel(name = "类型", width = 15)
	@ApiModelProperty(value = "类型")
	private String type;
	/**
	 * 大小
	 */
	@Excel(name = "大小", width = 15)
	@ApiModelProperty(value = "大小")
	private String fileSize;
	/**
	 * 下载状态
	 */
	@Excel(name = "下载状态", width = 15)
	@ApiModelProperty(value = "下载状态")
	private Integer downStatus;
	/**
	 * 编辑状态
	 */
	@Excel(name = "编辑状态", width = 15)
	@ApiModelProperty(value = "编辑状态")
	private Integer editStatus;
	/**
	 * 下载次数
	 */
	@Excel(name = "下载次数", width = 15)
	@ApiModelProperty(value = "下载次数")
	private Integer downSize;
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


	@ApiModelProperty("可查看人员")
	@TableField(exist = false)
	@NotEmpty(message = "小主，请添加可查看人员哦！")
	private List<String> lookIds;

	@ApiModelProperty("可编辑人员")
	@TableField(exist = false)
	@NotEmpty(message = "小主，请添加可编辑人员哦！")
	private List<String> editIds;

	@ApiModelProperty("可上传人员")
	@TableField(exist = false)
	@NotEmpty(message = "小主，请添加可上传人员哦！")
	private List<String> uploads;

	@ApiModelProperty("可下载人员")
	@TableField(exist = false)
	@NotEmpty(message = "小主，请添加可下载人员哦！")
	private List<String> downloads;

	@ApiModelProperty("可删除人员")
	@TableField(exist = false)
	@NotEmpty(message = "小主，请添加可删除人员哦！")
	private List<String> deletes;

	@ApiModelProperty("可在线编辑人员")
	@TableField(exist = false)
	@NotEmpty(message = "小主，请添加可在线编辑人员哦！")
	private List<String> onlineEditing;


	public static final String TYPE = "type";
	public static final String DEL_FLAG = "del_flag";
	public static final String TYPE_ID = "type_id";
}
