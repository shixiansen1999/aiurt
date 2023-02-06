package com.aiurt.boot.materials.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: emergency_materials_invoices
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_materials_invoices对象", description="emergency_materials_invoices")
public class EmergencyMaterialsInvoicesDTO {

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**应急物资巡检单号*/
	@Excel(name = "应急物资巡检单号", width = 15)
    @ApiModelProperty(value = "应急物资巡检单号")
    private String materialsPatrolCode;
	/**巡视标准编码*/
	@Excel(name = "巡视标准编码", width = 15)
    @ApiModelProperty(value = "巡视标准编码")
    private String standardCode;
	/**巡视标准名称*/
	@Excel(name = "巡视标准名称", width = 15)
    @ApiModelProperty(value = "巡视标准名称")
    private String standardName;
	/**巡视日期*/
	@Excel(name = "巡视日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "巡视日期")
    private java.util.Date patrolDate;
    /**主管部门编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门编码")
    private java.lang.String primaryOrg;
    /**线路编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视线路编码")
    private java.lang.String lineCode;


    /**巡视线路名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视线路名称")
    private java.lang.String lineName;
    /**站点编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视站点编码")
    private java.lang.String stationCode;


    /**巡视站点名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视站点名称")
    private java.lang.String stationName;
    /**位置编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视位置编码")
    private java.lang.String positionCode;
    /**巡视位置名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视位置名称")
    private java.lang.String positionName;
    /**主管部门集合*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门集合")
    private List<String> primaryCodeList;

    /**检查结果：0异常、1正常*/
    @Excel(name = "检查结果：0异常、1正常", width = 15)
    @ApiModelProperty(value = "检查结果：0异常、1正常")
    @Dict(dicCode = "check_result")
    private Integer inspectionResults;
    /**检查结果：0异常、1正常*/
    @Excel(name = "检查结果：0异常、1正常", width = 15)
    @ApiModelProperty(value = "检查结果：0异常、1正常")
    private String result;
	/**巡视人ID*/
	@Excel(name = "巡视人ID", width = 15)
    @ApiModelProperty(value = "巡视人ID")
    private String userId;
    /**巡视人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视人名称")
    private java.lang.String patrolName;

    /**巡视班组Code*/
    @Excel(name = "巡视班组Code", width = 15)
    @ApiModelProperty(value = "巡视班组Code")
    private String departmentCode;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
    @TableField(exist = false)
    private List<String> selections;
    /**巡视班组名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视班组名称")
    private java.lang.String patrolTeamName;
    /**巡视日期*/
    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private String startTime;


    /**巡视日期*/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
