package com.aiurt.modules.stock.dto.req;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 出入库记录查询的请求DTO
 *
 * @author 华宜威
 * @date 2023-09-20 11:38:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MaterialStockOutInRecordReqDTO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**记录类型（3三级库出入库，2二级库出入库）*/
    @ApiModelProperty(value = "记录类型（3三级库出入库，2二级库出入库）")
    private java.lang.Integer materialRequisitionType;

    /**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;

    /**存放仓库编号*/
    @ApiModelProperty(value = "存放仓库编号")
    private  String  warehouseCode;

    /**入库还是出库，1入库 2出库*/
    @ApiModelProperty(value = "入库还是出库，1入库 2出库")
    private java.lang.Integer isOutIn;

    /**出入库类型:普通出库、借出出库、归还入库等，具体看数据字典*/
    @ApiModelProperty(value = "出入库类型:普通出库、借出出库、归还入库等，具体看数据字典")
    private java.lang.Integer outInType;

    /**搜索开始时间->确认时间大于等于的时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "搜索开始时间->确认时间大于等于的时间")
    private java.util.Date searchBeginTime;

    /**搜索结束时间->确认时间小于等于的时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "搜索开始时间->确认时间大于等于的时间")
    private java.util.Date searchEndTime;
}
