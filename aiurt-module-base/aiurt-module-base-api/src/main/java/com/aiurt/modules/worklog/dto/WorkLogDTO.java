package com.aiurt.modules.worklog.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Data
@TableName("t_work_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="t_work_log对象", description="工作日志")
public class WorkLogDTO implements Serializable {

    /**主键id,自动递增*/
    @ApiModelProperty(value = "主键id,自动递增")
    private  String  id;

    /**保存状态:0.保存 1.提交 2.确认 3.审阅*/
    @ApiModelProperty(value = "保存状态:0.保存 1.提交 2.确认 3.审阅")
    private  Integer  status;

    /**提交人id*/
    @ApiModelProperty(value = "提交人id")
    private  String  submitId;

    /**提交时间*/
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    /**日期*/
    @ApiModelProperty(value = "日期")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private  Date  logTime;

    /**工作内容*/
    @ApiModelProperty(value = "工作内容")
    private  Object  workContent;

    /**交接班内容*/
    @ApiModelProperty(value = "交接班内容")
    private  Object  content;

    /**接班人id*/
    @ApiModelProperty(value = "接班人id")
    private  String  succeedId;

    /**审批人*/
    @ApiModelProperty(value = "审批人")
    private  String  approverId;

    /**配合施工时间*/
    @Excel(name = "配合施工时间", width = 15)
    @ApiModelProperty(value = "配合施工时间")
    private  String  assortTime;

    /**配合施工地点*/
    @Excel(name = "配合施工地点", width = 15)
    @ApiModelProperty(value = "配合施工地点")
    private  String  assortLocation;

    /**配合施工单位*/
    @Excel(name = "配合施工单位", width = 15)
    @ApiModelProperty(value = "配合施工单位")
    private  String  assortUnit;

    /**配合施工参与人*/
    @Excel(name = "配合施工参与人", width = 15)
    @ApiModelProperty(value = "配合施工参与人")
    private  String  assortIds;

    /**配合施工人次*/
    @Excel(name = "配合施工人次", width = 15)
    @ApiModelProperty(value = "配合施工人次")
    private  Integer  assortNum;

    /**配合施工内容*/
    @Excel(name = "配合施工内容", width = 15)
    @ApiModelProperty(value = "配合施工内容")
    private  Object  assortContent;

    /**附件列表*/
    @ApiModelProperty(value = "附件列表")
    public String urlList;

    /**签名*/
    @Excel(name = "签名", width = 15)
    @ApiModelProperty(value = "签名")
    private String signature;

}
