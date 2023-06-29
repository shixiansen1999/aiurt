package com.aiurt.modules.sparepart.entity.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 备件仓库管理，导入excel对应的VO类
 * @author 华宜威
 * @date 2023-06-28 15:22:40
 */
@Data
public class SparePartStockInfoImportExcelVO {

    /**备件仓库名称*/
    @Excel(name = "备件仓库名称", width = 15)
    @ApiModelProperty(value = "备件仓库名称")
    private String warehouseName;

    /**备件仓库编号*/
    @Excel(name = "备件仓库编号", width = 15)
    @ApiModelProperty(value = "备件仓库编号")
    private String warehouseCode;

    /**所属组织机构编码*/
    @ApiModelProperty(value = "所属组织机构编码")
    @Excel(name = "所属组织机构编码", width = 15)
    private String orgCode;

    /**组织id*/
    @ApiModelProperty(value = "组织id")
    private String organizationId;

    /**备件仓库位置*/
    @Excel(name = "备件仓库位置", width = 15)
    @ApiModelProperty(value = "备件仓库位置")
    private String warehousePosition;

    /**备件仓库状态字符串，为导入检测所准备：1启用、2停用*/
    @Excel(name = "备件仓库状态", width = 15)
    @ApiModelProperty(value = "备件仓库状态：1启用、2停用")
    private String warehouseStatusString;

    /**备件仓库状态：1启用、2停用*/
    @ApiModelProperty(value = "数据字典：warehouse_status ，备件仓库状态：1启用、2停用")
    private Integer warehouseStatus;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**数据验证时的错误信息*/
    @Excel(name = "导入失败原因", width = 15)
    @ApiModelProperty(value = "数据验证时的错误信息")
    private String errorMessage;

}

