package com.aiurt.boot.modules.fault.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("fault")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault对象", description="故障表")
public class Fault {

	/**主键id,自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id,自动递增")
	private Long id;

	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
	private String code;

	/**线路编号*/
	@Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
	private String lineCode;

	/**站点编号*/
	@Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
	private String stationCode;

	/**故障设备编号集合*/
	@Excel(name = "故障设备编号集合", width = 15)
    @ApiModelProperty(value = "故障设备编号集合")
	private String devicesIds;

	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
	private String faultPhenomenon;

	/**故障类型*/
	@Excel(name = "故障类型", width = 15)
    @ApiModelProperty(value = "故障类型")
	private Integer faultType;

	/**状态：0-新报修 1-维修中 2-维修完成*/
	@Excel(name = "状态：0-新报修 1-维修中 2-维修完成", width = 15)
    @ApiModelProperty(value = "状态：0-新报修 1-维修中 2-维修完成")
	private Integer status;

	/**指派状态：0-未指派 1-已指派未填写 2-已指派已填写*/
	@Excel(name = "指派状态：0-未指派 1-已指派未填写 2-已指派已填写", width = 15)
	@ApiModelProperty(value = "指派状态：0-未指派 1-已指派未填写 2-已指派已填写")
	private Integer assignStatus;

	/**故障级别：1-普通故障 2-重大故障*/
	@Excel(name = "故障级别：1-普通故障 2-重大故障", width = 15)
	@ApiModelProperty(value = "故障级别：1-普通故障 2-重大故障")
	private Integer faultLevel;

	/**故障位置*/
	@Excel(name = "故障位置", width = 15)
	@ApiModelProperty(value = "故障位置")
	private String location;

	/**故障详细位置*/
	@Excel(name = "故障详细位置", width = 15)
	@ApiModelProperty(value = "故障详细位置")
	private String detailLocation;

	/**影响范围*/
	@Excel(name = "影响范围", width = 15)
	@ApiModelProperty(value = "影响范围")
	private String scope;

	/**发生时间*/
	@Excel(name = "发生时间", width = 15)
	@ApiModelProperty(value = "发生时间")
	private Date occurrenceTime;

	/**系统编号*/
	@Excel(name = "系统编号", width = 15)
    @ApiModelProperty(value = "系统编号")
	private String systemCode;

	/**删除状态：0.未删除 1已删除*/
	@Excel(name = "删除状态：0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态：0.未删除 1已删除")
	@TableLogic
	private Integer delFlag;

	/**挂起状态：0.未挂起 1挂起*/
	@Excel(name = "挂起状态：0.未挂起 1挂起", width = 15)
	@ApiModelProperty(value = "挂起状态：0.未挂起 1挂起")
	private Integer hangState;

	/**挂起说明*/
	@Excel(name = "挂起说明", width = 15)
	@ApiModelProperty(value = "挂起说明")
	private String remark;

	@Excel(name = "报修方式", width = 15)
	@ApiModelProperty(value = "报修方式")
	private String repairWay;

	/**报修编号*/
	@Excel(name = "报修编号", width = 15)
	@ApiModelProperty(value = "报修编号")
	private String repairCode;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;

	/**创建时间，CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
	private Date createTime;

	/**修改时间，根据当前时间戳更新*/
	@Excel(name = "修改时间，根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
	private Date updateTime;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String orgId;

	public static final String ID = "id";

	public static final String CODE = "code";
}
