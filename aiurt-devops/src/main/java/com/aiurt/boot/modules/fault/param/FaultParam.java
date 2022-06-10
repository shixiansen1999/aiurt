package com.aiurt.boot.modules.fault.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @Author: swsc
 * 故障查询参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class FaultParam {
    /**
     * 系统编号
     */
    @Excel(name = "系统编号", width = 15)
    @ApiModelProperty(value = "系统编号")
    private String systemCode;

    /**
     * 故障编号
     */
    @Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号")
    private String code;

    /**
     * 站点编号
     */
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private String stationCode;

    /**
     * 设备编码
     */
    @Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    private String devicesIds;

    /**
     * 故障现象
     */
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    /**
     * 状态
     */
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 挂起状态
     */
    @Excel(name = "挂起状态", width = 15)
    @ApiModelProperty(value = "挂起状态")
    private String hangState;

    /**
     * 故障类型
     */
    @Excel(name = "故障类型", width = 15)
    @ApiModelProperty(value = "故障类型")
    private Integer faultType;

    /**
     * 登记开始时间
     */
    @Excel(name = "登记开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "登记开始时间")
    private String dayStart;

    /**
     * 登记结束时间
     */
    @Excel(name = "登记结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "登记结束时间")
    private String dayEnd;

    /**
     * 报修方式
     */
    @Excel(name = "报修方式", width = 15)
    @ApiModelProperty(value = "报修方式")
    private String repairWay;


    /**
     * 系统编号
     */
    @Excel(name = "班组id", width = 15)
    @ApiModelProperty(value = "班组id")
    private String orgId;

    @ApiModelProperty(value = "权限班组集合(后台处理)")
    private List<String> departList;

    @ApiModelProperty(value = "权限系统集合(后台处理)")
    private List<String> systemCodes;

    @ApiModelProperty(value = "权限系站点集合(后台处理)")
    private List<String> stationCodes;

    @ApiModelProperty(value = "用户id(后台处理)")
    private String userId;
}
