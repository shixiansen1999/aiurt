package com.aiurt.modules.faultalarm.entity;

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
 * @author:wgp
 * @create: 2023-06-05 09:36
 * @Description: 告警记录实体
 */
@Data
@TableName("on_alm_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "on_alm_record对象", description = "告警记录对象")
public class AlmRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    private String id;

    @ApiModelProperty(value = "告警发生时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date almTime;

    @ApiModelProperty(value = "处理状态")
    private Integer state;

    @ApiModelProperty(value = "告警级别")
    private Integer level;

    @ApiModelProperty(value = "告警文本")
    private String almText;

    @ApiModelProperty(value = "设备ID")
    private String equipmentGuid;

    @ApiModelProperty(value = "最后告警时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastAlarmTime;

    @ApiModelProperty(value = "处理说明")
    private String dealRemark;

    @ApiModelProperty(value = "处理时间(yyyy-MM-dd)")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dealDateTime;

    @ApiModelProperty(value = "处理人ID")
    private String dealUserId;

    @ApiModelProperty(value = "如果取消告警，记录取消时间+30分钟到此字段")
    private Date timeAfter30Minutes;

    @ApiModelProperty(value = "告警重复次数")
    private Integer almNum;

    @ApiModelProperty(value = "工单编号")
    private String faultCode;

    @Excel(name = "删除状态，0.未删除 1.已删除", width = 15)
    @ApiModelProperty(value = "删除状态，0.未删除 1.已删除", required = false)
    private java.lang.Integer delFlag;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "创建人")
    private String createBy;
}
