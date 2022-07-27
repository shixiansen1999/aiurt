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
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Data
@TableName("spare_part_out_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_out_order对象", description="spare_part_out_order")
public class SparePartOutOrder implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
	/**出库的仓库编码*/
	@Excel(name = "出库的仓库编码", width = 15)
    @ApiModelProperty(value = "出库的仓库编码")
    private String warehouseCode;
    /**出库的仓库名称*/
    @Excel(name = "出库的仓库名称", width = 15)
    @ApiModelProperty(value = "出库的仓库名称")
    @TableField(exist = false)
    private String warehouseName;
	/**出库数量*/
	@Excel(name = "出库数量", width = 15)
    @ApiModelProperty(value = "出库数量")
    private Integer num;
	/**确认时间*/
	@Excel(name = "确认时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
	/**确认人ID*/
	@Excel(name = "确认人ID", width = 15)
    @ApiModelProperty(value = "确认人ID")
    private String confirmUserId;
    /**确认人*/
    @Excel(name = "确认人", width = 15)
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    private String confirmName;
	/**申请出库时间*/
	@Excel(name = "申请出库时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "申请出库时间")
    private Date applyOutTime;
	/**申请出库人ID*/
	@Excel(name = "申请出库人ID", width = 15)
    @ApiModelProperty(value = "申请出库人ID")
    private String applyUserId;
    /**申请出库人*/
    @Excel(name = "申请出库人", width = 15)
    @ApiModelProperty(value = "申请出库人")
    @TableField(exist = false)
    private String applyUserName;
	/**备件出库单状态：1待确认、2已确认*/
	@Excel(name = "备件出库单状态：1待确认、2已确认", width = 15)
    @ApiModelProperty(value = "备件出库单状态：1待确认、2已确认")
    @Dict(dicCode = "spare_out_order_status")
    private Integer status;
    /**出库单状态名称*/
    @ApiModelProperty(value = "备件出库单状态：1待确认、2已确认")
    @Dict(dicCode = "spare_out_order_status")
    @TableField(exist = false)
    private String statusName;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
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
