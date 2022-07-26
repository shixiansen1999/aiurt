package com.aiurt.modules.train.exam.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 考试成绩录入
 * @author zwl
 */
@Data
public class BdAchievementDTO {

    @ApiModelProperty("用户id")
    private  String  userId;

    @ApiModelProperty("得分")
    private Integer score;

    @ApiModelProperty("是否及格 （1及格，0不及格）")
    private Integer isPass;

    @ApiModelProperty("试卷id")
    private String examPaperId;

    @ApiModelProperty("任务id")
    private String trainTaskId;

    @ApiModelProperty("考试记录id")
    private String recordId;

    @ApiModelProperty("考试类别")
    private Integer examClassify;
    /**考试状态*/
    @Excel(name = "考试状态（0：未考试，1：考试中，2：已考试）", width = 15)
    @ApiModelProperty(value = "考试状态（）")
    @Dict(dicCode = "exam_state")
    private String examState;
}
