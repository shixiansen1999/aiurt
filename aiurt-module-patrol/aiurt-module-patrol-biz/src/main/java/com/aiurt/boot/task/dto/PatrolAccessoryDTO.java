package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/1
 * @desc
 */
@Data
public class PatrolAccessoryDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**巡检任务设备关联表ID*/
    @Excel(name = "巡检任务设备关联表ID", width = 15)
    @ApiModelProperty(value = "巡检任务设备关联表ID")
    private java.lang.String taskDeviceId;
    /**巡检检查结果表ID*/
    @Excel(name = "巡检检查结果表ID", width = 15)
    @ApiModelProperty(value = "巡检检查结果表ID")
    private java.lang.String checkResultId;
    /**附件名称*/
    @Excel(name = "附件名称", width = 15)
    @ApiModelProperty(value = "附件名称")
    private java.lang.String name;
    /**附件地址*/
    @Excel(name = "附件地址", width = 15)
    @ApiModelProperty(value = "附件地址")
    private java.lang.String address;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**删除状态： 0未删除 1已删除*/
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
}
