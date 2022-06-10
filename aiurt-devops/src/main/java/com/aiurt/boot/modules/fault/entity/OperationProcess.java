package com.aiurt.boot.modules.fault.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

import java.util.Date;

/**
 * @Description: 运转流程
 * @Author: swsc
 * @Date:   2021-09-27
 * @Version: V1.0
 */
@Data
@TableName("operation_process")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="operation_process对象", description="运转流程")
public class OperationProcess {

	/**主键id,自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id,自动递增")
	public Long id;

	/**故障编号*/
	@Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号")
	public String faultCode;

	/**环节编号*/
	@Excel(name = "环节编号", width = 15)
	@ApiModelProperty(value = "环节编号")
	public Integer processCode;

	/**故障环节*/
	@Excel(name = "故障环节", width = 15)
    @ApiModelProperty(value = "故障环节")
	public String processLink;

	/**处理人*/
	@Excel(name = "处理人", width = 15)
    @ApiModelProperty(value = "处理人")
	public String processPerson;

	/**处理时间*/
	@Excel(name = "处理时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
	public Date processTime;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	public Date createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	public Date updateTime;
}
