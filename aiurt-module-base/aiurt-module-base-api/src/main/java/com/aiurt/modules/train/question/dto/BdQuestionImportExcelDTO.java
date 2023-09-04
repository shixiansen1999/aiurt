package com.aiurt.modules.train.question.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 培训-考卷习题管理使用excel导入时的DTO对象
 * @author 华宜威
 * @date 2023-08-23 09:01:53
 */
@Data
public class BdQuestionImportExcelDTO {
    /**该习题的所属班组*/
    @Excel(name = "所属班组", width = 15)
    @ApiModelProperty(value = "所属班组")
    private String orgCode;
    /**题目类别名称，使用题目类别名称获取题目类别id->categoryId*/
    @Excel(name = "题目类别", width = 15)
    @ApiModelProperty(value = "题目类别")
    private String categoryName;
    /**题目类别，关联习题类别表的主键*/
    @ApiModelProperty(value = "题目类别，关联习题类别表的主键")
    private String categoryId;
    /**题目内容*/
    @Excel(name = "题目", width = 15)
    @ApiModelProperty(value = "题目")
    private String content;
    /**题目类型（1选择题、2多选题、3简答题、4判断题、5填空题）字符串*/
    @Excel(name = "题目类型", width = 15)
    @ApiModelProperty(value = "题目类型（1选择题、2多选题、3简答题、4判断题、5填空题）字符串")
    private String queTypeString;
    @ApiModelProperty(value = "题目类型（1选择题、2多选题、3简答题、4判断题、5填空题）")
    private Integer queType;
    /**答案内容，简答题和填空题用到*/
    @Excel(name = "答案内容", width = 15)
    @ApiModelProperty(value = "答案内容")
    private String answer;
    /**选项*/
    @ExcelCollection(name = "选项")
    @ApiModelProperty(value = "选项")
    private List<BdQuestionOptionImportExcelDTO> bdQuestionOptionImportExcelDTOList;
    /**导入时的错误信息*/
    @ApiModelProperty(value = "选项")
    private String errorMessage;
}
