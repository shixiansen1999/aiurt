package com.aiurt.modules.schedule.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: schedule_item
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule_item")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="排班班次对象", description="排班班次对象")
public class ScheduleItem {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Integer  id;

	/**班次名称*/
	@Excel(name = "班次名称", width = 15)
    @ApiModelProperty(value = "班次名称")
	private  String  name;

	/**开始时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
	@DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "开始时间")
	private  Date  startTime;

	/**结束时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
	@DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "结束时间")
	private  Date  endTime;

	/**标识*/
	@Excel(name = "标识", width = 15)
    @ApiModelProperty(value = "标识")
	private  String  remark;

	/**时间标记*/
	@Excel(name = "时间标记（0无，1跨日 非字典值）", width = 15)
    @ApiModelProperty(value = "时间标记（0无，1跨日 非字典值）")
	private  String  timeId;

	/**班次组成*/
	@Excel(name = "班次组成", width = 15)
	@ApiModelProperty(value = "班次组成")
	@TableField(exist = false)
	private  String  composition;

	/**颜色*/
	@Excel(name = "颜色", width = 15)
    @ApiModelProperty(value = "颜色")
	private  String  color;

	/**说明*/
	@Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
	private  String  description;

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
    private static final String NAME = "name";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String REMARK = "remark";
    private static final String COLOR = "color";
    private static final String DESCRIPTION = "description";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
