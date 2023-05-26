package com.aiurt.modules.sysfile.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 树状返回
 *
 * @description: SysFileTypeTreeVO
 * @author: wgp
 * @date: 2023/05/24 14:58
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFolderTreeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	@ApiModelProperty(value = "兼容前端，与id值保持一致")
	private Long value;

	@ApiModelProperty(value = "兼容前端")
	private List<SysFolderTreeVO> children;

	@ApiModelProperty(value = "文件夹名称")
	private String title;

	@ApiModelProperty(value = "是否是叶子节点：否false是true")
	private Boolean isLeaf;

	@ApiModelProperty(value = "权限（1允许查看，2允许下载、3允许在线编辑、4允许删除、5允许编辑、6可管理权限）")
	private Integer permission;

	@ApiModelProperty(value = "父级id")
	private Long parentId;


}
