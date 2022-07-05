package com.aiurt.modules.faultknowledgebase.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Data
@TableName("fault_knowledge_base")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_knowledge_base对象", description="故障知识库")
public class FaultKnowledgeBase implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**故障知识分类编码*/
	@Excel(name = "故障知识分类编码", width = 15)
    @ApiModelProperty(value = "故障知识分类编码")
    @Dict(dictTable = "fault_knowledge_base_type", dicText = "name", dicCode = "code")
    private String knowledgeBaseTypeCode;
	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;
	/**故障原因*/
	@Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    private String faultReason;
	/**故障措施/解决方案*/
	@Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "故障措施/解决方案")
    private String solution;
	/**浏览次数*/
	@Excel(name = "浏览次数", width = 15)
    @ApiModelProperty(value = "浏览次数")
    private Integer scanNum;
	/**关联故障*/
	@Excel(name = "关联故障", width = 15)
    @ApiModelProperty(value = "关联故障")
    private String faultCodes;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;
	/**排查方法*/
	@Excel(name = "排查方法", width = 15)
    @ApiModelProperty(value = "排查方法")
    private String method;
	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;
	/**专业子系统*/
	@Excel(name = "专业子系统", width = 15)
    @ApiModelProperty(value = "专业子系统")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private String systemCode;
	/**设备类型*/
	@Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    @Dict(dictTable ="device_Type",dicText = "name",dicCode = "code")
    private String deviceTypeCode;
	/**设备组件*/
	@Excel(name = "设备组件", width = 15)
    @ApiModelProperty(value = "设备组件")
    @Dict(dictTable ="device_assembly",dicText = "material_name",dicCode = "device_code")
    private String materialCode;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
    @ApiModelProperty(value = "审核人")
    private String approvedUserName;
	/**审核结果(1:通过,0不通过)*/
	@Excel(name = "审核结果(1:通过,0不通过)", width = 15)
    @ApiModelProperty(value = "审核结果(1:通过,0不通过)")
    private Integer approvedResult;
	/**审核时间*/
	@Excel(name = "审核时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间")
    private Date approvedTime;
	/**审核说明*/
	@Excel(name = "审核说明", width = 15)
    @ApiModelProperty(value = "审核说明")
    private String approvedRemark;
    /**附件*/
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private java.lang.String filePath;
    /**状态*/
    @Excel(name = "状态(0:待审批,1:已审批,2:已驳回)", width = 15)
    @ApiModelProperty(value = "状态(0:待审批,1:已审批,2:已驳回)")
    private Integer status;

    /**设备类型名称*/
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String deviceTypeName;
    /**组件名称*/
    @Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
    @TableField(exist = false)
    private String materialName;

    /**图片*/
    @Excel(name = "图片", width = 15,type = 2)
    @ApiModelProperty(value = "图片")
    @TableField(exist = false)
    private byte[] picture;

    /**
     *分词，故障推荐解决方案使用
     */
    @ApiModelProperty(value = "分词值")
    @TableField(exist = false)
    private String matchName;
}
