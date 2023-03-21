package com.aiurt.modules.fault.entity;

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
 * @Description: fault_device
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("fault_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="故障设备", description="fault_device")
public class FaultDevice implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
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

	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;

	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

	/**故障单编号*/
	@Excel(name = "故障单编号", width = 15)
    @ApiModelProperty(value = "故障单编号")
    private String faultCode;

	/**设备id*/
	@Excel(name = "设备id", width = 15)
    @ApiModelProperty(value = "设备id")
    private String deviceId;

	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

	@ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
	private String deviceName;


	@ApiModelProperty("设备类型名称")
    @TableField(exist = false)
	private String deviceTypeName;

	@ApiModelProperty("设备类编码")
    @TableField(exist = false)
	private String deviceTypeCode;

	@ApiModelProperty("线路编码")
    @TableField(exist = false)
	private String lineCode;

    @ApiModelProperty("线路编码")
    @TableField(exist = false)
	private String lineName;

    @ApiModelProperty("站点名称")
    @TableField(exist = false)
	private String stationName;

    @ApiModelProperty("站点编码")
    @TableField(exist = false)
	private String stationCode;

    @ApiModelProperty("位置编码")
    @TableField(exist = false)
	private String positionCode;

    @ApiModelProperty("位置名称")
    @TableField(exist = false)
	private String positionName;

    /**所属部门*/
    @ApiModelProperty(value = "送修状态")
    @Dict(dicCode = "device_repair_status")
    private String repairStatus;

    /**送修序列号*/
    @ApiModelProperty(value = "送修序列号")
    private String repairSerialNumber;

    /**所属部门*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修时间")
    private Date repairSendTime;

    /**所属部门*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修返回时间")
    private Date repairBackTime;

    @ApiModelProperty("组件编码")
    private String materialCodes;
    @ApiModelProperty("组件名称")
    @TableField(exist = false)
    private String materialNames;
}
