package com.aiurt.modules.sysfile.entity;

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

import java.util.Date;

/**
 * @Description: 文件权限表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Data
@TableName("sys_file_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_file_role对象", description = "文件权限表")
public class SysFileRole {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;
	/**
	 * 文件id
	 */
	@Excel(name = "文件id", width = 15)
	@ApiModelProperty(value = "文件id")
	private Long fileId;
	/**
	 * 类型id
	 */
	@Excel(name = "类型id", width = 15)
	@ApiModelProperty(value = "类型id")
	private Long typeId;
	/**
	 * 用户id
	 */
	@Excel(name = "用户id", width = 15)
	@ApiModelProperty(value = "用户id")
	private String userId;
	/**
	 * 原查看状态
	 */
	@Excel(name = "原查看状态", width = 15)
	@ApiModelProperty(value = "原查看状态")
	private Integer primaryLookStatus;
	/**
	 * 查看状态
	 */
	@Excel(name = "查看状态", width = 15)
	@ApiModelProperty(value = "查看状态")
	private Integer lookStatus;

	/**
	 * 原编辑状态
	 */
	@Excel(name = "原编辑状态", width = 15)
	@ApiModelProperty(value = "原编辑状态")
	private Integer primaryEditStatus;
	/**
	 * 编辑状态
	 */
	@Excel(name = "编辑状态", width = 15)
	@ApiModelProperty(value = "编辑状态")
	private Integer editStatus;
	/**
	 * 原上传状态
	 */
	@Excel(name = "原上传状态", width = 15)
	@ApiModelProperty(value = "原上传状态")
	private Integer primaryUploadStatus;
	/**
	 * 上传状态
	 */
	@Excel(name = "上传状态", width = 15)
	@ApiModelProperty(value = "上传状态")
	private Integer uploadStatus;

	/**
	 * 上传标记
	 */
	@Excel(name = "上传标记", width = 15)
	@ApiModelProperty(value = "上传标记")
	private Integer uploadTag;
	/**
	 * 原下载状态
	 */
	@Excel(name = "原下载状态", width = 15)
	@ApiModelProperty(value = "原下载状态")
	private Integer primaryDownloadStatus;
	/**
	 * 下载状态
	 */
	@Excel(name = "下载状态", width = 15)
	@ApiModelProperty(value = "下载状态")
	private Integer downloadStatus;
	/**
	 * 原删除状态
	 */
	@Excel(name = "原删除状态", width = 15)
	@ApiModelProperty(value = "原删除状态")
	private Integer primaryDeleteStatus;
	/**
	 * 删除状态
	 */
	@Excel(name = "删除状态", width = 15)
	@ApiModelProperty(value = "删除状态")
	private Integer deleteStatus;
	/**
	 * 原重命名状态
	 */
	@Excel(name = "原重命名状态", width = 15)
	@ApiModelProperty(value = "原重命名状态")
	private Integer primaryRenameStatus;
	/**
	 * 重命名状态
	 */
	@Excel(name = "重命名状态", width = 15)
	@ApiModelProperty(value = "重命名状态")
	private Integer renameStatus;
	/**
	 * 原在线编辑状态
	 */
	@Excel(name = "原在线编辑状态", width = 15)
	@ApiModelProperty(value = "原在线编辑状态")
	private Integer primaryOnlineEditing;
	/**
	 * 在线编辑状态
	 */
	@Excel(name = "在线编辑状态", width = 15)
	@ApiModelProperty(value = "在线编辑状态")
	private Integer onlineEditing;
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
}
