package com.aiurt.boot.common.result;

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
import java.util.List;

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
public class FaultResult {


//	{"线路", "故障编号", "站点"
//			,"班组", "系统", "设备"
//			,"故障现象", "报修方式", "故障发生时间"
//			, "维修完成时间", "状态", "维修人"
//			, "挂起说明", "报修编号", "故障影响范围"
//			, "故障分析", "维修措施"};

	/**主键id,自动递增*/
	private Long id;

	/**线路编号*/
	@ApiModelProperty(value = "线路编号")
	private String lineCode;

	/**线路*/
	@Excel(name = "线路", width = 15)
	@ApiModelProperty(value = "线路")
	private String lineName;

	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号", width = 15)
	@ApiModelProperty(value = "故障编号")
	private String code;

	/**站点*/
	@Excel(name = "线路站点", width = 15)
	@ApiModelProperty(value = "站点")
	private String station;

	/**班组*/
	@Excel(name = "班组", width = 15)
	@ApiModelProperty(value = "班组")
	private String departName;

	/**系统名称*/
	@Excel(name = "系统", width = 15)
	@ApiModelProperty(value = "系统")
	private String systemName;

	/**故障设备编号集合*/
	@Excel(name = "设备", width = 15)
	@ApiModelProperty(value = "设备")
	private String device;

	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
	@ApiModelProperty(value = "故障现象")
	private String faultPhenomenon;

	/**报修方式*/
	@Excel(name = "报修方式", width = 15)
	@ApiModelProperty(value = "报修方式")
	private String repairWay;

	/**创建人姓名*/
	@ApiModelProperty(value = "填报人")
	private String createByName;

	/**发生时间*/
	@Excel(name = "故障发生时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "故障发生时间")
	private Date occurrenceTime;

	/**维修完成时间，CURRENT_TIMESTAMP*/
	@Excel(name = "维修完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "维修完成时间")
	private Date overTime;

	/**状态：0-新报修 1-维修中 2-维修完成*/
	@Excel(name = "状态", width = 15)
	@ApiModelProperty(value = "状态")
	private String statusDesc;

	/**维修人*/
	@Excel(name = "维修人", width = 15)
	@ApiModelProperty(value = "维修人")
	private String repairUserName;

	/**挂起说明*/
	@Excel(name = "挂起说明", width = 15)
	@ApiModelProperty(value = "挂起说明")
	private String remark;

	/**报修编号*/
	@Excel(name = "报修编号", width = 15)
	@ApiModelProperty(value = "报修编号")
	private String repairCode;

	/**影响范围*/
	@Excel(name = "故障影响范围", width = 15)
	@ApiModelProperty(value = "故障影响范围")
	private String scope;

	/**故障分析*/
	@Excel(name = "故障分析", width = 15)
	@ApiModelProperty(value = "故障分析")
	private String faultAnalysis;

	/**维修措施*/
	@Excel(name = "维修措施", width = 15)
	@ApiModelProperty(value = "维修措施")
	private String maintenanceMeasures;

	/**解决方案*/
	@ApiModelProperty(value = "解决方案")
	private String solution;

	/**故障原因*/
	@ApiModelProperty(value = "故障原因")
	private String faultReason;

	/**班组id*/
	@ApiModelProperty(value = "班组id")
	private String teamId;

	/**站点编号*/
	@ApiModelProperty(value = "站点编号")
	private String stationCode;

	/**故障设备编号集合*/
	@ApiModelProperty(value = "故障设备编号集合")
	private String devicesIds;

	/**故障类型*/
	@ApiModelProperty(value = "故障类型")
	private Integer faultType;

	/**故障类型*/
	@ApiModelProperty(value = "故障类型")
	private String faultTypeDesc;

	/**状态：0-新报修 1-维修中 2-维修完成*/
	@ApiModelProperty(value = "状态：0-新报修 1-维修中 2-维修完成")
	private Integer status;

	/**指派状态：0-未指派 1-指派 2-重新指派*/
	@ApiModelProperty(value = "指派状态：0-未指派 1-指派 2-重新指派")
	private Integer assignStatus;

	/**故障级别：1-普通故障 2-重大故障*/
	@ApiModelProperty(value = "故障级别：1-普通故障 2-重大故障")
	private Integer faultLevel;

	/**故障级别描述：1-普通故障 2-重大故障*/
	@ApiModelProperty(value = "故障级别描述：1-普通故障 2-重大故障")
	private String faultLevelDesc;

	/**故障位置*/
	@ApiModelProperty(value = "故障位置")
	private String location;

	/**系统编号*/
	@ApiModelProperty(value = "系统编号")
	private String systemCode;

	/**删除状态：0.未删除 1已删除*/
	@ApiModelProperty(value = "删除状态：0.未删除 1已删除")
	private Integer delFlag;

	/**挂起状态：0.未挂起 1挂起*/
	@ApiModelProperty(value = "挂起状态：0.未挂起 1挂起")
	private Integer hangState;

	/**创建人*/
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**修改人*/
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**创建时间，CURRENT_TIMESTAMP*/
	private Date createTime;

	/**修改时间，根据当前时间戳更新*/
	private Date updateTime;

	/**附件列表*/
	public List<String> urlList;

	/**故障报修时长*/
	private String timeCost;
}
