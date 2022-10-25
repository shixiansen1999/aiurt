package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description 字典值类
 * @Author scott
 * @Date 2019-4-20
 * @Version V1.0
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
