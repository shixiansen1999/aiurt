package com.aiurt.boot.task.dto;

import com.aiurt.boot.plan.entity.RepairPoolCodeContent;
import com.aiurt.boot.task.entity.RepairTaskResult;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author zwl
 */
@Data
public class CheckListDTO {

    /**检修单id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单id")
    private String deviceId;

    /**检修结果id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果id")
    private String resultId;

    /**检修任务标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修任务标准id")
    private String standardId;

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

    /**结束检修时间*/
    @Excel(name = "结束检修时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始检修时间")
    @TableField(exist = false)
    private java.util.Date startTime;

    /**结束检修时间*/
    @Excel(name = "结束检修时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束检修时间")
    @TableField(exist = false)
    private java.util.Date endTime;

    /**同行人*/
    @ApiModelProperty(value = "同行人")
    @TableField(exist = false)
    private String peer;

    /**检修时长*/
    @ApiModelProperty(value = "检修时长")
    @TableField(exist = false)
    private Integer duration;

    /**检修位置*/
    @ApiModelProperty(value = "检修位置")
    @TableField(exist = false)
    private String maintenancePosition;

    /**设备位置*/
    @ApiModelProperty(value = "设备位置")
    @TableField(exist = false)
    private String equipmentLocation;

    /**正常项*/
    @ApiModelProperty(value = "正常项")
    @TableField(exist = false)
    private Integer normal;

    /**异常项*/
    @ApiModelProperty(value = "异常项")
    @TableField(exist = false)
    private Integer abnormal;

    /**检修单名称*/
    @ApiModelProperty(value = "检修单名称")
    @TableField(exist = false)
    private String resultName;

    /**检修单号*/
    @ApiModelProperty(value = "检修单号")
    @TableField(exist = false)
    private String resultCode;

    @ApiModelProperty(value = "检修单（树形）")
    @TableField(exist = false)
    List<RepairTaskResult> repairTaskResultList;

    /**站所编号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站所编号")
    private java.lang.String stationCode;
    /**线路编号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;
    /**位置编号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "位置编号")
    private java.lang.String positionCode;
    /**具体位置*/
    @TableField(exist = false)
    @ApiModelProperty(value = "具体位置")
    private java.lang.String specificLocation;

    /**故障编号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "故障编号")
    private java.lang.String faultCode;

    /**检修项数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修项数量")
    private Integer maintenanceItemsQuantity;

    /**已检修数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "已检修数量")
    private Integer overhauledQuantity;

    /**待检修数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "待检修数量")
    private Integer toBeOverhauledQuantity;

    /**检修单附件*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修单附件")
    private List<String> enclosureUrl;
}
