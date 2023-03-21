package com.aiurt.modules.schedule.entity;

import com.aiurt.modules.schedule.model.ScheduleRecordModel;
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
 * @Description: schedule
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "人员排班对象", description = "人员排班对象")
public class Schedule {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 排班人员id
     */
    @Excel(name = "排班人员id", width = 15)
    @ApiModelProperty(value = "排班人员id")
    private String userId;

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

    /**
     * 规则id
     */
    @Excel(name = "规则id", width = 15)
    @ApiModelProperty(value = "规则id")
    private Integer ruleId;

    /**
     * 0 排班 1确认排班
     */
    @Excel(name = "0 排班 1确认排班", width = 15)
    @ApiModelProperty(value = "0 排班 1确认排班")
    private Integer status;

    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     *是否跳过周末
     */
    @Excel(name = "是否跳过周末", width = 15)
    @ApiModelProperty(value = "是否跳过周末")
    private Boolean isSkipWeekend;

    /**
     *节假日是否调整
     */
    @Excel(name = "节假日是否调整", width = 15)
    @ApiModelProperty(value = "节假日是否调整")
    private Boolean isHolidayAdjustment;

    /**
     * 调整前班次id
     */
    @Excel(name = "调整前班次id", width = 15)
    @ApiModelProperty(value = "调整前班次id")
    private Integer beforeItemId;

    /**
     * 调整后班次id
     */
    @Excel(name = "调整后班次id", width = 15)
    @ApiModelProperty(value = "调整后班次id")
    private Integer afterItemId;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private List<ScheduleRecordModel> item;

    @TableField(exist = false)
    private List<String> userIds;

    @TableField(exist = false)
    private String orgCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM")
    @DateTimeFormat(pattern = "yyyy-MM")
    @TableField(exist = false)
    private Date date;

    @TableField(exist = false)
    @ApiModelProperty(value = "班组名称")
    private String orgName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工号")
    private String workNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "查询条件")
    private String text;

    @TableField(exist = false)
    private List<ScheduleRuleItem> scheduleRuleItems;

}
