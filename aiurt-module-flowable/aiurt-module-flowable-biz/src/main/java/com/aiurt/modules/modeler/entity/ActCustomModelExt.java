package com.aiurt.modules.modeler.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.Accessors;

/**
 * @Description: 流程属性扩展表
 * @Author: aiurt
 * @Date:   2023-09-12
 * @Version: V1.0
 */
@Data
@Builder
@TableName("act_custom_model_ext")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_model_ext对象", description="流程属性扩展表")
@NoArgsConstructor
@AllArgsConstructor
public class ActCustomModelExt implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**流程引擎的定义Id*/
	@Excel(name = "流程引擎的定义Id", width = 15)
    @ApiModelProperty(value = "流程引擎的定义Id")
    private String processDefinitionId;
	/**流程标识*/
	@Excel(name = "流程标识", width = 15)
    @ApiModelProperty(value = "流程标识")
    private String modelKey;
	/**审批人去重规则*/
	@Excel(name = "审批人去重规则", width = 15)
    @ApiModelProperty(value = "审批人去重规则")
    private String dedulicateRule;
	/**是否去重*/
	@Excel(name = "是否去重", width = 15)
    @ApiModelProperty(value = "是否去重")
    private Integer isDedulicate;
	/**是否撤回*/
	@Excel(name = "是否撤回", width = 15)
    @ApiModelProperty(value = "是否撤回")
    private Integer isRecall;
	/**撤回节点*/
	@Excel(name = "撤回节点", width = 15)
    @ApiModelProperty(value = "撤回节点")
    private String recallNodeId;
	/**是否催办*/
	@Excel(name = "是否催办", width = 15)
    @ApiModelProperty(value = "是否催办")
    private Integer isRemind;
}
