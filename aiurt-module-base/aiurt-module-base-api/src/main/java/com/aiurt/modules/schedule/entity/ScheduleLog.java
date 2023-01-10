package com.aiurt.modules.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import java.util.List;

/**
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="调班日志对象", description="调班日志对象")
public class ScheduleLog {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Integer  id;

	/**排班人员id*/
	@Excel(name = "排班人员id", width = 15)
    @ApiModelProperty(value = "排班人员id")
	private  String  userId;

	/**排班人员姓名*/
	@Excel(name = "排班人员姓名", width = 15)
    @ApiModelProperty(value = "排班人员姓名")
	private  String  userName;

	@ApiModelProperty(value = "工号")
	private  String  workNo;

	/**排班记录id*/
	@Excel(name = "排班记录id", width = 15)
    @ApiModelProperty(value = "排班记录id")
	private  Integer  recordId;

	/**日期*/
	@Excel(name = "日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
	private  Date  date;

	/**原排班班次id*/
	@Excel(name = "原排班班次id", width = 15)
    @ApiModelProperty(value = "原排班班次id")
	private  Integer  sourceItemId;

	/**原排班班次名称*/
	@Excel(name = "原排班班次名称", width = 15)
    @ApiModelProperty(value = "原排班班次名称")
	private  String  sourceItemName;

	/**调班班次id*/
	@Excel(name = "调班班次id", width = 15)
    @ApiModelProperty(value = "调班班次id")
	private  Integer  targetItemId;

	/**调班班次名称*/
	@Excel(name = "调班班次名称", width = 15)
    @ApiModelProperty(value = "调班班次名称")
	private  String  targetItemName;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  remark;

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

	/**创建人*/
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private  String  createBy;

    private static final String ID = "id";
    private static final String USER_ID = "user_id";
    private static final String RECORD_ID = "record_id";
    private static final String DATE = "date";
    private static final String SOURCE_ITEM_ID = "source_item_id";
    private static final String SOURCE_ITEM_NAME = "source_item_name";
    private static final String TARGET_ITEM_ID = "target_item_id";
    private static final String TARGET_ITEM_NAME = "target_item_name";
    private static final String REMARK = "remark";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";

	@ApiModelProperty(value = "查询条件")
	@TableField(exist = false)
	private  String  text;

	@ApiModelProperty(value = "调班记录")
	@TableField(exist = false)
	private  String  shiftRecord ;

	/**
	 * 开始日期
	 */
	@Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "开始日期")
	private Date startDate;

	/**
	 * 结束日期
	 */
	@Excel(name = "结束日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "结束日期")
	private Date endDate;

	@TableField(exist = false)
	private List<String> userList;
}
