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
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Data
@TableName("spare_part_lend")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_lend对象", description="spare_part_lend")
public class SparePartLend implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
	/**借出仓库编号*/
	@Excel(name = "借出仓库编号", width = 15)
    @ApiModelProperty(value = "借出仓库编号")
    private String lendWarehouseCode;
	/**借入仓库编号*/
	@Excel(name = "借入仓库编号", width = 15)
    @ApiModelProperty(value = "借入仓库编号")
    private String backWarehouseCode;
	/**借出时间*/
	@Excel(name = "借出时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "借出时间")
    private Date outTime;
	/**还回时间*/
	@Excel(name = "还回时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "还回时间")
    private Date backTime;
	/**借用人ID*/
	@Excel(name = "借用人ID", width = 15)
    @ApiModelProperty(value = "借用人ID")
    private String lendPerson;
	/**归还人ID*/
	@Excel(name = "归还人ID", width = 15)
    @ApiModelProperty(value = "归还人ID")
    private String backPerson;
	/**借出数量*/
	@Excel(name = "借出数量", width = 15)
    @ApiModelProperty(value = "借出数量")
    private Integer lendNum;
	/**还回数量*/
	@Excel(name = "还回数量", width = 15)
    @ApiModelProperty(value = "还回数量")
    private Integer backNum;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**状态（0-未还 1-已还）*/
	@Excel(name = "状态（0-未还 1-已还）", width = 15)
    @ApiModelProperty(value = "状态（0-未还 1-已还）")
    private Integer status;
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
