package com.aiurt.modules.system.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : sbx
 * @Classname : SysHolidaysImportDTO
 * @Description : TODO
 * @Date : 2023/6/28 18:16
 */
@Data
public class SysHolidaysImportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**开始日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    /**开始日期*/
    @Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private String startDateStr;
    /**结束日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
    /**结束日期*/
    @Excel(name = "结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private String endDateStr;
    /**类型*/
    @ApiModelProperty(value = "类型：1调休，2补班")
    @Dict(dicCode = "holidays_type")
    private Integer type;
    /**类型*/
    @Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型名称：1调休，2补班")
    @Dict(dicCode = "holidays_type")
    private String typeName;
    /**节假日名称*/
    @Excel(name = "节假日名称", width = 15)
    @ApiModelProperty(value = "节假日名称")
    private String name;
    /**导入错误原因*/
    @Excel(name = "导入错误原因", width = 15)
    @ApiModelProperty(value = "导入错误原因")
    private String mistake;
}
