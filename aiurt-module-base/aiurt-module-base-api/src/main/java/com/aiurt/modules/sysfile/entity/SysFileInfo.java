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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

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

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 文件md5
     */
    @Excel(name = "文件md5", width = 15)
    @ApiModelProperty(value = "文件md5")
    private String md5;


    /**
     * 文件名
     */
    @Excel(name = "文件id", width = 15)
    @ApiModelProperty(value = "文件id")
    private Long fileId;


    /**
     * 文件名
     */
    @Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 0否，1是
     */
    @Excel(name = "0否，1是", width = 15)
    @ApiModelProperty(value = "0否，1是")
    private Integer isImg;


    /**
     * 文件类型
     */
    @Excel(name = "文件类型", width = 15)
    @ApiModelProperty(value = "文件类型")
    private String contentType;


    /**
     * 文件大小
     */
    @Excel(name = "文件大小", width = 15)
    @ApiModelProperty(value = "文件大小")
    private String size;


    /**
     * 物理路径
     */
    @Excel(name = "物理路径", width = 15)
    @ApiModelProperty(value = "物理路径")
    private String path;


    /**
     * web路径
     */
    @Excel(name = "web路径", width = 15)
    @ApiModelProperty(value = "web路径")
    private String url;


    /**
     * 存储源
     */
    @Excel(name = "存储源", width = 15)
    @ApiModelProperty(value = "存储源")
    private String source;


    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


    /**
     * 删除状态
     */
    @Excel(name = "删除状态", width = 15)
    @ApiModelProperty(value = "删除状态")
    private Integer delFlag;


    /**
     * 应用id
     */
    @Excel(name = "应用id", width = 15)
    @ApiModelProperty(value = "应用id")
    private String appId;


    /**
     * 下载用户
     */
    @Excel(name = "下载用户", width = 15)
    @ApiModelProperty(value = "下载用户")
    private String userName;


    /**
     * 部门编码
     */
    @Excel(name = "部门编码", width = 15)
    @ApiModelProperty(value = "部门编码")
    private String departmentCode;


    /**
     * 下载时间
     */
    @Excel(name = "下载时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "下载时间")
    private Date downloadTime;


    /**
     * 下载状态
     */
    @Excel(name = "下载状态", width = 15)
    @ApiModelProperty(value = "下载状态")
    private Integer downloadStatus;

    /**
     * 下载状态名称
     */
    @TableField(exist = false)
    @Excel(name = "下载状态", width = 15)
    @ApiModelProperty(value = "下载状态名称")
    private String downloadStatusName;

    /**
     * 下载时长
     */
    @Excel(name = "下载时长", width = 15)
    @ApiModelProperty(value = "下载时长")
    private String downloadDuration;

}
