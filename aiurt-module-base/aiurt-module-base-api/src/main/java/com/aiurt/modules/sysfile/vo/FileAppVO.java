package com.aiurt.modules.sysfile.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @description: FileAppVO
 * @author: Mr.zhao
 * @date: 2021/11/10 9:24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FileAppVO implements Serializable {
	private static final long serialVersionUID = 1L;

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
	private Long typeId;
	/**
	 * 分类名称
	 */
	@Excel(name = "分类名称", width = 15)
	@ApiModelProperty(value = "分类名称")
	private String typeName;
	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件名称")
	private String fileName;
	/**
	 * 文件url
	 */
	@ApiModelProperty(value = "文件url")
	private String url;
	/**
	 * 是否为文件 0.否 1.是
	 */
	@ApiModelProperty("是否为文件 0.否 1.是")
	private Integer status;


	@ApiModelProperty("上一级id")
	private Long parentId;
}
