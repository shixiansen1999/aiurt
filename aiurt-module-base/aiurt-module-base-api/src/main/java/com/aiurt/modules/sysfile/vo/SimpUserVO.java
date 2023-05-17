package com.aiurt.modules.sysfile.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: SimpUserVO
 * @author: Mr.zhao
 * @date: 2021/10/29 10:41
 */
@Data
@Accessors(chain = true)
public class SimpUserVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("用户id")
	private String userId;

	@ApiModelProperty("用户名称")
	private String userName;

	@ApiModelProperty(value = "上传标记")
	private Integer uploadTag;


	@ApiModelProperty(value = "人员key")
	private String userKey;

	/**
	 * 查看状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "查看状态标记")
	private String lookStatusMark;
	/**
	 * 编辑状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "编辑状态标记")
	private String editStatusMark;
	/**
	 * 上传状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "上传状态标记")
	private String uploadStatusMark;

	/**
	 * 下载状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "下载状态标记")
	private String downloadStatusMark;

	/**
	 * 删除状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "删除状态标记")
	private String deleteStatusMark;
	/**
	 * 重命名状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "重命名状态标记")
	private String renameStatusMark;

	/**
	 * 在线编辑状态标记
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "在线编辑状态标记")
	private String onlineEditingMark;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SimpUserVO that = (SimpUserVO) o;
		return Objects.equals(userId, that.userId) && Objects.equals(userName, that.userName);
	}


	@Override
	public int hashCode() {
		return Objects.hash(userId, userName);
	}
}
