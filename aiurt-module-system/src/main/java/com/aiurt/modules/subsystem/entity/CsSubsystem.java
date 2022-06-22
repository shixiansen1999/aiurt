package com.aiurt.modules.subsystem.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("cs_subsystem")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_subsystem对象", description="cs_subsystem")
public class CsSubsystem implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String systemName;
	/**编号*/
	@Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
    private String systemCode;
	/**说明*/
	@Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
    private String description;
	/**删除标志，0未删除，1已删除*/
	@Excel(name = "删除标志，0未删除，1已删除", width = 15)
    @ApiModelProperty(value = "删除标志，0未删除，1已删除")
    private Integer delFlag;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**所属专业-专业表*/
	@Excel(name = "所属专业-专业表", width = 15)
    @ApiModelProperty(value = "所属专业-专业表")
    private String majorCode;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**子系统人员*/
    @ApiModelProperty(value = "子系统人员")
    @TableField(exist = false)
    private List<CsSubsystemUser> systemUserList;
    /**子系统人员*/
    @ApiModelProperty(value = "子系统下的物资分类")
    @TableField(exist = false)
    private List<MaterialBaseType> materialBaseTypeList;
}
