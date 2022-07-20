package com.aiurt.modules.sparepart.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author km
 * @Date 2021/9/22 19:46
 * @Version 1.0
 */
@Data
public class SparePartInExcel {
    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**物资编号*/
    @Excel(name = "备件编号", width = 15)
    @ApiModelProperty(value = "备件编号")
    private  String  materialCode;

    /**备件名称*/
    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private  String  materialName;


    @ApiModelProperty(value = "备件类型")
    private  Integer  type;
    /**备件类型名称*/
    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty(value = "备件类型")
    private  String  typeName;

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

    /**存放仓库*/
    @Excel(name = "存放仓库", width = 15)
    @ApiModelProperty(value = "存放仓库")
    private  String  warehouseName;
    /**保管人*/
    @Excel(name = "保管人", width = 15)
    @ApiModelProperty(value = "保管人")
    private  String  keeperName;
    /**入库数量*/
    @Excel(name = "入库数量", width = 15)
    @ApiModelProperty(value = "入库数量")
    private  Integer  num;



}
