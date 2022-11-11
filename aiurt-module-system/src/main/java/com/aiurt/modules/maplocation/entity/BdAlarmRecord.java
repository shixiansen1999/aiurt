package com.aiurt.modules.maplocation.entity;

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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: bd_alarm_record
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Data
@TableName("bd_alarm_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_alarm_record对象", description="bd_alarm_record")
public class BdAlarmRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**报警记录表主键id，自增*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "报警记录表主键id，自增")
    private Integer id;
	/**报警巡检人员id，对应ht_staff表id*/
	@Excel(name = "报警巡检人员id，对应ht_staff表id", width = 15)
    @ApiModelProperty(value = "报警巡检人员id，对应ht_staff表id")
    private String staffId;
	/**报警类型（0未登录系统（非报错无alarm）1离线2越界3不在线迟到4(正常（正常）)5在线迟到（没干活））*/
	@Excel(name = "报警类型（0未登录系统（非报错无alarm）1离线2越界3不在线迟到4(正常（正常）)5在线迟到（没干活））", width = 15)
    @ApiModelProperty(value = "报警类型（0未登录系统（非报错无alarm）1离线2越界3不在线迟到4(正常（正常）)5在线迟到（没干活））")
    private Integer alarmType;
	/**报警开始时间*/
	@Excel(name = "报警开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "报警开始时间")
    private  String startTime;
	/**报警结束时间（null表示当前仍在报警）*/
	@Excel(name = "报警结束时间（null表示当前仍在报警）", width = 15, format = "yyyy-MM-dd  HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "报警结束时间（null表示当前仍在报警）")
    private  String endTime;
	/**任务类型（1巡检）*/
	@Excel(name = "任务类型（1巡检）", width = 15)
    @ApiModelProperty(value = "任务类型（1巡检）")
    private Integer taskType;
	/**关联巡检任务id（注：巡检指派id，非巡检记录id）*/
	@Excel(name = "关联巡检任务id（注：巡检指派id，非巡检记录id）", width = 15)
    @ApiModelProperty(value = "关联巡检任务id（注：巡检指派id，非巡检记录id）")
    private Integer taskId;
	/**报警状态（备用）*/
	@Excel(name = "报警状态（备用）", width = 15)
    @ApiModelProperty(value = "报警状态（备用）")
    private Integer alarmStatus;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	// 查询条件使用机构id
    @TableField(exist = false)
    private String teamId;
}
