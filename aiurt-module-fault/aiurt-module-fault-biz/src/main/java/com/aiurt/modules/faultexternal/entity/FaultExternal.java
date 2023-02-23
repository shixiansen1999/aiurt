package com.aiurt.modules.faultexternal.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class FaultExternal implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private Integer id;
	/**主键*/
	@Excel(name = "主键", width = 15)
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
	@Excel(name = "发生时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
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
	@Excel(name = "报修时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
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
	@Excel(name = "status", width = 15)
    @ApiModelProperty(value = "status")
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
	@Excel(name = "urls", width = 15)
    @ApiModelProperty(value = "urls")
    private String urls;

    @Excel(name = "fault_code", width = 15)
    @ApiModelProperty(value = "faultcode")
    private String faultcode;


}
