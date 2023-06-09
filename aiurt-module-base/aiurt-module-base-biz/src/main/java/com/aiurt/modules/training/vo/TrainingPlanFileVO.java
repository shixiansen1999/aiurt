package com.aiurt.modules.training.vo;

import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: TrainingPlanFileVO
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TrainingPlanFileVO extends TrainingPlanFile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件名称")
	private String name;
	
	/**
	 * 文件url
	 */
	@ApiModelProperty(value = "文件url")
	private String url;
	
	/**
	 * 类型
	 */
	@ApiModelProperty(value = "类型")
	private String type;
	
	/**
	 * 上传时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "上传时间")
	private Date uploadTime;
	
	/**
	 * 上传人
	 */
	@ApiModelProperty(value = "上传人")
	private String uploadName;
	/**
	 * 下载时长
	 */
	@ApiModelProperty(value = "下载时长")
	private String downloadDuration;
	/**
	 * 文件大小
	 */
	@ApiModelProperty(value = "文件大小")
	private String size;
	/**
	 * 权限
	 */
	@ApiModelProperty(value = "权限（1允许查看，2允许下载、3允许在线编辑、4允许删除、5允许编辑、6可管理权限）")
	private Integer permission;
}
