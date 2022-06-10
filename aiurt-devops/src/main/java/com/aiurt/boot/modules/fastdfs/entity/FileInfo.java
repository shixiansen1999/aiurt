package com.aiurt.boot.modules.fastdfs.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 附件表
 * @Author: swsc
 * @Date:   2020-10-23
 * @Version: V1.0
 */
@Data
@TableName("sys_file_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_file_info对象", description="附件表")
public class FileInfo {

	/**文件md5*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;

    @ApiModelProperty(value = "文件md5")
	private String md5;
	/**文件名*/
	@Excel(name = "文件名", width = 15)
    @ApiModelProperty(value = "文件名")
	private String name;
	/**0否，1是*/
	@Excel(name = "0否，1是", width = 15)
    @ApiModelProperty(value = "0否，1是")
	private Integer isImg;
	/**文件类型*/
	@Excel(name = "文件类型", width = 15)
    @ApiModelProperty(value = "文件类型")
	private String contentType;
	/**文件大小*/
	@Excel(name = "文件大小", width = 15)
    @ApiModelProperty(value = "文件大小")
	private Long size;
	/**物理路径*/
	@Excel(name = "物理路径", width = 15)
    @ApiModelProperty(value = "物理路径")
	private String path;
	/**web路径*/
	@Excel(name = "web路径", width = 15)
    @ApiModelProperty(value = "web路径")
	private String url;
	/**存储源*/
	@Excel(name = "存储源", width = 15)
    @ApiModelProperty(value = "存储源")
	private String source;
	/**createTime*/
	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private Date createTime;
	/**updateTime*/
	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
	private Date updateTime;
	/**应用id*/
	@Excel(name = "应用id", width = 15)
    @ApiModelProperty(value = "应用id")
	private String appId;
}
