package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 委外人员
 */
@Data
public class BelongUnitResult {


    /**字典项文本*/
    @Excel(name = "字典项文本", width = 15)
    @ApiModelProperty(value = "字典项文本")
    private String ItemText;

    /**字典项值*/
    @Excel(name = "字典项值", width = 15)
    @ApiModelProperty(value = "字典项值")
    private String ItemValue;
}
