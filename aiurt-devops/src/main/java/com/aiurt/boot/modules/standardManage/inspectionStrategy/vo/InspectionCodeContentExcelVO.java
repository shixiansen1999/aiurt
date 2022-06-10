package com.aiurt.boot.modules.standardManage.inspectionStrategy.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/24 13:59
 */
@Data
@ExcelTarget("InspectionCodeContentExcelVO")
public class InspectionCodeContentExcelVO implements Serializable {
    @Excel(name = "检修类型",replace = {"周检_1","月检_2","双月检_3","季检_4","半年检_5","年检_6"})
    public Integer type;
    @Excel(name = "检修规范内容")
    public String content;
    @Excel(name = "显示顺序")
    public Integer sortNort;
//    @Excel(name = "安全注意事项标题")
//    public String safetyPrecaution;
    @Excel(name = "是否需要验收确认",replace = {"是_1","否_0"})
    public Integer isReceipt;
    @Excel(name = "更多说明")
    public String remarks;
}
