package com.aiurt.modules.major.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("cs_major")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_major对象", description="cs_major")
public class CsMajor implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    @ExcelExtend(isRequired = true,remark = "必填字段，且在系统中存在该专业；")
    @MajorFilterColumn
    private String majorCode;
	/**专业名称*/
	@Excel(name = "专业名称", width = 15)
    @ExcelExtend(isRequired = true,remark = "必填字段，且不能重复，支持数字、英文字母、符号等；")
    @ApiModelProperty(value = "专业名称")
    private String majorName;
	/**说明*/
    @ApiModelProperty(value = "说明")
    private String remark;
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
	/**删除标志*/
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
    /**与专业关联的子系统*/
    @ApiModelProperty(value = "与专业关联的子系统")
	@TableField(exist = false)
    private List<CsSubsystem> children;

    @ApiModelProperty(value = "与专业关联的物资分类")
    @TableField(exist = false)
    private List<MaterialBaseType> materialBaseTypeList;

    @ApiModelProperty(value = "备用字段")
    @TableField(exist = false)
    private String byType = "zy";
    /**设备类型子集*/
    @ApiModelProperty(value = "设备类型子集")
    @TableField(exist = false)
    private List<DeviceType> deviceTypeChildren;

    @TableField(exist = false)
    private  String  title;

    @TableField(exist = false)
    private  String  value;
    @TableField(exist = false)
    private String color;

    @TableField(exist = false)
    private Boolean isRemove;
}
