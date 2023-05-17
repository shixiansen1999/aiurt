package com.aiurt.modules.sensorinformation.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: sensor_information
 * @Author: aiurt
 * @Date:   2023-05-15
 * @Version: V1.0
 */
@Data

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SensorInformationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**线路code*/
    @Excel(name = "线路code", width = 15)
    @ApiModelProperty(value = "线路code")
    private String lineCode;
	/**线路名称*/

    @ApiModelProperty(value = "线路名称")
    private String lineName;
	/**站点code*/
    @Excel(name = "站点code", width = 15)
    @ApiModelProperty(value = "站点code")
    private String stationCode;
	/**站点名称*/
    @ApiModelProperty(value = "站点名称")
    private String stationName;
	/**站点ip*/
	@Excel(name = "对应IP地址", width = 15)
    @ApiModelProperty(value = "站点ip")
    private String stationIp;
	/**子网掩码*/
	@Excel(name = "子网掩码", width = 15)
    @ApiModelProperty(value = "子网掩码")
    private String subnetMask;
	/**网关地址*/
	@Excel(name = "网关地址", width = 15)
    @ApiModelProperty(value = "网关地址")
    private String gatewayAddress;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**删除标志（0：未删除，1：已删除）*/

    @ApiModelProperty(value = "删除标志（0：未删除，1：已删除）")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**错误原因*/
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    private java.lang.String wrongReason;
}
