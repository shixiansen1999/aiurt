package com.aiurt.modules.sysfile.param;

import com.aiurt.modules.sysfile.vo.UploadVO;
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
	@NotNull(message = "上级菜单是不能为空的哦！")
	private Long parentId;

	@ApiModelProperty("分类名称")
	@NotBlank(message = "类型是不能为空的哦！")
	private String name;

	@ApiModelProperty("等级")
	@NotNull(message = "等级标识是不能为空的哦！")
	private Integer grade;

	@ApiModelProperty("可查看人员")
	private List<String> lookIds;

	@ApiModelProperty("可编辑人员")
	private List<String> editIds;

//	@ApiModelProperty("可上传人员")
//	private List<UploadVO> uploads;

	@ApiModelProperty("可上传人员")
	private List<String> uploads;

	@ApiModelProperty("可下载人员")
	private List<String> downloads;

	@ApiModelProperty("可删除人员")
	private List<String> deletes;

	@ApiModelProperty("可在线编辑人员")
	private List<String> onlineEditing;


}
