package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
public class SparePartScrap implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
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
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
	/**报废数量*/
	@Excel(name = "报废数量", width = 15)
    @ApiModelProperty(value = "报废数量")
    private Integer num;
	/**报废时间*/
	@Excel(name = "报废时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "报废时间")
    private Date scrapTime;
	/**报废原因*/
	@Excel(name = "报废原因", width = 15)
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
}
