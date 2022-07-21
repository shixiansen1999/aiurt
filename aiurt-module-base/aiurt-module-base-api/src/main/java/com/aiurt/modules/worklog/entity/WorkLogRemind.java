package com.aiurt.modules.worklog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */

@Data
@TableName("t_work_log_remind")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="t_work_log_remind对象", description="工作日志提醒时间")
public class WorkLogRemind {

    /**主键id,自动递增*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id,自动递增")
    private  Long  id;

    /**提醒时间*/
    @Excel(name = "提醒时间", width = 20)
    @ApiModelProperty(value = "提醒时间")
    @NotNull(message = "提醒时间不能为空")
    private LocalTime remindTime;

    /**提醒内容*/
    @Excel(name = "提醒内容", width = 15)
    @ApiModelProperty(value = "提醒内容")
    private  String  content;

    /**班组id*/
    @Excel(name = "班组id", width = 15)
    @ApiModelProperty(value = "班组id")
    private  String  orgId;

    /**班组名*/
    @Excel(name = "班组名", width = 15)
    @ApiModelProperty(value = "班组名")
    private  String  orgName;

    /**时间表达式*/
    @Excel(name = "表达式", width = 15)
    @ApiModelProperty(value = "表达式")
    private  String  cron;

    /**时间表达式-夜班*/
    @Excel(name = "表达式-夜班", width = 15)
    @ApiModelProperty(value = "表达式-夜班")
    private  String  cronNight;

    /**删除状态:0.未删除 1已删除*/
    @Excel(name = "删除状态:0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态:0.未删除 1已删除")
    @TableLogic
    private  Integer  delFlag;

    /**创建时间,CURRENT_TIMESTAMP*/
    @Excel(name = "创建时间,CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间,CURRENT_TIMESTAMP")
    private Date createTime;

    /**修改时间,根据当前时间戳更新*/
    @Excel(name = "修改时间,根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间,根据当前时间戳更新")
    private  Date  updateTime;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private  String  createBy;

    /**修改人*/
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private  String  updateBy;

    public static final String ORG_ID = "org_id";

    /**夜班提醒时间*/
    @Excel(name = "提醒时间", width = 20)
    @ApiModelProperty(value = "夜班提醒时间")
    @NotNull(message = "夜班提醒时间不能为空")
    private LocalTime remindTimeNight;
}
