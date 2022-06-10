package com.aiurt.boot.modules.repairManage.vo.export;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mr.zhao
 * @date 2022/3/23 9:48
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RepairTaskExportVO implements Serializable {

	private static final long serialVersionUID = 1L;


	@Excel(name = "线路",width = 30)
	@TableField(exist = false)
	@ApiModelProperty(value = "线路")
	private String lineName;

	/**
	 * 编号,示例:JX20211105
	 */
	@Excel(name = "任务编号", width = 15)
	@ApiModelProperty(value = "编号,示例:JX20211105 ")
	private String code;


	@Excel(name = "检修周", width = 15)
	@ApiModelProperty(value = "周数")
	private String weeks;

	//@Excel(name = "适用站点", width = 15,replace = {"控制中心_1","车辆段_2","班组_3"})
	@ApiModelProperty(value = "类型(1-控制中心 2-车辆段 3-班组)")
	private Integer icType;

	@TableField(exist = false)
	@Excel(name = "适用站点", width = 15)
	@ApiModelProperty(value = "站点")
	private String stationName;


	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "开始时间")
	private Date startTime;

	/**
	 * 结束时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "结束时间")
	private Date endTime;

	@Excel(name = "班组",width = 30)
	@TableField(exist = false)
	@ApiModelProperty(value = "检修班组")
	private String teamName;




	@Excel(name = "检修人", width = 15)
	@ApiModelProperty(value = "检修人，检修人names")
	private String staffNames;

	/**
	 * 提交时间
	 */
	@Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "提交时间")
	private Date submitTime;

	/**
	 * 检修状态：0.未检修
	 * 1.已检修
	 * 2.确认
	 * 3.不予确认
	 * 4.验收
	 * 5.不予验收
	 */
	@Excel(name = "状态", width = 15,replace = {"未检修_0","确认中_1","已确认_2","不予确认_3","已验收_4","不予验收_5"})
	@ApiModelProperty(value = "检修状态 0.未检修 1.已检修 2.确认 3.不予确认 4.验收 5.不予验收")
	private Integer status;

	/**
	 * 确认人
	 */
	@Excel(name = "确认人", width = 15)
	@ApiModelProperty(value = "确认人")
	private String confirmUserName;

	/**
	 * 确认时间
	 */
	@Excel(name = "确认时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "确认时间")
	private Date confirmTime;

	/**
	 * 验收人
	 */
	@Excel(name = "验收人", width = 15)
	@ApiModelProperty(value = "验收人")
	private String receiptUserName;

	/**
	 * 验收时间
	 */
	@Excel(name = "验收时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "验收时间")
	private Date receiptTime;

	//@ApiModelProperty(value = "验收签名url")
	//@Excel(name = "验收签名",type = 2)
	//private String receiptUrl;


	@Excel(name = "查看", width = 15 ,type = 5)
	@TableField(exist = false)
	private String des;


}
