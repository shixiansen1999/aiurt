package com.aiurt.modules.weeklyplan.entity;

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

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: bd_overhaul_device_report
 * @Author: jeecg-boot
 * @Date:   2021-05-22
 * @Version: V1.0
 */
@Data
@TableName("bd_overhaul_device_report")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_overhaul_device_report对象", description="bd_overhaul_device_report")
public class BdOverhaulDeviceReport implements Serializable {
    private static final long serialVersionUID = 1L;

	/**检修设备报告表*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "检修设备报告表")
    private Integer id;
	/**对应指派记录表ht_overhaul_report的id*/
	@Excel(name = "对应指派记录表ht_overhaul_report的id", width = 15)
    @ApiModelProperty(value = "对应指派记录表ht_overhaul_report的id")
    private Integer overhaulReportId;
	/**设备所在站点id*/
	@Excel(name = "设备所在站点id", width = 15)
    @ApiModelProperty(value = "设备所在站点id")
    private String stationId;
	/**设备id*/
	@Excel(name = "设备id", width = 15)
    @ApiModelProperty(value = "设备id")
    private Integer deviceArchiveId;
	/**对应检修项表ht_overhaul_content_item的ids*/
	@Excel(name = "对应检修项表ht_overhaul_content_item的ids", width = 15)
    @ApiModelProperty(value = "对应检修项表ht_overhaul_content_item的ids")
    private String overhaulContentItemIds;
	/**对应每个巡检项的状态值*/
	@Excel(name = "对应每个巡检项的状态值", width = 15)
    @ApiModelProperty(value = "对应每个巡检项的状态值")
    private String states;
	/**上传记录的时间*/
	@Excel(name = "上传记录的时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "上传记录的时间")
    private Date dateTime;
	/**审批人审批的状态 0 未审批 1审批通过 2审批不通过*/
	@Excel(name = "审批人审批的状态 0 未审批 1审批通过 2审批不通过", width = 15)
    @ApiModelProperty(value = "审批人审批的状态 0 未审批 1审批通过 2审批不通过")
    private Integer formState;
	/**检修staff们的id*/
	@Excel(name = "检修staff们的id", width = 15)
    @ApiModelProperty(value = "检修staff们的id")
    private String userIds;
	/**审批人id*/
	@Excel(name = "审批人id", width = 15)
    @ApiModelProperty(value = "审批人id")
    private String approvalUserId;
	/**remark*/
	@Excel(name = "remark", width = 15)
    @ApiModelProperty(value = "remark")
    private String remark;
	/**confirmDateTime*/
	@Excel(name = "confirmDateTime", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "confirmDateTime")
    private Date confirmDateTime;

    @ApiModelProperty("检修语音，多个已逗号分开")
    @TableField(exist = false)
    private String voice;
    @ApiModelProperty("检修图片，多个已逗号分开")
    @TableField(exist = false)
    private String picture;
    @ApiModelProperty("检修视频，多个已逗号分开")
    @TableField(exist = false)
    private String video;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
}
