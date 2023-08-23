package com.aiurt.modules.train.question.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 训-考卷习题管理使用excel导入时，选择题、判断题选项的DTO对象
 * @author 华宜威
 * @date 2023-08-23 09:29:53
 */
@Data
public class BdQuestionOptionImportExcelDTO {
    /**选项内容*/
    @Excel(name = "选项内容", width = 15)
    @ApiModelProperty(value = "选项内容")
    private String content;
    /**是否是答案的字符串*/
    @Excel(name = "是否是答案", width = 15)
    @ApiModelProperty(value = "是否是答案")
    private String isRightString;
    /**是否是答案（是否正确） 正确1 错误0*/
    @Excel(name = "是否正确 正确1 错误0", width = 15)
    @ApiModelProperty(value = "是否正确 正确1 错误0")
    private Integer isRight;
    /**选项排序号*/
    @ApiModelProperty(value = "选项排序号")
    private Integer isort;
}
