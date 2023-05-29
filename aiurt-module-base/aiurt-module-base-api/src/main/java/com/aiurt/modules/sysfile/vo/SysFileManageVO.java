package com.aiurt.modules.sysfile.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: SysFileManageVO
 * @author: wpg
 * @date: 2023/05/24 13:54
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileManageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属文件夹id")
    private String typeId;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件url")
    private String url;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "大小")
    private String fileSize;

    @ApiModelProperty(value = "下载次数")
    private Integer downSize;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "权限（1允许查看，2允许下载、3允许在线编辑、4允许删除、5允许编辑、6可管理权限）")
    private Integer permission;
}
