package com.aiurt.modules.train.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.task.dto
 * @className: TeacherFeedbackDTO
 * @author: life-0
 * @date: 2022/4/24 16:56
 * @description: TODO
 * @version: 1.0
 */
@Data
public class TranscriptDTO {

    /**人员名字*/
    @Excel(name = "人员名字", width = 15)
    @ApiModelProperty(value = "人员名字")
    private String userName;
    //分数
    @ApiModelProperty(value = "分数")
    private Integer score;

}
