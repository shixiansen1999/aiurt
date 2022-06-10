package com.aiurt.boot.modules.training.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.aiurt.boot.modules.training.entity.TrainingPlanFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: TrainingPlanFileVO
 * @author: Mr.zhao
 * @date: 2021/11/28 18:27
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

}
