package com.aiurt.boot.common.result;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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

	/**主键id,自动递增*/
	private Long id;
	/**故障编号，示例：G101.2109.001*/
	private String code;
	/**线路编号*/
	private String lineCode;
	/**站点编号*/
	private String stationCode;
	/**故障设备编号集合*/
	private String devicesIds;
	/**故障设备集合*/
	private String devices;
	/**故障设备编号集合*/
	private String device;
	/**故障现象*/
	private String faultPhenomenon;
	/**报修方式*/
	private String repairWay;
	/**故障类型*/
	private Integer faultType;
	/**状态：0-新报修 1-维修中 2-维修完成*/
	private Integer status;
	/**状态：0-新报修 1-维修中 2-维修完成*/
	private String statusDesc;
	/**状态：0-新报修 1-维修中 2-维修完成*/
	private Integer assignStatus;
	/**故障级别：1-普通故障 2-重大故障*/
	private Integer faultLevel;
	/**故障位置*/
	private String location;
	/**影响范围*/
	private String scope;
	/**发生时间*/
	private Date occurrenceTime;
	/**系统编号*/
	private String systemCode;
	/**删除状态：0.未删除 1已删除*/
	private Integer delFlag;
	/**挂起状态：0.未挂起 1挂起*/
	private Integer hangState;
	/**备注*/
	private String remark;
	/**创建人*/
	private String createBy;
	/**修改人*/
	private String updateBy;
	/**创建时间，CURRENT_TIMESTAMP*/
	private Date createTime;
	/**修改时间，根据当前时间戳更新*/
	private Date updateTime;
	/***/
	public List<String> urlList;
	/**解决方案*/
	private String solution;
	/**故障原因*/
	private String faultReason;
}
