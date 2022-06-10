package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 二级库库存信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Data
@TableName("stock_level2")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="stock_level2对象", description="二级库库存信息")
public class StockLevel2 {

	/**主键自增id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键自增id")
	private Long id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
	private String materialCode;
	/**物资名称*/
	@Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
	private String materialName;
	/**物资类型（1：非生产类型 2：生产类型）*/
	@Excel(name = "物资类型（1：非生产类型 2：生产类型）", width = 15)
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
	private Integer type;
	/**规格&型号*/
	@Excel(name = "规格&型号", width = 15)
    @ApiModelProperty(value = "规格&型号")
	private String specifications;
	/**原产地*/
	@Excel(name = "原产地", width = 15)
    @ApiModelProperty(value = "原产地")
	private String countryOrigin;
	/**生产商*/
	@Excel(name = "生产商", width = 15)
    @ApiModelProperty(value = "生产商")
	private String manufacturer;
	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private String brand;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
	private String unit;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
	private Integer num;
	/**仓库编号*/
	@Excel(name = "仓库编号", width = 15)
    @ApiModelProperty(value = "仓库编号")
	private String warehouseCode;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
    @ApiModelProperty(value = "仓库名称")
	private String warehouseName;
	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
	@TableLogic
	private Integer delFlag;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;

	/**入库时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "入库时间")
	private  Date  stockInTime;

}
