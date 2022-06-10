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
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="schedule_log对象", description="schedule_log")
public class ScheduleLog {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Integer  id;

	/**排班人员id*/
	@Excel(name = "排班人员id", width = 15)
    @ApiModelProperty(value = "排班人员id")
	private  Integer  userId;

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


}
