package com.aiurt.modules.train.mistakes.dto.resp;

import com.aiurt.modules.train.question.entity.BdQuestion;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * app-查看错题集详情
 * 由于时间比较赶，数据结构和 app-查看考试详情 的响应格式先一致，没做统一问题格式，希望后续能优化
 *
 * @author 华宜威
 * @date 2023-08-28 09:04:08
 */
@Data
public class BdExamMistakesAppDetailRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**试卷名称*/
    @ApiModelProperty(value = "试卷名称")
    private String name;

    /**原考卷题目数量*/
    @ApiModelProperty(value = "原考卷题目数量")
    private Integer number;

    /**原考卷总分数*/
    @ApiModelProperty(value = "原考卷总分数")
    private Integer score;

    @ApiModelProperty(value = "简答题集合")
    private List<BdQuestion> answerQuestionList;


    @ApiModelProperty(value = "多选题集合")
    private List<BdQuestion> multipleChoiceList;

    @ApiModelProperty(value = "单选题集合")
    private List<BdQuestion> singleChoiceList;

    @ApiModelProperty(value = "错题集状态")
    private Integer state;

}
