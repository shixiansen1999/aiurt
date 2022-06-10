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
 * @Description: 故障更换备件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("fault_change_spare_part")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault_change_spare_part对象", description="故障更换备件表")
public class FaultChangeSparePart {

	/**主键id，自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
	private Long id;
	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
	private String faultCode;
	/**维修记录id*/
	@Excel(name = "维修记录id", width = 15)
    @ApiModelProperty(value = "维修记录id")
	private Long repairRecordId;
	/**原备件编号*/
	@Excel(name = "原备件编号", width = 15)
    @ApiModelProperty(value = "原备件编号")
	private String oldSparePartCode;
	/**原备件数量*/
	@Excel(name = "原备件数量", width = 15)
    @ApiModelProperty(value = "原备件数量")
	private Integer oldSparePartNum;
	/**新备件编号*/
	@Excel(name = "新备件编号", width = 15)
    @ApiModelProperty(value = "新备件编号")
	private String newSparePartCode;
	/**新备件数量*/
	@Excel(name = "新备件数量", width = 15)
    @ApiModelProperty(value = "新备件数量")
	private Integer newSparePartNum;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**删除状态：0.未删除 1已删除*/
	@Excel(name = "删除状态：0.未删除 1已删除", width = 15)
	@ApiModelProperty(value = "删除状态：0.未删除 1已删除")
	private Integer delFlag;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
}
