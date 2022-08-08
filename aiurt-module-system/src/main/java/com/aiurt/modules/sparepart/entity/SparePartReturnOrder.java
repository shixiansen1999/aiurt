package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Data
@TableName("spare_part_return_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_return_order对象", description="spare_part_return_order")
public class SparePartReturnOrder implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**序号*/
    @Excel(name = "序号", width = 15)
    @TableField(exist = false)
    private String number;
    /**状态名称*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态：1待确认、2已确认")
    @TableField(exist = false)
    private String statusName;
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
    /**出库表id*/
    @ApiModelProperty(value = "出库表id")
    private String outOrderId;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
	/**退入的仓库编号*/
    @ApiModelProperty(value = "退入的仓库编号")
    private String warehouseCode;
    /**退入的仓库名称*/
    @Excel(name = "退入仓库", width = 15)
    @ApiModelProperty(value = "退入的仓库名称")
    @TableField(exist = false)
    private String warehouseName;
	/**退库数量*/
	@Excel(name = "退库数量", width = 15)
    @ApiModelProperty(value = "退库数量")
    private Integer num;
    /**退库时间*/
    @Excel(name = "退库时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "退库时间")
    private Date returnTime;
    /**退库人*/
    @Excel(name = "退库人", width = 15)
    @ApiModelProperty(value = "退库人")
    @TableField(exist = false)
    private String userName;
	/**班组id*/
    @ApiModelProperty(value = "班组id")
    private String orgId;
	/**备注*/
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**退库人ID*/
    @ApiModelProperty(value = "退库人ID")
    private String userId;
	/**状态：1待确认、2已确认*/
    @ApiModelProperty(value = "状态：1待确认、2已确认")
    private Integer status;
	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
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
    private  String  majorCode;
    /**子系统code*/
    @ApiModelProperty(value = "子系统code")
    @TableField(exist = false)
    private  String  systemCode;
    /**物资分类code*/
    @ApiModelProperty(value = "物资分类code")
    @TableField(exist = false)
    private  String  baseTypeCode;

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
    /**确认时间*/
    @Excel(name = "确认时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
    /**确认人ID*/
    @Excel(name = "确认人ID", width = 15)
    @ApiModelProperty(value = "确认人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String confirmId;
    /**确认人*/
    @Excel(name = "确认人", width = 15)
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    private String confirmName;

}
