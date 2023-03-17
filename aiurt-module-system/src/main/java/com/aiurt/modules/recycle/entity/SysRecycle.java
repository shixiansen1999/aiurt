package com.aiurt.modules.recycle.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_recycle")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_recycle对象", description="sys_recycle")
public class SysRecycle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    private String sysOrgCode;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**单据表名*/
    @ApiModelProperty(value = "单据表名")
    private String billTablenm;
    /**单据值*/
    @ApiModelProperty(value = "单据值")
    private String billValue;
    /**还原人*/
    @ApiModelProperty(value = "还原人")
    private String restoreUserId;
    /**还原时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "还原时间")
    private Date restoreTime;
    /**删除人*/
    @ApiModelProperty(value = "删除人")
    private String physicaldelId;
    /**删除时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "删除时间")
    private Date dphysicalDel;
    /**状态（（1正常2还原3删除））*/
    @ApiModelProperty(value = "状态（（1正常2还原3删除））")
    private Integer state;
    /**单据id*/
    @ApiModelProperty(value = "单据id")
    private String billId;
    /**模块名称*/
    @ApiModelProperty(value = "模块名称")
    private String moduleName;
    /**删除记录用户id*/
    @ApiModelProperty(value = "删除记录用户id")
    private String regUserId;
    /**是否逻辑删除, 1是 0否*/
    @ApiModelProperty(value = "是否逻辑删除, 1是 0否")
    private Integer delSign;
}
