package com.aiurt.modules.sysFile.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: SysFileTypeParam
 * @author: Mr.zhao
 * @date: 2021/10/27 9:16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileTypeParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("文档分类id")
	private Long id;

	@ApiModelProperty("上级菜单")
	@NotNull(message = "上级菜单不能为空")
	private Long parentId;

	@ApiModelProperty("分类名称")
	@NotBlank(message = "类型不能为空")
	private String name;

	@ApiModelProperty("等级")
	@NotNull(message = "等级标识不能为空")
	private Integer grade;

	@ApiModelProperty("可查看人员")
	@NotEmpty(message = "请添加可查看人员")
	private List<String> lookIds;

	@ApiModelProperty("可上传人员")
	@NotEmpty(message = "请添加可上传人员")
	private List<String> editIds;


}
