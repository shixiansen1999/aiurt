package com.aiurt.modules.sparepart.dto;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 更换记录
 * @author 报废
 */
@Data
public class SparePartScrapDTO implements Serializable {

    private static final long serialVersionUID = -375597470704551038L;

    /**序号*/
    @TableField(exist = false)
    private String number;


    /**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private String materialCode;

    /**仓库编号*/
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;

    /**出库表id*/
    @ApiModelProperty(value = "出库表id")
    private String outOrderId;


    /**报废数量*/
    @Excel(name = "报废数量", width = 15)
    @ApiModelProperty(value = "报废数量")
    private Integer num;

    /**报废时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报废时间")
    private Date scrapTime;

    /**报废原因*/
    @ApiModelProperty(value = "报废原因")
    private String reason;

    /**报废人*/
    @ApiModelProperty(value = "报废人")
    private String createBy;

    /**状态：1待报损、2待确认、3已确认*/
    @ApiModelProperty(value = "状态：1待报损、2待确认、3已确认")
    private Integer status;

    /**线路编号*/
    @ApiModelProperty(value = "线路编号")
    private String lineCode;

    /**站点编号*/
    @ApiModelProperty(value = "站点编号")
    private String stationCode;

    /**班组id*/
    @ApiModelProperty(value = "班组id")
    private String orgId;

    /**保管人*/
    @ApiModelProperty(value = "保管人")
    private String keepPerson;

    /**报修/报废原因*/
    @ApiModelProperty(value = "报修/报废原因")
    private String scrapReason;

    /**送修时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修时间")
    private Date repairTime;

    /**送修部门*/
    @ApiModelProperty(value = "送修部门")
    private String scrapDepart;

    /**购置日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "购置日期")
    private Date buyTime;


    /**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;


    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**确认时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
    /**确认人ID*/
    @ApiModelProperty(value = "确认人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String confirmId;
    /**确认人*/
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    private String confirmName;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    private String sysOrgCode;
}
