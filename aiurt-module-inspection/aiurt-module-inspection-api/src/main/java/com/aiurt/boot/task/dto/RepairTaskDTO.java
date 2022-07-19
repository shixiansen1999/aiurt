package com.aiurt.boot.task.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zwl
 * @Title:
 * @Description:
 * @date 2022/6/2409:24
 */
@Data
public class RepairTaskDTO {


    /**检修任务id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务id")
    private String taskId;

    /**
     * 开始检修时间起始
     */
    @ApiModelProperty(value = "开始检修时间起始")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @TableField(exist = false)
    private Date startTimeBegin;

    /**
     * 开始检修时间
     */
    @ApiModelProperty(value = "开始检修时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date startTime;

    /**
     * 开始检修时间结束
     */
    @ApiModelProperty(value = "开始检修时间结束")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @TableField(exist = false)
    private Date startTimeEnd;

    /**检修任务编号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务编号")
    private String taskCode;

    /**检修任务状态*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务单状态")
    private String taskStatus;

    /** 检修任务状态名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务单状态名称")
    private String taskStatusName;

    /**
     * 检修时长
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "检修时长")
    private Long duration;

    /**检修单id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单id")
    private String deviceId;

    /**检修任务标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务标准id")
    private String standardId;

    /**检修结果id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果id")
    private String resultId;

    /**提交人id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "提交人id")
    private String overhaulId;

    /**提交人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "提交人名称")
    private String overhaulName;

    /**设备编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备编码")
    private String equipmentCode;

    /**设备名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "设备名称")
    private String equipmentName;


    /**专业编码*/
    @ApiModelProperty(value = "专业编码")
    @TableField(exist = false)
    private String majorCode;


    /**专业名称*/
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private String majorName;


    /**系统编码*/
    @ApiModelProperty(value = "系统编码")
    @TableField(exist = false)
    private String systemCode;

    /**专业名称*/
    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;


    /**设备类型编码*/
    @ApiModelProperty(value = "设备类型编码")
    @TableField(exist = false)
    private String deviceTypeCode;

    /**设备类型名称*/
    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String deviceTypeName;

    /**检修结果*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果")
    private Integer maintenanceResults;


    /**检修结果名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果名称")
    private String maintenanceResultsName;


    /**检修单号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单号")
    private String overhaulCode;

    /**检修标准名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修标准名称")
    private String overhaulStandardName;

    /**正常项*/
    @ApiModelProperty(value = "正常项")
    @TableField(exist = false)
    private Integer normal;

    /**异常项*/
    @ApiModelProperty(value = "异常项")
    @TableField(exist = false)
    private Integer abnormal;

    /**同行人名称*/
    @ApiModelProperty(value = "同行人名称")
    @TableField(exist = false)
    private String peerName;

    /**同行人Id*/
    @ApiModelProperty(value = "同行人Id")
    @TableField(exist = false)
    private String peerId;

    /**提交时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    @TableField(exist = false)
    private java.util.Date submitTime;

    /**设备位置*/
    @ApiModelProperty(value = "设备位置")
    @TableField(exist = false)
    private String equipmentLocation;

    /**是否已提交，0未提交1已提交*/
    @TableField(exist = false)
    @ApiModelProperty(value = "是否已提交，0未提交1已提交")
    private Integer isSubmit;

    /**未开始*/
    @TableField(exist = false)
    @ApiModelProperty(value = "未开始")
    private Integer notStarted;

    /**进行中*/
    @TableField(exist = false)
    @ApiModelProperty(value = "进行中")
    private Integer haveInHand;

    /**已提交*/
    @TableField(exist = false)
    @ApiModelProperty(value = "已提交")
    private Integer submitted;

    /**是否与设备相关，0否1是*/
    @TableField(exist = false)
    @ApiModelProperty(value = "是否与设备相关，0否1是")
    private Integer isAppointDevice;

    /**搜索*/
    @ApiModelProperty(value = "搜索")
    @TableField(exist = false)
    private String search;
}
