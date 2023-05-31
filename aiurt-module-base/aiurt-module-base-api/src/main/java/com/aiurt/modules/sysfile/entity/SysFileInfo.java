package com.aiurt.modules.sysfile.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author zwl
 */
@Data
@TableName("sys_file_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_file_info对象", description = "文件信息表")
public class SysFileInfo {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @Excel(name = "文件md5", width = 15)
    @ApiModelProperty(value = "文件md5")
    private String md5;

    @Excel(name = "文件id", width = 15)
    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名")
    @NotBlank(message = "文件名称不能为空")
    @Length(min = 1, max = 255, message = "文件名称长度必须是1-255个字符")
    private String fileName;

    @Excel(name = "0否，1是", width = 15)
    @ApiModelProperty(value = "0否，1是")
    private Integer isImg;

    @Excel(name = "文件类型", width = 15)
    @ApiModelProperty(value = "文件类型")
    private String contentType;

    @Excel(name = "文件大小", width = 15)
    @ApiModelProperty(value = "文件大小")
    private String size;

    @Excel(name = "物理路径", width = 15)
    @ApiModelProperty(value = "物理路径")
    private String path;

    @Excel(name = "web路径", width = 15)
    @ApiModelProperty(value = "web路径")
    private String url;

    @Excel(name = "存储源", width = 15)
    @ApiModelProperty(value = "存储源")
    private String source;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @Excel(name = "删除状态", width = 15)
    @ApiModelProperty(value = "删除状态")
    private Integer delFlag;

    @Excel(name = "应用id", width = 15)
    @ApiModelProperty(value = "应用id")
    private String appId;

    @Excel(name = "下载用户", width = 15)
    @ApiModelProperty(value = "下载用户")
    private String userName;

    @Excel(name = "部门编码", width = 15)
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    @Excel(name = "下载时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "下载时间")
    private Date downloadTime;

    @Excel(name = "下载状态", width = 15)
    @ApiModelProperty(value = "下载状态")
    private Integer downloadStatus;

    @TableField(exist = false)
    @Excel(name = "下载状态", width = 15)
    @ApiModelProperty(value = "下载状态名称")
    private String downloadStatusName;

    @Excel(name = "下载时长", width = 15)
    @ApiModelProperty(value = "下载时长")
    private String downloadDuration;

}
