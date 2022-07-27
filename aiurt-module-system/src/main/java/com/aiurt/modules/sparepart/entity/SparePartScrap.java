package com.aiurt.modules.sparepart.entity;

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
    private java.lang.String id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private java.lang.String materialCode;
	/**报废数量*/
	@Excel(name = "报废数量", width = 15)
    @ApiModelProperty(value = "报废数量")
    private java.lang.Integer num;
	/**报损时间*/
	@Excel(name = "报损时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "报损时间")
    private java.util.Date scrapTime;
	/**报损原因*/
	@Excel(name = "报损原因", width = 15)
    @ApiModelProperty(value = "报损原因")
    private java.lang.String reason;
	/**状态：1待报损、2待确认、3已确认*/
	@Excel(name = "状态：1待报损、2待确认、3已确认", width = 15)
    @ApiModelProperty(value = "状态：1待报损、2待确认、3已确认")
    private java.lang.Integer status;
	/**线路编号*/
	@Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;
	/**站点编号*/
	@Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private java.lang.String stationCode;
	/**班组id*/
	@Excel(name = "班组id", width = 15)
    @ApiModelProperty(value = "班组id")
    private java.lang.String orgId;
	/**保管人*/
	@Excel(name = "保管人", width = 15)
    @ApiModelProperty(value = "保管人")
    private java.lang.String keepPerson;
	/**报修/报废原因*/
	@Excel(name = "报修/报废原因", width = 15)
    @ApiModelProperty(value = "报修/报废原因")
    private java.lang.String scrapReason;
	/**送修时间*/
	@Excel(name = "送修时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修时间")
    private java.util.Date repairTime;
	/**送修部门*/
	@Excel(name = "送修部门", width = 15)
    @ApiModelProperty(value = "送修部门")
    private java.lang.String scrapDepart;
	/**购置日期*/
	@Excel(name = "购置日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "购置日期")
    private java.util.Date buyTime;
	/**规定年限*/
	@Excel(name = "规定年限", width = 15)
    @ApiModelProperty(value = "规定年限")
    private java.lang.Integer serviceLife;
	/**使用年限*/
	@Excel(name = "使用年限", width = 15)
    @ApiModelProperty(value = "使用年限")
    private java.lang.Integer useLife;
	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
