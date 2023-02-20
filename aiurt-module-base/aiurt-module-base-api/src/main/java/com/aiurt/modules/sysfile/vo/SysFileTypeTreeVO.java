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
 * 树状返回
 *
 * @description: SysFileTypeTreeVO
 * @author: Mr.zhao
 * @date: 2021/10/26 14:58
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileTypeTreeVO extends SysFileType implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<SysFileTypeTreeVO> children;

	@ApiModelProperty("可编辑")
	private Set<SimpUserVO> editUsers;

	@ApiModelProperty("可上传")
	private Set<SimpUserVO> uploadStatus;

	@ApiModelProperty("可删除")
	private Set<SimpUserVO> deleteStatus;
	private String color;

	private Boolean matching;


}
