package com.aiurt.boot.record.vo;

import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 盘点结果记录VO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedAssetsCheckRecordVO extends FixedAssetsCheckRecord {
    /**
     * 盘盈(+)/盘亏(-)
     */
    @ApiModelProperty(value = "盘盈(+)/盘亏(-)")
    @TableField(exist = false)
    private java.lang.String profitAndLoss;
    /**
     * 已使用年限
     */
    @ApiModelProperty(value = "已使用年限")
    @TableField(exist = false)
    private java.lang.Integer usefulLife;
    /**
     * 账面原值
     */
    @ApiModelProperty(value = "账面原值")
    @TableField(exist = false)
    private java.math.BigDecimal assetOriginal;
    /**
     * 资产分类名称
     */
    @Excel(name = "资产分类名称", width = 15)
    @ApiModelProperty(value = "资产分类名称")
    private java.lang.String categoryName;
    /**
     * 存放地点名称
     */
    @Excel(name = "存放地点名称", width = 15)
    @ApiModelProperty(value = "存放地点名称")
    private java.lang.String locationName;
    /**
     * 组织机构名称
     */
    @Excel(name = "组织机构名称", width = 15)
    @ApiModelProperty(value = "组织机构名称")
    private java.lang.String orgName;
}
