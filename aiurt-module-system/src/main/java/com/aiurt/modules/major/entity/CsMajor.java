package com.aiurt.modules.major.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
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
    private String majorCode;
	/**专业名称*/
	@Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private String majorName;
	/**说明*/
	@Excel(name = "说明", width = 15)
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
    private String sysOrgCode;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
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
}
