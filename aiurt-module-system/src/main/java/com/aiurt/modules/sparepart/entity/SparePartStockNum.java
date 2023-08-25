package com.aiurt.modules.sparepart.entity;

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
import java.util.Date;

/**
 * @Description: spare_part_stock_num
 * @Author: aiurt
 * @Date:   2023-08-09
 * @Version: V1.0
 */
@Data
@TableName("spare_part_stock_num")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_stock_num对象", description="spare_part_stock_num")
public class SparePartStockNum implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    /**物资编号*/
    @Excel(name = "仓库编号", width = 15)
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
	/**库存数量中的全新的数量*/
	@Excel(name = "库存数量中的全新的数量", width = 15)
    @ApiModelProperty(value = "库存数量中的全新的数量")
    private Integer newNum;
	/**库存数量中的已使用过的数量*/
	@Excel(name = "库存数量中的已使用过的数量", width = 15)
    @ApiModelProperty(value = "库存数量中的已使用过的数量")
    private Integer usedNum;
	/**待报废数量*/
	@Excel(name = "待报废数量", width = 15)
    @ApiModelProperty(value = "待报废数量")
    private Integer scrapNum;
	/**委外送修数量*/
	@Excel(name = "委外送修数量", width = 15)
    @ApiModelProperty(value = "委外送修数量")
    private Integer outsourceRepairNum;
	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
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
}
