package com.aiurt.modules.faultanalysisreport.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: fault_analysis_report
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Data
@TableName("fault_analysis_report")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_analysis_report对象", description="fault_analysis_report")
public class FaultAnalysisReport implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**故障编号*/
	@Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号")
    private String faultCode;
	/**故障分析*/
	@Excel(name = "故障分析", width = 15)
    @ApiModelProperty(value = "故障分析")
    private String faultAnalysis;
	/**解决方案*/
	@Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "解决方案")
    private String solution;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
    /**附件*/
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private java.lang.String filePath;
    /**审核人*/
    @Excel(name = "审核人", width = 15)
    @ApiModelProperty(value = "审核人")
    private java.lang.String approvedUserName;
    /**审核结果*/
    @Excel(name = "审核结果", width = 15)
    @ApiModelProperty(value = "审核结果")
    private java.lang.Integer approvedResult;
    /**审核时间*/
    @Excel(name = "审核时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "审核时间")
    private java.util.Date approvedTime;
    /**审核说明*/
    @Excel(name = "审核说明", width = 15)
    @ApiModelProperty(value = "审核说明")
    private java.lang.String approvedRemark;
    /**浏览次数*/
    @Excel(name = "浏览次数", width = 15)
    @ApiModelProperty(value = "浏览次数")
    private java.lang.Integer scanSum;
    /**故障知识库id*/
    @ApiModelProperty(value = "故障知识库id")
    private String faultKnowledgeBaseId;

    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @TableField(exist = false)
    private String faultPhenomenon;

    /**状态*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    @TableField(exist = false)
    private Integer status;

    /**故障分类名称*/
    @Excel(name = "故障分类名称", width = 15)
    @ApiModelProperty(value = "故障分类名称")
    @TableField(exist = false)
    private String faultTypeCode;

    /**开始日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date startTime;

    /**结束日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date endTime;
}
