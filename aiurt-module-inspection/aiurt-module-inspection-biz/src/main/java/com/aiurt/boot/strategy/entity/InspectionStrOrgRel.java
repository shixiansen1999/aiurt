package com.aiurt.boot.strategy.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: inspection_str_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("inspection_str_org_rel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="inspection_str_org_rel对象", description="inspection_str_org_rel")
public class InspectionStrOrgRel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**检修策略编码,关联inspection_strategy表的code*/
	@Excel(name = "检修策略编码,关联inspection_strategy表的code", width = 15)
    @ApiModelProperty(value = "检修策略编码,关联inspection_strategy表的code")
    private java.lang.String inspectionStrCode;
	/**组织编码，关联sys_depart的org_code*/
	@Excel(name = "组织编码，关联sys_depart的org_code", width = 15)
    @ApiModelProperty(value = "组织编码，关联sys_depart的org_code")
    private java.lang.String orgCode;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
