package com.aiurt.modules.faultexternal.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
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
import java.util.List;

/**
 * @Description: 调度系统故障
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
@Data
@TableName("fault_external")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_external对象", description="调度系统故障")
public class FaultExternal extends DictEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**主键*/
    @ApiModelProperty(value = "主键")
    private Integer indocno;
	/**派修单号*/
	@Excel(name = "派修单号", width = 15)
    @ApiModelProperty(value = "派修单号")
    private String smfcode;
	/**故障编号*/
	@Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号")
    private String sexecode;
	/**发生时间*/
	@Excel(name = "发生时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发生时间")
    private Date dhappen;
	/**线路id*/
	@Excel(name = "线路id", width = 15)
    @ApiModelProperty(value = "线路id")
    private Integer iline;
	/**线路 nm*/
	@Excel(name = "线路 nm", width = 15)
    @ApiModelProperty(value = "线路 nm")
    private String sline;
	/**车站/工区 id*/
	@Excel(name = "车站/工区 id", width = 15)
    @ApiModelProperty(value = "车站/工区 id")
    private Integer ipos;
	/**车站/工区 nm*/
	@Excel(name = "车站/工区 nm", width = 15)
    @ApiModelProperty(value = "车站/工区 nm")
    private String spos;
	/**位置详细描述*/
	@Excel(name = "位置详细描述", width = 15)
    @ApiModelProperty(value = "位置详细描述")
    private String spositiondetail;
	/**设备id*/
	@Excel(name = "设备id", width = 15)
    @ApiModelProperty(value = "设备id")
    private Integer idevice;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private String sdevice;
	/**设备分类id*/
	@Excel(name = "设备分类id", width = 15)
    @ApiModelProperty(value = "设备分类id")
    private Integer ideviceclass;
	/**设备分名称*/
	@Excel(name = "设备分名称", width = 15)
    @ApiModelProperty(value = "设备分名称")
    private String sdeviceclass;
	/**缺陷类型id*/
	@Excel(name = "缺陷类型id", width = 15)
    @ApiModelProperty(value = "缺陷类型id")
    private Integer iphenoid;
	/**缺陷类型名称*/
	@Excel(name = "缺陷类型名称", width = 15)
    @ApiModelProperty(value = "缺陷类型名称")
    private String sphenonm;
	/**缺陷详细描述*/
	@Excel(name = "缺陷详细描述", width = 15)
    @ApiModelProperty(value = "缺陷详细描述")
    private String sproandway;
	/**报修时间*/
	@Excel(name = "报修时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报修时间")
    private Date drepairtime;
	/**紧急程度*/
	@Excel(name = "紧急程度", width = 15)
    @ApiModelProperty(value = "紧急程度")
    private Integer surgencylevel;
	/**处理建议*/
	@Excel(name = "处理建议", width = 15)
    @ApiModelProperty(value = "处理建议")
    private String ssuggest;
	/**受理部门id*/
	@Excel(name = "受理部门id", width = 15)
    @ApiModelProperty(value = "受理部门id")
    private String imajor;
	/**受理部门nm*/
	@Excel(name = "受理部门nm", width = 15)
    @ApiModelProperty(value = "受理部门nm")
    private String smajor;
	/**受理人 id*/
	@Excel(name = "受理人 id", width = 15)
    @ApiModelProperty(value = "受理人 id")
    private String iinform;
	/**受理人 nm*/
	@Excel(name = "受理人 nm", width = 15)
    @ApiModelProperty(value = "受理人 nm")
    private String sinform;
	/**受理人工号*/
	@Excel(name = "受理人工号", width = 15)
    @ApiModelProperty(value = "受理人工号")
    private String sinformworkno;
	/**是否影响行车 1/2*/
	@Excel(name = "是否影响行车 1/2", width = 15)
    @ApiModelProperty(value = "是否影响行车 1/2")
    private String crane;
	/**是否影响客运服务 1/2*/
	@Excel(name = "是否影响客运服务 1/2", width = 15)
    @ApiModelProperty(value = "是否影响客运服务 1/2")
    private String transportservice;
	/**是否停止服务 1/2否*/
	@Excel(name = "是否停止服务 1/2否", width = 15)
    @ApiModelProperty(value = "是否停止服务 1/2否")
    private String stopservice;
    /**是否影响行车*/
    @TableField(exist = false)
    @ApiModelProperty(value = "fault_yn,是否影响行车,1:是,0否,2未知",  required = true)
    @Dict(dicCode = "fault_yn")
    private Integer affectDrive;

    /**是否影响客运服务*/
    @TableField(exist = false)
    @ApiModelProperty(value = "fault_yn,是否影响客运服务,1:是,0否,2未知",  required = true)
    @Dict(dicCode = "fault_yn")
    private Integer affectPassengerService;


    /**是否停止服务*/
    @TableField(exist = false)
    @Dict(dicCode = "fault_yn")
    @ApiModelProperty(value = "fault_yn,是否停止服务,1:是,0否,2未知",  required = true)
    private Integer isStopService;
	/**报修人id*/
	@Excel(name = "报修人id", width = 15)
    @ApiModelProperty(value = "报修人id")
    private Integer ireportuser;
	/**报修人工号*/
	@Excel(name = "报修人工号", width = 15)
    @ApiModelProperty(value = "报修人工号")
    private String sreportworkno;
	/**报修人姓名*/
	@Excel(name = "报修人姓名", width = 15)
    @ApiModelProperty(value = "报修人姓名")
    private String sreportuser;
	/**报修部门id*/
	@Excel(name = "报修部门id", width = 15)
    @ApiModelProperty(value = "报修部门id")
    private Integer ireportdept;
	/**报修部门名称*/
	@Excel(name = "报修部门名称", width = 15)
    @ApiModelProperty(value = "报修部门名称")
    private String sreportdept;
	/**status*/
    @ApiModelProperty(value = "status")
    @Dict(dicCode = "dispatch_fault_status")
    private Integer status;
	/**createTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "createTime")
    private Date createTime;
	/**updateTime*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "updateTime")
    private Date updateTime;
	/**urls*/
    @ApiModelProperty(value = "urls")
    private String urls;

    @Excel(name = "故障表编号", width = 15)
    @ApiModelProperty(value = "故障表编号")
    private String faultcode;

    @TableField(exist = false)
    private List<String> urlList;

    @TableField(exist = false)
    private String lineCode;

    @TableField(exist = false)
    @Dict(dictTable = "cs_station", dicCode = "station_code" , dicText = "station_name")
    private String stationCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "非实体属性-查询传参-站点id")
    private String stationId;

    @TableField(exist = false)
    private List<String> stationCodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "非实体属性-查询传参-开始时间")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "非实体属性-查询传参-结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "故障现象或者编码")
    private String faultPhenomenonOrCode;
}
