package com.aiurt.modules.fault.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: 故障操作日志
 * @Author: gaowei
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("operation_process")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="operation_process对象", description="故障操作日志")
public class OperationProcess implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id,自动递增*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

	/**故障编号*/
	@Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号", required = true)
    private String faultCode;

	/**环节编号*/
	@Excel(name = "环节编号", width = 15)
    @ApiModelProperty(value = "环节编号")
    private Integer processCode;

	/**故障环节*/
	@Excel(name = "故障环节", width = 15)
    @ApiModelProperty(value = "故障环节")
    private String processLink;

	/**处理人*/
	@Excel(name = "处理人", width = 15)
    @ApiModelProperty(value = "处理人")
    private String processPerson;

	@ApiModelProperty(value = "处理人名称")
    @TableField(exist = false)
	private String processPersonName;

	@ApiModelProperty(value = "角色名称")
    @TableField(exist = false)
	private String roleName;

	/**处理时间*/
	@Excel(name = "处理时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date processTime;

	@ApiModelProperty("处理时长")
    @TableField(exist = false)
	private String processingTime;

	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;

	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "说明")
    private String remark;
}
