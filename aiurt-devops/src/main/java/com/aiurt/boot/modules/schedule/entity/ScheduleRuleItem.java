package com.aiurt.boot.modules.schedule.entity;

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
 * @Description: schedule_rule_item
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("schedule_rule_item")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "排班规则关联班次对象", description = "排班规则关联班次对象")
public class ScheduleRuleItem {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 规则id
     */
    @Excel(name = "规则id", width = 15)
    @ApiModelProperty(value = "规则id")
    private Integer ruleId;

    /**
     * 班次id
     */
    @Excel(name = "班次id", width = 15)
    @ApiModelProperty(value = "班次id")
    private Integer itemId;

    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer sort;

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
	private String itemName;

    @TableField(exist = false)
    private String color;


    private static final String ID = "id";
    private static final String RULE_ID = "rule_id";
    private static final String ITEM_ID = "item_id";
    private static final String SORT = "sort";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
