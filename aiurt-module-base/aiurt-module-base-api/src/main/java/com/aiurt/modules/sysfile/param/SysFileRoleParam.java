package com.aiurt.modules.sysfile.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @description: SysFileRoleParam
 * @author: Mr.zhao
 * @date: 2021/10/26 15:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileRoleParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("类型id")
	private Long typeId;

	@ApiModelProperty("类型id")
	private Long fileId;

	@ApiModelProperty("用户id")
	private String userId;

	@ApiModelProperty(value = "原查看状态 0:不允许 1:允许")
	private Integer primaryLookStatus;

	@ApiModelProperty("查看状态 0:不允许 1:允许")
	private Integer lookStatus;

	@ApiModelProperty(value = "原编辑状态 0:不允许 1:允许")
	private Integer primaryEditStatus;

	@ApiModelProperty("编辑状态 0:不允许 1:允许")
	private Integer editStatus;

	@ApiModelProperty(value = "原上传状态 0:不允许 1:允许")
	private Integer primaryUploadStatus;

	@ApiModelProperty("上传状态 0:不允许 1:允许")
	private Integer uploadStatus;

	@ApiModelProperty(value = "原下载状态 0:不允许 1:允许")
	private Integer primaryDownloadStatus;

	@ApiModelProperty("下载状态 0:不允许 1:允许")
	private Integer downloadStatus;

	@ApiModelProperty(value = "原删除状态 0:不允许 1:允许")
	private Integer primaryDeleteStatus;

	@ApiModelProperty("删除状态 0:不允许 1:允许")
	private Integer deleteStatus;

	@ApiModelProperty(value = "原重命名状态 0:不允许 1:允许")
	private Integer primaryRenameStatus;

	@ApiModelProperty("重命名状态 0:不允许 1:允许")
	private Integer renameStatus;

	@ApiModelProperty(value = "原在线编辑状态 0:不允许 1:允许")
	private Integer primaryOnlineEditing;

	@ApiModelProperty("在线编辑状态 0:不允许 1:允许")
	private Integer onlineEditing;

	@ApiModelProperty(value = "上传标记")
	private Integer uploadTag;
}

