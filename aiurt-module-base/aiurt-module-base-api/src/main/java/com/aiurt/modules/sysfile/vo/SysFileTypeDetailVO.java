package com.aiurt.modules.sysfile.vo;

import com.aiurt.modules.sysfile.entity.SysFileType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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

	@ApiModelProperty("可查看")
	private Set<SimpUserVO> lookUsers;

	@ApiModelProperty("可编辑")
	private Set<SimpUserVO> editUsers;

	@ApiModelProperty("可上传")
	private Set<SimpUserVO> uploadStatus;

	@ApiModelProperty("可下载")
	private Set<SimpUserVO> downloadStatus;

	@ApiModelProperty("可删除")
	private Set<SimpUserVO> deleteStatus;

	@ApiModelProperty("可重命名")
	private Set<SimpUserVO> renameStatus;

	@ApiModelProperty("可在线编辑")
	private Set<SimpUserVO> onlineEditing;
}
