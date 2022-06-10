package com.aiurt.boot.modules.schedule.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: schedule_record
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "排班记录对象", description = "排班记录对象")
public class ScheduleRecord {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 排班用户id
     */
    @Excel(name = "排班用户id", width = 15)
    @ApiModelProperty(value = "排班用户id")
    private String userId;

    /**
     * 排班表id
     */
    @Excel(name = "排班表id", width = 15)
    @ApiModelProperty(value = "排班表id")
    private Integer scheduleId;

    /**
     * 日期
     */
    @Excel(name = "日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
    private Date date;

    /**
     * 班次id
     */
    @Excel(name = "班次id", width = 15)
    @ApiModelProperty(value = "班次id")
    private Integer itemId;

    /**
     * 班次名称
     */
    @Excel(name = "班次名称", width = 15)
    @ApiModelProperty(value = "班次名称")
    private String itemName;

    /**
     * 上班时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "上班时间")
    private Date startTime;

    /**
     * 下班时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "下班时间")
    private Date endTime;

    /**
     * 颜色
     */
    @Excel(name = "颜色", width = 15)
    @ApiModelProperty(value = "颜色")
    private String color;

    /**
     * 删除标志
     */
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

    @TableField(exist = false)
    private Integer excelIndex;
}
