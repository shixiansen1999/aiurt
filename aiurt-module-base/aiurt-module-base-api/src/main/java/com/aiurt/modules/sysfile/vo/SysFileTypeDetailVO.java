package com.aiurt.modules.sysfile.vo;

import com.aiurt.modules.sysfile.entity.SysFileType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @description: SysFileTypeDetailVO
 * @author: Mr.zhao
 * @date: 2021/10/29 9:34
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileTypeDetailVO extends SysFileType implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("原可查看")
	private Set<SimpUserVO> primaryLookStatus;

	@ApiModelProperty("原可查看筛选")
	private List<String> primaryLookStatusCode;

	@ApiModelProperty("可查看")
	private Set<SimpUserVO> lookUsers;

	@ApiModelProperty("原可编辑")
	private Set<SimpUserVO> primaryEditStatus;

	@ApiModelProperty("原可编辑筛选")
	private List<String> primaryEditStatusCode;

	@ApiModelProperty("可编辑")
	private Set<SimpUserVO> editUsers;

	@ApiModelProperty("原可上传")
	private Set<SimpUserVO> primaryUploadStatus;

	@ApiModelProperty("管理员权限筛选")
	private List<String> primaryUploadStatusCode;

	@ApiModelProperty("可上传")
	private Set<SimpUserVO> uploadStatus;

	@ApiModelProperty("原可下载")
	private Set<SimpUserVO> primaryDownloadStatus;

	@ApiModelProperty("原可下载筛选")
	private List<String> primaryDownloadStatusCode;

	@ApiModelProperty("可下载")
	private Set<SimpUserVO> downloadStatus;

	@ApiModelProperty("原可删除")
	private Set<SimpUserVO> primaryDeleteStatus;

	@ApiModelProperty("原可删除筛选")
	private List<String> primaryDeleteStatusCode;

	@ApiModelProperty("可删除")
	private Set<SimpUserVO> deleteStatus;

	@ApiModelProperty("原可重命名")
	private Set<SimpUserVO> primaryRenameStatus;

	@ApiModelProperty("可重命名")
	private Set<SimpUserVO> renameStatus;

	@ApiModelProperty("原可在线编辑")
	private Set<SimpUserVO> primaryOnlineEditing;

	@ApiModelProperty("原可在线编辑筛选")
	private List<String> primaryOnlineEditingCode;

	@ApiModelProperty("可在线编辑")
	private Set<SimpUserVO> onlineEditing;


}
