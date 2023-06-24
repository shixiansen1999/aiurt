package com.aiurt.modules.fault.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: fault_cause_detail
 * @Author: aiurt
 * @Date:   2023-05-29
 * @Version: V1.0
 */
@Data
@TableName("fault_cause_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_cause_detail对象", description="fault_cause_detail")
public class FaultCauseDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**故障编码*/
	@Excel(name = "故障编码", width = 15)
    @ApiModelProperty(value = "故障编码")
    private String faultCode;
	/**出现百分率*/
	@Excel(name = "出现百分率", width = 15)
    @ApiModelProperty(value = "出现百分率")
    private String percentage;
	/**故障原因id*/
	@Excel(name = "故障原因id", width = 15)
    @ApiModelProperty(value = "故障原因id")
    private String faultCauseSolutionId;
	/**知识库库id*/
	@Excel(name = "知识库库id", width = 15)
    @ApiModelProperty(value = "知识库库id")
    private String faultKnowledgeBaseId;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private Integer num;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**逻辑删除字段*/
	@Excel(name = "逻辑删除字段", width = 15)
    @ApiModelProperty(value = "逻辑删除字段")
    @TableLogic
    private Integer delFlag;

	private String faultCause;
}
