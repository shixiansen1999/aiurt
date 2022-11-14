package com.aiurt.boot.weeklyplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * @Description: bd_overhaul_report
 * @Author: jeecg-boot
 * @Date:   2021-05-17
 * @Version: V1.0
 */
@Data
@TableName("bd_overhaul_report")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_overhaul_report对象", description="bd_overhaul_report")
public class BdOverhaulReport implements Serializable {
    private static final long serialVersionUID = 1L;

	/**指派检修记录表*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "指派检修记录表")
    private Integer id;
	/**指派者的id*/
	@Excel(name = "指派者的id", width = 15)
    @ApiModelProperty(value = "指派者的id")
    private String assignStaffId;
	/**指派检修的时间*/
	@Excel(name = "指派检修的时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "指派检修的时间")
    private java.util.Date assignTime;
	/**指派该站点需要检修的设备们的ids*/
	@Excel(name = "指派该站点需要检修的设备们的ids", width = 15)
    @ApiModelProperty(value = "指派该站点需要检修的设备们的ids")
    private String deviceArchiveIds;
	/**指派干活的staffs的ids*/
	@Excel(name = "指派干活的staffs的ids", width = 15)
    @ApiModelProperty(value = "指派干活的staffs的ids")
    private String staffIds;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**指派记录生成的时间*/
	@Excel(name = "指派记录生成的时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "指派记录生成的时间")
    private java.util.Date dateTime;
	/**站点id*/
	@Excel(name = "站点id", width = 15)
    @ApiModelProperty(value = "站点id")
    private String stationId;
	@ApiModelProperty(value = "变电所id")
    private String substation;
	/**对应计划令的id，非B2C3的检修必须要关联的计划令*/
	@Excel(name = "对应计划令的id，非B2C3的检修必须要关联的计划令", width = 15)
    @ApiModelProperty(value = "对应计划令的id，非B2C3的检修必须要关联的计划令")
    private Integer operatePlanDeclarationFormId;
	/**检修表状态*/
	@Excel(name = "检修表状态", width = 15)
    @ApiModelProperty(value = "检修表状态")
    private Integer formState;
	/**beginTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "beginTime")
    private java.util.Date beginTime;
	/**endTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "endTime")
    private java.util.Date endTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;


    @Excel(name = "检修语音", width = 15)
    @ApiModelProperty(value = "检修语音")
    private String voice;

    @Excel(name = "检修图片", width = 15)
    @ApiModelProperty(value = "'检修图片'")
    private String picture;

    @Excel(name = "检修视频", width = 15)
    @ApiModelProperty(value = "检修视频")
    private String video;

    @Excel(name = "工作票编号", width = 15)
    @ApiModelProperty(value = "工作票编号")
    private String workTicketId;

    @Excel(name = "工作票图片", width = 15)
    @ApiModelProperty(value = "工作票图片")
    private String workTicketPicture;

}
