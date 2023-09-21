package com.aiurt.modules.stock.dto.resp;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 出入库记录查询的响应DTO
 *
 * @author 华宜威
 * @date 2023-09-20 11:33:13
 */
@Data
public class MaterialStockOutInRecordRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private java.lang.String materialCode;
    /**出库/入库的仓库编码*/
    @ApiModelProperty(value = "出库/入库的仓库编码")
    private java.lang.String warehouseCode;
    /**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    private java.lang.String sysOrgCode;
    /**出库/入库数量*/
    @ApiModelProperty(value = "出库/入库数量")
    private java.lang.Integer num;
    /**确认时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private java.util.Date confirmTime;
    /**确认人ID*/
    @ApiModelProperty(value = "确认人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private java.lang.String confirmUserId;
    /**出库/入库单的id*/
    @ApiModelProperty(value = "出库/入库单的id")
    private java.lang.String orderId;
    /**出库/入库单的code*/
    @ApiModelProperty(value = "出库/入库单的code")
    private java.lang.String orderCode;
    /**备件出库单状态：1待确认、2已确认*/
    @ApiModelProperty(value = "备件出库单状态：1待确认、2已确认")
    private java.lang.Integer status;
    /**领料单表ID*/
    @ApiModelProperty(value = "领料单表ID")
    private java.lang.String materialRequisitionId;
    /**领料单表code*/
    @ApiModelProperty(value = "领料单表code")
    private java.lang.String materialRequisitionCode;
    /**记录类型（3三级库出入库，2二级库出入库）*/
    @ApiModelProperty(value = "记录类型（3三级库出入库，2二级库出入库）")
    private java.lang.Integer materialRequisitionType;
    /**入库还是出库，1入库 2出库*/
    @ApiModelProperty(value = "入库还是出库，1入库 2出库")
    private java.lang.Integer isOutIn;
    /**出入库类型:普通出库、借出出库、归还入库等，具体看数据字典*/
    @ApiModelProperty(value = "出入库类型:普通出库、借出出库、归还入库等，具体看数据字典")
    @Dict(dicCode = "stock_out_in_type")
    private java.lang.Integer outInType;
    /**库存结余*/
    @ApiModelProperty(value = "库存结余")
    private java.lang.Integer balance;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;


}
