package com.aiurt.modules.sysfile.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    /**
     * 下载状态
     */
    @Excel(name = "下载状态", width = 15)
    @ApiModelProperty(value = "下载状态")
    @NotNull(message = "下载状态不能为空")
    private Integer downStatus;

    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String createBy;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String updateBy;
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

    @ApiModelProperty("上一级id")
    private Long parentId;
}
