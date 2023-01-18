package com.aiurt.boot.check.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FixedAssetsCheckRecordDTO extends DictEntity {
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**盘点人ID*/
    @Excel(name = "盘点人ID", width = 15)
    @ApiModelProperty(value = "盘点人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String checkId;
    /**盘点任务单号*/
    @Excel(name = "盘点任务单号", width = 15)
    @ApiModelProperty(value = "盘点任务单号")
    private java.lang.String inventoryList;
    /**盘点实际开始时间*/
    @Excel(name = "盘点实际开始时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "盘点实际开始时间")
    private java.util.Date actualStartTime;
	/**账面数量*/
	@Excel(name = "账面数量", width = 15)
    @ApiModelProperty(value = "账面数量")
    private Integer number;
    /**审核人ID*/
    @Excel(name = "审核人ID", width = 15)
    @ApiModelProperty(value = "审核人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String auditId;
    /**审核时间*/
    @Excel(name = "审核时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "审核时间")
    private java.util.Date auditIdTime;
    /**实盘数量*/
    @Excel(name = "实盘数量", width = 15)
    @ApiModelProperty(value = "实盘数量")
    private java.lang.Integer actualNumber;
    /**盘盈(+)/盘亏(-)*/
    @Excel(name = "盘盈(+)/盘亏(-)", width = 15)
    @ApiModelProperty(value = "盘盈(+)/盘亏(-)")
    private java.lang.Integer actualSurplusLoss;
    /**自用资产数量*/
    @Excel(name = "自用资产数量", width = 15)
    @ApiModelProperty(value = "自用资产数量")
    private java.lang.Integer oneselfAssetNumber;
    /**他用资产数量*/
    @Excel(name = "他用资产数量", width = 15)
    @ApiModelProperty(value = "他用资产数量")
    private java.lang.Integer othersAssetNumber;
    /**累计折旧*/
    @Excel(name = "累计折旧", width = 15)
    @ApiModelProperty(value = "累计折旧")
    private java.math.BigDecimal accumulatedDepreciation;
    /**闲置资产数量*/
    @Excel(name = "闲置资产数量", width = 15)
    @ApiModelProperty(value = "闲置资产数量")
    private java.lang.Integer leisureAssetNumber;
    /**闲置资产数量面积*/
    @Excel(name = "闲置资产数量面积", width = 15)
    @ApiModelProperty(value = "闲置资产数量面积")
    private java.math.BigDecimal leisureArea;
    /**资产抵押、质押及担保情况*/
    @Excel(name = "资产抵押、质押及担保情况", width = 15)
    @ApiModelProperty(value = "资产抵押、质押及担保情况")
    private java.lang.String hypothecate;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
}
