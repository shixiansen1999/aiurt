package com.aiurt.modules.sysfile.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: FileAppVO
 * @author: Mr.zhao
 * @date: 2021/11/10 9:24
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileManageAppVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "文件夹或文件名称")
    private String name;

    @ApiModelProperty(value = "文件url")
    private String url;

    @ApiModelProperty(value = "文件类型")
    private String type;

    /**
     * 是否为文件 0.否 1.是
     */
    @ApiModelProperty("是否为文件 0.否 1.是")
    private Integer status;

    @ApiModelProperty(value = "下载状态")
    @NotNull(message = "下载状态不能为空")
    private Integer downStatus;

    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String updateBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty("上一级id")
    private Long parentId;

    @ApiModelProperty(value = "权限（1允许查看，2允许下载、3允许在线编辑、4允许删除、5允许编辑、6可管理权限）")
    private Integer permission;
}
