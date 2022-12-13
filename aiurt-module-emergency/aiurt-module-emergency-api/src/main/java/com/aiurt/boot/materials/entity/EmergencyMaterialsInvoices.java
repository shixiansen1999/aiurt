package com.aiurt.boot.materials.entity;

import java.io.Serializable;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
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
 * @Description: emergency_materials_invoices
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_materials_invoices")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_materials_invoices对象", description="emergency_materials_invoices")
public class EmergencyMaterialsInvoices extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**应急物资巡检单号*/
	@Excel(name = "应急物资巡检单号", width = 15)
    @ApiModelProperty(value = "应急物资巡检单号")
    private java.lang.String materialsPatrolCode;
	/**巡视标准编码*/
	@Excel(name = "巡视标准编码", width = 15)
    @ApiModelProperty(value = "巡视标准编码")
    private java.lang.String standardCode;
	/**巡视标准名称*/
	@Excel(name = "巡视标准名称", width = 15)
    @ApiModelProperty(value = "巡视标准名称")
    private java.lang.String standardName;
	/**巡视日期*/
	@Excel(name = "巡视日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "巡视日期")
    private java.util.Date patrolDate;
    /**线路编码*/
    @Excel(name = "巡视线路编码", width = 15)
    @ApiModelProperty(value = "巡视线路编码")
    private java.lang.String lineCode;
    /**站点编码*/
    @Excel(name = "巡视站点编码", width = 15)
    @ApiModelProperty(value = "巡视站点编码")
    private java.lang.String stationCode;
    /**位置编码*/
    @Excel(name = "巡视位置编码", width = 15)
    @ApiModelProperty(value = "巡视位置编码")
    private java.lang.String positionCode;
    /**检查结果：0异常、1正常*/
    @Excel(name = "检查结果：0异常、1正常", width = 15)
    @ApiModelProperty(value = "检查结果：0异常、1正常")
    @Dict(dicCode = "check_result")
    private java.lang.Integer inspectionResults;
	/**巡视人ID*/
	@Excel(name = "巡视人ID", width = 15)
    @ApiModelProperty(value = "巡视人ID")
    private java.lang.String userId;
    /**巡视班组Code*/
    @Excel(name = "巡视班组Code", width = 15)
    @ApiModelProperty(value = "巡视班组Code")
    private java.lang.String departmentCode;
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
