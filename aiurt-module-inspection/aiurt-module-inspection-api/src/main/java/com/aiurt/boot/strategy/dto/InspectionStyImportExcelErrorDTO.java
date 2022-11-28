package com.aiurt.boot.strategy.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/22 11:56
 **/
@Data
public class InspectionStyImportExcelErrorDTO {
    /**
     * 年份
     */
    @Excel(name = "年份", width = 10, needMerge = true)
    @ApiModelProperty(value = "年份")
    private Integer year;
    /**
     * 策略名称
     */
    @Excel(name = "策略名称", width = 20, needMerge = true)
    @ApiModelProperty(value = "策略名称")
    private String name;
    /**
     * 站点名称
     */
    @Excel(name = "站点名称", width = 25, needMerge = true)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**
     * 组织机构
     */
    @Excel(name = "组织机构", width = 25, needMerge = true)
    @ApiModelProperty(value = "组织机构")
    private String orgName;
    /**
     * 检修周期类型
     */
    @Excel(name = "检修周期类型", width = 15, needMerge = true, replace = {"周检_0", "月检_1", "双月检_2", "季检_3", "半年检_4", "年检_5", "无_null"})
    @ApiModelProperty(value = "检修周期类型")
    private Integer type;
    /**
     * 是否需要审核：0否 1是
     */
    @Excel(name = "是否需要审核", width = 15, needMerge = true, replace = {"否_0", "是_1", "无_null"})
    @ApiModelProperty(value = "是否需要审核")
    private Integer isConfirm;
    /**
     * 是否需要验收：0否 1是
     */
    @Excel(name = "是否需要验收", width = 15, needMerge = true, replace = {"否_0", "是_1", "无_null"})
    @ApiModelProperty(value = "是否需要验收")
    private Integer isReceipt;

    /**
     * 作业类型（A1不用计划令,A2,A3,B1,B2,B3）
     */
    @Excel(name = "作业类型", width = 15, needMerge = true, replace = {"A1_1", "A2_2", "A3_3", "B1_4", "B2_5", "B3_6", "C1_7", "C2_8", "C3_9", "无_null"})
    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    private Integer workType;
    /**
     * 是否委外，0否1是
     */
    @Excel(name = "是否委外", width = 15, needMerge = true, replace = {"否_0", "是_1", "无_null"})
    @ApiModelProperty(value = "是否委外，0否1是")
    private Integer isOutsource;

    /**
     * 检修策略错误原因
     */
    @Excel(name = "检修策略错误原因", width = 15)
    @ApiModelProperty(value = "检修策略错误原因")
    @TableField(exist = false)
    private String InspectionStyErrorReason;

    @ExcelCollection(name = "检修标准")
    @ApiModelProperty(value = "检修标准")
    List<InspectionImportExcelErrorDTO> inspectionExcelDTOList = new ArrayList<>();
}
