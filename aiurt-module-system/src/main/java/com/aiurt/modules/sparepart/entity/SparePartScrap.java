package com.aiurt.modules.sparepart.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
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
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Data
@TableName("spare_part_scrap")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_scrap对象", description="spare_part_scrap")
public class SparePartScrap extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**序号*/
    @Excel(name = "序号", width = 15)
    @TableField(exist = false)
    private String number;
    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private  String  majorName;
    /**子系统名称*/
    @Excel(name = "所属子系统", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private  String  systemName;
    /**物资分类*/
    @Excel(name = "物资分类", width = 15)
    @ApiModelProperty(value = "物资分类名称")
    @TableField(exist = false)
    private  String  baseTypeCodeName;
    /**物资类型名称*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型名称")
    @TableField(exist = false)
    private  String  typeName;
    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    @TableField(exist = false)
    private String materialName;
    /**仓库编号*/
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
    /**出库表id*/
    @ApiModelProperty(value = "出库表id")
    private String outOrderId;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
	/**处置数量*/
	@Excel(name = "处置数量", width = 15)
    @ApiModelProperty(value = "处置数量")
    private Integer num;
	/**处置时间*/
	@Excel(name = "处置时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "处置时间")
    private Date scrapTime;
	/**处置原因*/
	@Excel(name = "处置原因", width = 15)
    @ApiModelProperty(value = "处置原因")
    private String reason;
    /**申请处置人*/
    @Excel(name = "申请处置人", width = 15)
    @ApiModelProperty(value = "申请处置人")
    private String createBy;
	/**备件处置单状态：1待处理、2已报废、3已报损*/
    @ApiModelProperty(value = "备件处置单状态：1待处理、2已报废、3已报损")
    private Integer status;
    @Excel(name = "处置方式", width = 15)
    @ApiModelProperty(value = "备件处置方式：0报损、1报废")
    @Dict(dicCode = "spare_handle_way")
    private Integer handleWay;
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
	/**规定年限*/
    @ApiModelProperty(value = "规定年限")
    private Integer serviceLife;
	/**使用年限*/
    @ApiModelProperty(value = "使用年限")
    private Integer useLife;
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
    /**ids*/
    @ApiModelProperty(value = "ids")
    @TableField(exist = false)
    private List<String> ids;
    /**所属专业code*/
    @ApiModelProperty(value = "专业名称code")
    @TableField(exist = false)
    @MajorFilterColumn
    private  String  majorCode;

    /**子系统code*/
    @ApiModelProperty(value = "子系统code")
    @TableField(exist = false)
    @SystemFilterColumn
    private  String  systemCode;

    /**物资分类code*/
    @ApiModelProperty(value = "物资分类code")
    @TableField(exist = false)
    private  String  baseTypeCode;

    /**物资类型*/
    @ApiModelProperty(value = "类型")
    @TableField(exist = false)
    private  Integer  type;

    /**规格型号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "规格型号")
    private String specifications;
    /**单位*/
    @ApiModelProperty(value = " 单位")
    @TableField(exist = false)
    private String unit;
    /**生产厂商*/
    @ApiModelProperty(value = "生产厂商名称")
    @TableField(exist = false)
    private String manufactorCodeName;
    /**单价(元)*/
    @ApiModelProperty(value = " 单价")
    @TableField(exist = false)
    private String price;
    /** 状态名称*/
    @ApiModelProperty(value = "状态名称")
    @TableField(exist = false)
    private String statusName;
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

    @Excel(name = "存放位置", width = 15)
    @ApiModelProperty(value = "存放位置")
    private String location;

    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;

    @Excel(name = "故障单编号", width = 15)
    @ApiModelProperty(value = "故障单编号")
    private String faultCode;

    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @TableField(exist = false)
    private String symptoms;

    @Excel(name = "备件送修状态", width = 15)
    @ApiModelProperty(value = "备件送修状态：1待返修、2已返修、3已验收")
    private String repairStatus;

    @Excel(name = "序列号", width = 15)
    @ApiModelProperty(value = "序列号")
    private String serialNumber;

    @Excel(name = "返回时间", width = 15)
    @ApiModelProperty(value = "返回时间")
    private Date returnTime;

    @Excel(name = "负责人", width = 15)
    @ApiModelProperty(value = "负责人")
    @TableField(exist = false)
    private String responsibleUserName;

    @Excel(name = "送修经办人", width = 15)
    @ApiModelProperty(value = "送修经办人")
    @TableField(exist = false)
    private String manageUserName;
}
