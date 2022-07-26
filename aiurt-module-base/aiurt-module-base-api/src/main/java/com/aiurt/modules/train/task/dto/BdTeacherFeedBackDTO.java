package com.aiurt.modules.train.task.dto;

import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author lkj
 */
@Data
@ApiModel(value = "讲师反馈表DTO")
public class BdTeacherFeedBackDTO {
    /**讲师反馈记录*/
    @Excel(name = "讲师反馈记录", width = 15)
    @ApiModelProperty(value = "讲师反馈记录")
    List<BdTrainTeacherFeedbackRecord> bdTrainTeacherFeedbackRecords;
}
