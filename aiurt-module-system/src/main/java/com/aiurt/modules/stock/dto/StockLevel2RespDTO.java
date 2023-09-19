package com.aiurt.modules.stock.dto;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 二级库库存信息分类列表查询的返回DTO
 *
 * @author 华宜威
 * @date 2023-09-19 17:13:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="二级库库存信息分类列表查询的返回DTO", description="二级库库存信息分类列表查询的返回DTO")
public class StockLevel2RespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键id*/
    @ApiModelProperty(value = "主键id")
    private  String  id;

    /**专业名称*/
    @ApiModelProperty(value = "专业名称")
    private  String  majorName;

    /**所属子系统*/
    @ApiModelProperty(value = "所属子系统")
    private  String  systemName;

    /**机构编码*/
    @ApiModelProperty(value = "机构编码")
    @DeptFilterColumn
    private String orgCode;

    /**物资分类编码*/
    @ApiModelProperty(value = "物资分类编码")
    private  String  baseTypeCode;

    /**物资分类编码分级手动翻译*/
    @ApiModelProperty(value = "物资分类编码分级手动翻译")
    private  String  baseTypeCodeName;

    /**物资编号*/
    @ApiModelProperty(value = "物资编号")
    @Dict(dictTable ="material_base",dicText = "name",dicCode = "code")
    private  String  materialCode;

    /**物资名称*/
    @ApiModelProperty(value = "物资名称")
    private  String  materialName;

    /**物资类型名称*/
    @ApiModelProperty(value = "物资类型名称")
    private  String  typeName;

    /**存放仓库*/
    @ApiModelProperty(value = "存放仓库")
    private  String  warehouseName;

    /**所属组织机构*/
    @ApiModelProperty(value = "所属组织机构")
    private  String  organizationName;

    /**存放仓库编号*/
    @ApiModelProperty(value = "存放仓库编号")
    @Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private  String  warehouseCode;

    /**数量*/
    @ApiModelProperty(value = "数量")
    private  Integer  num;

    /**专业编码*/
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
    @MajorFilterColumn
    private  String  majorCode;

    /**子系统编号*/
    @ApiModelProperty(value = "子系统编号")
    @Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
    @SystemFilterColumn
    private  String  systemCode;

    /**单位名称*/
    @ApiModelProperty(value = " 单位名称")
    private  String  unitName;

    /**入库时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "入库时间")
    private  java.util.Date  stockInTime;

    /**入库时间*/
    @ApiModelProperty(value = "表格用时间")
    private  String  stockInTimeExcel;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrices;

    @ApiModelProperty(value = "一级库数量")
    private Integer oneLevelNum;

    @ApiModelProperty(value = "一级库存放地点")
    private String oneLevelWarehouseCode;

    @ApiModelProperty(value = "一级库总价")
    private String oneLevelTotalPrices;

    @ApiModelProperty(value = "厂家/品牌")
    @Dict(dictTable ="cs_manufactor",dicText = "name",dicCode = "id")
    private String manufactorId;

    @ApiModelProperty(value = "技术参数")
    private String technicalParameter;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private  String  remark;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private  String  createBy;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private  String  updateBy;

    /**支持专业、子系统、物资三种一起模糊查询的物资分类条件*/
    @ApiModelProperty(value = "支持专业、子系统、物资三种一起模糊查询的物资分类条件")
    private  String  selectMaterialType;

    /**物资类型*/
    @ApiModelProperty(value = "物资类型")
    @Dict(dicCode = "material_type")
    private  String  type;

    /**物资类型*/
    @ApiModelProperty(value = "规格型号")
    private  String  specifications;

    /**组织机构id*/
    @ApiModelProperty(value = "组织机构id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private  String  organizationId;

    /**单位*/
    @ApiModelProperty(value = " 单位")
    @Dict(dicCode = "materian_unit")
    private  String  unit;

    /**分类无层级*/
    @ApiModelProperty(value = " 分类无层级")
    private  String  baseType;

    @ApiModelProperty(value = "物资分类查询")
    private String allCode;

    @Excel(name = "最低库存值")
    @ApiModelProperty(value = "最低库存值")
    private Integer minimumStock;
}
