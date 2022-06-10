package com.aiurt.boot.modules.schedule.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: schedule
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="schedule对象", description="schedule")
public class Schedule {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Integer  id;

	/**排班人员id*/
	@Excel(name = "排班人员id", width = 15)
    @ApiModelProperty(value = "排班人员id")
	private  Integer  userId;

	/**开始日期*/
	@Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
	private  Date  startDate;

	/**规则id*/
	@Excel(name = "规则id", width = 15)
    @ApiModelProperty(value = "规则id")
	private  Integer  ruleId;

	/**0 排班 1确认排班*/
	@Excel(name = "0 排班 1确认排班", width = 15)
    @ApiModelProperty(value = "0 排班 1确认排班")
	private  Integer  status;

	/**删除标志*/
	@Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
	private  Integer  delFlag;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  Date  createTime;

	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private  Date  updateTime;


    private static final String ID = "id";
    private static final String USER_ID = "user_id";
    private static final String START_DATE = "start_date";
    private static final String RULE_ID = "rule_id";
    private static final String STATUS = "status";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
