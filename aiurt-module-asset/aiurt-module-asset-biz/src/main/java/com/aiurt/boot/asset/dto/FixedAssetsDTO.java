package com.aiurt.boot.asset.dto;

import com.aiurt.boot.check.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
public class FixedAssetsDTO extends DictEntity  {

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**资产名称*/
	@Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
    private String assetCode;
	/**资产名称*/
	@Excel(name = "资产名称", width = 15)
    @ApiModelProperty(value = "资产名称")
    private String assetName;
	/**资产分类编码*/
	@Excel(name = "资产分类编码", width = 15)
    @ApiModelProperty(value = "资产分类编码")
    @Dict(dictTable = "fixed_assets_category", dicText = "category_name", dicCode = "category_code")
    private String categoryCode;
	/**使用组织机构编码*/
	@Excel(name = "使用组织机构编码", width = 15)
    @ApiModelProperty(value = "使用组织机构编码")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private String orgCode;
	/**账面数量*/
	@Excel(name = "账面数量", width = 15)
    @ApiModelProperty(value = "账面数量")
    private Integer number;
	/**存放地点编码*/
	@Excel(name = "存放地点编码", width = 15)
    @ApiModelProperty(value = "存放地点编码")
    private String location;
	/**存放地点*/
    @ApiModelProperty(value = "存放地点")
    private String locationName;
	/**存放地点编码*/
    @ApiModelProperty(value = "线路及站点code")
    private List<String> lineStations;
    /**存放位置是否是站点*/
    @ApiModelProperty(value = "存放位置是否是站点")
    private Boolean isLine;
	/**责任人ID*/
    @ApiModelProperty(value = "责任人ID")
    private String responsibilityId;
	/**责任人*/
	@Excel(name = "责任人ID", width = 15)
    @ApiModelProperty(value = "责任人ID")
    private String responsibilityName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specification;
	/**计量单位(1个、2栋、3台)*/
	@Excel(name = "计量单位(1个、2栋、3台)", width = 15)
    @ApiModelProperty(value = "计量单位(1个、2栋、3台)")
    @Dict(dicCode = "materian_unit")
    private Integer units;
	/**房产证号*/
	@Excel(name = "房产证号", width = 15)
    @ApiModelProperty(value = "房产证号")
    private String houseNumber;
	/**建成/购置时间*/
	@Excel(name = "建成/购置时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "建成/购置时间")
    private java.util.Date buildBuyDate;
	/**建筑面积*/
	@Excel(name = "建筑面积", width = 15)
    @ApiModelProperty(value = "建筑面积")
    private java.math.BigDecimal coveredArea;
	/**启用状态(0停用、1启用)*/
	@Excel(name = "启用状态(0停用、1启用)", width = 15)
    @ApiModelProperty(value = "启用状态(0停用、1启用)")
    @Dict(dicCode = "fixed_assets_status")
    private Integer status;
	/**折旧年限*/
	@Excel(name = "折旧年限", width = 15)
    @ApiModelProperty(value = "折旧年限")
    private String depreciableLife;
	/**使用年限*/
	@Excel(name = "使用年限", width = 15)
    @ApiModelProperty(value = "使用年限")
    private String durableYears;
	/**开始使用日期*/
	@Excel(name = "开始使用日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始使用日期")
    private java.util.Date startDate;
	/**账面原值*/
	@Excel(name = "账面原值", width = 15)
    @ApiModelProperty(value = "账面原值")
    private java.math.BigDecimal assetOriginal;
	/**累计折旧*/
	@Excel(name = "累计折旧", width = 15)
    @ApiModelProperty(value = "累计折旧")
    private java.math.BigDecimal accumulatedDepreciation;
    /**房产证号*/
    @Excel(name = "是否能编辑", width = 15)
    @ApiModelProperty(value = "是否能编辑")
    private Boolean isNotEdit;
    /**折旧年限*/
    @Excel(name = "固定资产盘点记录", width = 15)
    @ApiModelProperty(value = "固定资产盘点记录")
    private List<FixedAssetsCheckRecordDTO> recordDTOList;
    /**树查询编码*/
    @Excel(name = "树查询编码", width = 15)
    @ApiModelProperty(value = "树查询编码")
    private List<String> treeCode;
}
